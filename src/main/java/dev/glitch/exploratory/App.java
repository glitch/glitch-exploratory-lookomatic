package dev.glitch.exploratory;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

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

  public static void main(String[] args) {
    App app = new App();
    JCommander jcmdr = JCommander.newBuilder().programName("App").addObject(app).build();
    jcmdr.parse(args);

    if (app.help) {
      jcmdr.usage();
      return;
    }

    log.info("Boilerplate project startup");
    Flux<RecordPair> origin = Flux.fromStream(getRecordsStream(app.total).unordered().parallel());
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

  public static Stream<RecordPair> getRecordsStream(Long nElems) {
    final Random random = new Random();
    return new Random().ints(nElems).mapToObj(rando -> {
      return new RecordPair(UUID.randomUUID().toString(), Long.valueOf(random.nextInt(7)));
    });
  }
}
