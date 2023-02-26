package dev.glitch.exploratory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import dev.glitch.exploratory.lookup.fst.MultiFst;
import dev.glitch.exploratory.lookup.fst.SingleFst;
import dev.glitch.exploratory.model.RecordPair;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class App {

  @Parameter(names = {"--help"}, description = "Print help message", required = false)
  boolean help = false;

  @Parameter(names = {"--total"}, description = "Number of elements for testing", required = false)
  Long total = 0L;

  @Parameter(names = {"--batch"}, description = "Number of elements per fst", required = true)
  Integer batch = 1000;

  @Parameter(names = {
      "--validatePercent"}, description = "Percentage of stream to use for validation (ballpark, based on Random)", required = false)
  Integer validatePercent = 0;

  @Parameter(names = {"--query"}, description = "Number of query lookups to test Looup throughput", required = false)
  Long queryLookups = 0L;

  public static void main(String[] args) throws Exception {
    App app = new App();
    JCommander jcmdr = JCommander.newBuilder().programName("App").addObject(app).build();
    jcmdr.parse(args);

    if (app.help) {
      jcmdr.usage();
      return;
    }

    log.info("Boilerplate project startup");
    streamExample(app);
  }

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
      return new RecordPair(UUID.randomUUID().toString(), Long.valueOf(random.nextInt(7)));
    }).peek(r -> {
      if (null != validation && validatePercent > 0 && sampler.nextInt(99) + 1 <= validatePercent) {
        validation.add(r);
      }
    });
  }

  public static void streamExample(App app) throws Exception {
    List<RecordPair> validation = (app.validatePercent > 0) ? new ArrayList<>() : null;
    Stream<RecordPair> recordStream = getRecordsStream(app.total, validation, app.validatePercent);
    MultiFst multi = new MultiFst(recordStream, app.batch);
    if (null != validation) {
      log.info("Running validation for {} records", validation.size());
      for (RecordPair pair : validation) {
        if (multi.contains(pair.getKey()) == -1L) {
          log.error("Missed record {}", pair.getKey());
          throw new Exception("FST is broken :(");
        }
      }

      for (String id : List.of("one-id", "two-id", "3987", "shouldn't find me")) {
        if (-1L != multi.contains(id)) {
          log.error("Found an FST which should NOT exist", id);
          throw new Exception("FST is broken :(");
        }
      }
    }

    log.info("Manual Garbage Collection");
    System.gc();

    // Test raw lookup throughput
    if (app.queryLookups > 0) {
      log.info("Running query throughput test");
      long startWatch = System.currentTimeMillis();
      getRecordsStream(app.queryLookups, null, 0).unordered().parallel().forEach(r -> {
        multi.contains(r.getKey());
      });
      long ellapsed = (System.currentTimeMillis() - startWatch) / 1000;
      log.info("Query lookups {}, took {}s", app.queryLookups, ellapsed);
    }
  }

  /**
   * Example building FST using Project Reactor / Flux
   *
   * This tends to run out of memory/overflow the heap since the FST needs to sort
   * the incoming stream before building. The technique here with Flux doesn't
   * block and windowing the stream causes all of the batches to be sorted at
   * once. If you have the RAM this should build faster than the BatchingIterator
   * version.
   *
   * @param app
   */
  public static void reactorExample(App app) {
    List<RecordPair> validation = (app.validatePercent > 0) ? new ArrayList<>() : null;
    Flux<RecordPair> origin = Flux
        .fromStream(getRecordsStream(app.total, validation, app.validatePercent).unordered().parallel());
    List<Flux<RecordPair>> listFlux = origin.window(app.batch).collectList().block();

    List<SingleFst> fistList = listFlux.stream().unordered().parallel().map(flux -> {
      Stream<RecordPair> pairStream = flux.toStream();
      try {
        return new SingleFst(pairStream);
      } catch (IOException e) {
        log.error("{}", e);
      }
      return null;
    }).collect(Collectors.toList());

    int i = 1;
    for (SingleFst fst : fistList) {
      // System.err.println(i + ", fst.getSize(): " + fst.getSize());
      log.info("{}, fst.getSize(): {}", i, fst.getSize());
      i += 1;
    }
  }
}
