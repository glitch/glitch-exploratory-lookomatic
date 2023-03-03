package dev.glitch.exploratory.lookup.fst;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.glitch.exploratory.lookup.Lookup;
import dev.glitch.exploratory.model.RecordPair;
import dev.glitch.exploratory.util.BatchingIterator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultiFst implements Lookup {

  private final List<SingleFst> multi;
  private final Integer batchSize;

  public MultiFst(Stream<RecordPair> recordStream, Integer batchSize) throws IOException {
    this.batchSize = batchSize;
    final AtomicInteger counter = new AtomicInteger(1);
    final AtomicBoolean problem = new AtomicBoolean(false);

    this.multi = BatchingIterator.batchedStreamOf(recordStream, this.batchSize).map(batch -> {
      log.info("Working on batch {}", counter.getAndIncrement());
      try {
        return new SingleFst(batch.stream());
      } catch (IOException e) {
        log.error("Exception building single fst, {}", e);
        problem.set(true);
      }
      return null;
    }).collect(Collectors.toList());

    if (problem.get()) {
      throw new IOException("Encounterd an exception building the FSTs");
    }
  }

  @Override
  public String contains(String id) {
    return this.multi.parallelStream().map(fst -> {
      try {
        return fst.contains(id);
      } catch (IOException e) {
      }
      return "";
    }).filter(r -> r.isEmpty()).findAny().orElse("");
  }

  @Override
  public Long getSize() {
    return this.multi.stream().mapToLong(fst -> fst.getSize()).sum();
  }

}
