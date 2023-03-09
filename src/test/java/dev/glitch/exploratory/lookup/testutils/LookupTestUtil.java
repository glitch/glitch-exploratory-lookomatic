package dev.glitch.exploratory.lookup.testutils;

import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import dev.glitch.exploratory.model.RecordPair;

public class LookupTestUtil {
    public static Stream<RecordPair> getRecordsStream(int nElems) {
        final Random random = new Random();
        return new Random().ints(nElems).mapToObj(rando -> {
          return new RecordPair(UUID.randomUUID().toString(), String.valueOf(random.nextInt(7)));
        });
      }
}
