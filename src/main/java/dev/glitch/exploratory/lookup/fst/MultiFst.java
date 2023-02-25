package dev.glitch.exploratory.lookup.fst;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import dev.glitch.exploratory.lookup.Lookup;
import dev.glitch.exploratory.model.RecordPair;
import dev.glitch.exploratory.util.BatchingIterator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultiFst implements Lookup {

  private final List<SingleFst> multi = new ArrayList<>();
  private final Integer batchSize;

  public MultiFst(Stream<RecordPair> recordStream, Integer batchSize) {
    this.batchSize = batchSize;
    final AtomicInteger counter = new AtomicInteger(1);
    final AtomicBoolean problems = new AtomicBoolean(false);
    // io.vavr.collection.Stream.ofAll(recordStream).grouped(batchSize).forEach(batch
    // -> {
    // SingleFst single = new SingleFst(batch);
    // });

    BatchingIterator.batchedStreamOf(recordStream, this.batchSize).forEach(batch -> {
      log.info("Working on batch {}", counter.getAndIncrement());
      try {
        SingleFst single = new SingleFst(batch.stream());
      } catch (IOException e) {
        log.error("");
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });;
  }

  @Override
  public Long contains(String id) {
    throw new UnsupportedOperationException("Unimplemented method 'contains'");
  }

  @Override
  public Long getSize() {
    throw new UnsupportedOperationException("Unimplemented method 'getSize'");
  }

}
