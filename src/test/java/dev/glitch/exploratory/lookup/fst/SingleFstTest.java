package dev.glitch.exploratory.lookup.fst;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.glitch.exploratory.model.RecordPair;

public class SingleFstTest {

  @Test
  void testFst() throws IOException {
    // Save the stream into a list so we can use it for validation
    final List<RecordPair> input = getRecordsStream(10).collect(Collectors.toList());
    SingleFst uut = new SingleFst(input.stream());

    for (RecordPair expected : input) {
      Long actual = uut.contains(expected.getKey());
      Assertions.assertEquals(expected.getValue().longValue(), actual.longValue());
    }

    // Test that we persisted the count
    Assertions.assertEquals(10L, uut.getSize());

    // Test strings which should NOT be in the FST
    for (String nope : List.of("I shouldn't", "be here", "today")) {
      Assertions.assertNull(uut.contains(nope));
    }
  }

  public static Stream<RecordPair> getRecordsStream(int nElems) {
    final Random random = new Random();
    return new Random().ints(nElems).mapToObj(rando -> {
      return new RecordPair(UUID.randomUUID().toString(), Long.valueOf(random.nextInt(7)));
    });
  }
}
