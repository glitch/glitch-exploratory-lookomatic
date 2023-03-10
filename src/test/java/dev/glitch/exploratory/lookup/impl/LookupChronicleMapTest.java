package dev.glitch.exploratory.lookup.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dev.glitch.exploratory.lookup.testutils.LookupTestUtil;
import dev.glitch.exploratory.model.RecordPair;
import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.map.ChronicleMap;

@Slf4j
public class LookupChronicleMapTest {

  @Test
  void testChronicle(@TempDir Path tempDir) throws IOException {
    Path tempFile = Files.createTempFile(tempDir, "chronicle", "test");
    UUID uuid = UUID.randomUUID();

    final Long nElems = 1_000_000L;
    final double samplePercent = 0.001;
    final int sampleSize = (int) (nElems * (samplePercent / 100.0));
    final List<RecordPair> sample = new ArrayList<>(sampleSize);
    log.info("Sample size {}, based on samplePercent {} and nElems {}", sampleSize, samplePercent, nElems);

    ChronicleMap<CharSequence, CharSequence> chronicle = ChronicleMap.of(CharSequence.class, CharSequence.class)
        .name("lookup-map").entries(nElems).averageKey(uuid.toString()).averageValue(uuid.toString())
        .createPersistedTo(tempFile.toFile());

    final AtomicInteger sampleCount = new AtomicInteger(0);
    final Random rng = new Random();
    LookupTestUtil.getRecordsStream(nElems.intValue()).forEach(r -> {
      if (sampleCount.get() < sampleSize && rng.nextLong(nElems) < sampleSize) {
        // log.trace("Sampling {}", r);
        sample.add(r);
        sampleCount.incrementAndGet();
      }
      chronicle.put(r.getKey(), r.getValue());
    });

    log.info("sampled {} records", sample.size());

    // ChronicleMap getUsing(key, reusableValueHolder) works with StringBuilder to
    // avoid generating extra objects.
    // but it also depends on how you plan to use the value.
    final StringBuilder value = new StringBuilder();
    sample.stream().forEach(r -> {
      // CharSequence value = chronicle.get(r.getKey());
      chronicle.getUsing(r.getKey(), value);
      Assertions.assertEquals(r.getValue(), value.toString());
      log.trace("Found record {} in ChronicleMap", new RecordPair(r.getKey(), value.toString()));
    });
    chronicle.close();

  }
}