package dev.glitch.exploratory.util;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.util.Pair;

import dev.glitch.exploratory.lookup.Lookup;
import dev.glitch.exploratory.lookup.fst.MultiFst;
import dev.glitch.exploratory.model.RecordPair;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LookupFactory {

  public static Pair<Lookup, List<RecordPair>> lookupFactory(Long nElems, Integer batchSize, Boolean withSamples)
      throws IOException {
    // Stream<RecordPair> pairStream = CommonUtil.getRecordsStream(nElems,
    // validation, 10);
    Pair<Stream<RecordPair>, List<RecordPair>> pair = CommonUtil.getRecordsStreamFixedValidation(nElems, withSamples);
    MultiFst multi = new MultiFst(pair.getFirst(), batchSize);
    log.debug("{}", pair.getSecond());
    return Pair.of(multi, pair.getSecond());
  }

}
