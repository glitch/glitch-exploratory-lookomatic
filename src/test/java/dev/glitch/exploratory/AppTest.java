package dev.glitch.exploratory;

import java.util.Comparator;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.glitch.exploratory.model.RecordPair;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppTest {

  @Test
  public void shouldAnswerWithTrue() {
    Assertions.assertTrue(true);
  }

  @Test
  public void testFluxCapacitor() throws InterruptedException {
    /*
     * System.err.println("Starting The test"); // List<FluxCapacitor> fcs =
     * java.util.concurrent.
     *
     * Flux<RecordPair> origin = Flux.fromStream(getRecordsStream(10005));
     * List<Flux<RecordPair>> listFlux = origin.window(1000).collectList().block();
     * System.err.println("Size of listFlux" + listFlux.size());
     *
     * List<FluxCapacitor> k = listFlux.stream().map(flux -> { Stream<RecordPair>
     * pairStream = flux.toStream(); FluxCapacitor c = new
     * FluxCapacitor(pairStream); System.err.println("Count: " + c.getCount());
     * return c; }).collect(Collectors.toList()); // List<FluxCapacitor> k =
     * Flux.fromStream(getRecordsStream(97)) // .window(10).doOnN // .map(flux -> {
     * // System.err.println("I'm in the map of flux"); // Stream<RecordPair>
     * pairStream = flux.toStream();
     *
     * // FluxCapacitor c = new FluxCapacitor(pairStream); //
     * System.err.println("Count: " + c.getCount()); // return c; //
     * }).collectList().block();
     *
     * System.err.println("k.size(): " + k.size()); int i = 1; for (FluxCapacitor
     * marty : k) { System.err.println(i + ", FluxCapacitor.getCount(): " +
     * marty.getCount()); i += 1; }
     */
  }

  @Test
  public void testFST() {
    /*
     * Flux<RecordPair> origin =
     * Flux.fromStream(getRecordsStream(1_000_000).unordered().parallel());
     * List<Flux<RecordPair>> listFlux =
     * origin.window(100000).collectList().block();
     * System.err.println("Size of listFlux: " + listFlux.size());
     *
     * List<SingleFst> fistList = listFlux.stream().unordered().parallel().map(flux
     * -> { Stream<RecordPair> pairStream = flux.toStream(); try { return new
     * SingleFst(pairStream); } catch (IOException e) { block e.printStackTrace(); }
     * return null; }).collect(Collectors.toList());
     *
     * int i = 1; for (SingleFst fst : fistList) { System.err.println(i +
     * ", fst.getSize(): " + fst.getSize()); i += 1; }
     */
  }

  public static Stream<RecordPair> getRecordsStream(int nElems) {
    final Random random = new Random();
    return new Random().ints(nElems).mapToObj(rando -> {
      return new RecordPair(UUID.randomUUID().toString(), Long.valueOf(random.nextInt(7)));
    });
  }

  public static class FluxCapacitor {
    private final AtomicLong counter = new AtomicLong(0L);
    public FluxCapacitor(Stream<RecordPair> recStream) {
      System.err.println("I'm in the FluxCapacitor constructor START");
      recStream.sorted(Comparator.comparing(RecordPair::getKey)).forEach(x -> {
        log.info("Look, I'm doing stuff with {}", x);
        System.err.println("Look, I'm doing stuff with " + x);
        counter.incrementAndGet();
      });
      System.err.println("I'm at the FluxCapacitor constructor END");

    }

    public Long getCount() {
      return this.counter.get();
    }
  }
}
