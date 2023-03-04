package dev.glitch.exploratory.lookup.fst;

import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.CharsRefBuilder;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.CharSequenceOutputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.FST.INPUT_TYPE;
import org.apache.lucene.util.fst.FSTCompiler;
import org.apache.lucene.util.fst.Util;

import dev.glitch.exploratory.lookup.Lookup;
import dev.glitch.exploratory.model.RecordPair;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SingleFst implements Lookup {

  // private final FST<Long> fst;
  private final FST<CharsRef> fst;
  private final Long numberElements;

  public SingleFst(Stream<RecordPair> recordStream) throws IOException {
    log.debug("Building single FST");
    // final PositiveIntOutputs outputs = PositiveIntOutputs.getSingleton();
    // final FSTCompiler<Long> compiler = new FSTCompiler<Long>(INPUT_TYPE.BYTE1,
    // outputs);
    final CharSequenceOutputs outputs = CharSequenceOutputs.getSingleton();
    final CharsRefBuilder charsRefBuilder = new CharsRefBuilder();
    final FSTCompiler<CharsRef> compiler = new FSTCompiler<>(INPUT_TYPE.BYTE1, outputs);
    final BytesRefBuilder bytesRefBuilder = new BytesRefBuilder();
    final IntsRefBuilder intsRefBuilder = new IntsRefBuilder();
    final AtomicBoolean problem = new AtomicBoolean(false);
    final AtomicLong counter = new AtomicLong(0L);

    // Input to FST compiler MUST BE SORTED
    recordStream.sorted(Comparator.comparing(RecordPair::getKey)).forEach(r -> {
      try {
        // compiler.add(Util.toIntsRef(bytesRefBuilder.toBytesRef(), intsRefBuilder),
        // r.getValue());
        charsRefBuilder.clear();
        bytesRefBuilder.clear();
        bytesRefBuilder.copyChars(r.getKey());
        charsRefBuilder.append(r.getValue());
        compiler.add(Util.toIntsRef(bytesRefBuilder.toBytesRef(), intsRefBuilder), charsRefBuilder.toCharsRef());
      } catch (IOException e) {
        log.error("Error adding record {} to FSTCompiler, {}", r, e);
        problem.set(true);
      }
      counter.incrementAndGet();
    });
    this.numberElements = counter.get();

    if (problem.get()) {
      // Throw IOException here because we encountred it during stream processing
      throw new IOException("Exception encountered during FST construction");
    }

    this.fst = compiler.compile();
  }

  @Override
  public String contains(String id) throws IOException {
    CharsRef val = (Util.get(this.fst, new BytesRef(id)));
    return (null == val) ? "" : val.toString();
  }

  @Override
  public Long getSize() {
    return this.numberElements;
  }

}
