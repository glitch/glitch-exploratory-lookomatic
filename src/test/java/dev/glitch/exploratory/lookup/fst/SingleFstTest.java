package dev.glitch.exploratory.lookup.fst;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.glitch.exploratory.lookup.testutils.LookupTestUtil;
import dev.glitch.exploratory.model.RecordPair;

public class SingleFstTest {

  @Test
  void testFst() throws IOException {
    // Save the stream into a list so we can use it for validation
    final List<RecordPair> input = LookupTestUtil.getRecordsStream(10).collect(Collectors.toList());
    SingleFst uut = new SingleFst(input.stream());

    for (RecordPair expected : input) {
      String actual = uut.contains(expected.getKey());
      Assertions.assertEquals(expected.getValue(), actual);
    }

    // Test that we persisted the count
    Assertions.assertEquals(10L, uut.getSize());

    // Test strings which should NOT be in the FST
    for (String nope : List.of("I shouldn't", "be here", "today")) {
      Assertions.assertEquals("", uut.contains(nope));
    }
  }
}
