package dev.glitch.exploratory.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.springframework.data.util.Pair;

import dev.glitch.exploratory.model.RecordPair;

public class CommonUtil {

  /**
   * Create a Stream of randomly generated RecordPair objects
   *
   * @param nElems
   *          Number of elements in the stream
   * @return Stream of RecordPair objects
   */
  public static Stream<RecordPair> getRecordsStream(Long nElems, List<RecordPair> validation, Integer validatePercent) {
    final Random random = new Random();
    final Random sampler = new Random();
    return new Random().ints(nElems).mapToObj(rando -> {
      return new RecordPair(UUID.randomUUID().toString(), String.valueOf(random.nextInt(7)));
    }).peek(r -> {
      if (null != validation && validatePercent > 0 && sampler.nextInt(99) + 1 <= validatePercent) {
        validation.add(r);
      }
    });
  }

  /**
   * Create a Stream of randomly generated RecordPair objects
   *
   * @param nElems
   *          Number of elements in the stream
   * @return Stream of RecordPair objects
   */
  public static Pair<Stream<RecordPair>, List<RecordPair>> getRecordsStreamFixedValidation(Long nElems,
      Boolean withSample) {
    final Random random = new Random();
    final AtomicLong counter = new AtomicLong(0L);
    final List<RecordPair> samples = new ArrayList<>();
    Stream<RecordPair> pairStream = new Random().ints(nElems).mapToObj(rando -> {
      return new RecordPair(UUID.randomUUID().toString(), String.valueOf(random.nextInt(7)));
    }).peek(r -> {
      if (counter.getAndIncrement() < 10L) {
        samples.add(r);
      }
    });
    return Pair.of(pairStream, samples);
  }
}