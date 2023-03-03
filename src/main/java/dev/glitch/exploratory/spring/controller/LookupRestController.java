package dev.glitch.exploratory.spring.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.glitch.exploratory.lookup.Lookup;
import dev.glitch.exploratory.model.RecordPair;
import dev.glitch.exploratory.spring.config.LookupConfig;
import dev.glitch.exploratory.spring.config.LookupConfig.Properties;
import dev.glitch.exploratory.util.LookupFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RestControllerAdvice
@RequestMapping("/api/lookup")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Lookup API", description = "Simple Id Lookup Service using Lucene FST & Coming soon BloomFilter + File-based, (K,V) pairs")
public class LookupRestController implements InitializingBean {

  private final LookupConfig lookupConfig;
  private final ObjectMapper mapper = new ObjectMapper();
  private AtomicReference<Lookup> lookup = new AtomicReference<>();
  private AtomicReference<Properties> lookupProps = new AtomicReference<>();

  @Override
  public void afterPropertiesSet() throws Exception {
    Properties props = lookupConfig.getLookups().get("default");
    log.info("Initializing with {}", props);
    Pair<Lookup, List<RecordPair>> p = LookupFactory.lookupFactory(props.getNElems(), props.getBatchSize(), true);
    props.setSamples(p.getSecond());
    this.lookup.set(p.getFirst());
    this.lookupProps.set(props);
  }

  @Operation(description = "Look up the provided id in the current Lookup instance")
  @GetMapping("/{id}")
  public String lookupId(
      @Parameter(required = true, name = "id", description = "Id to lookup") @PathVariable("id") String id) {
    try {
      return this.lookup.get().contains(id);
    } catch (Exception e) {
      return "";
    }
  }

  @Operation(description = "Create a new Lookup instance from a set of pre-configured properties")
  @GetMapping("/createLookup/{lookupSize}")
  public ResponseEntity<String> createLookup(
      @Parameter(required = true, name = "lookupSize", description = "One of <default | small | medium | large>", example = "small") @PathVariable("lookupSize") String lookupSize)
      throws IOException, LookupConfigNotFound, JsonProcessingException {

    LookupConfig.Properties props = lookupConfig.getLookups().get(lookupSize);
    if (null == props) {
      log.error("No configuration found for createLookup/{} ", lookupSize);
      throw new LookupConfigNotFound(String.format("No configuration found for lookupSize: {}", lookupSize));
    } else {
      Pair<Lookup, List<RecordPair>> p = LookupFactory.lookupFactory(props.getNElems(), props.getBatchSize(), true);
      props.setSamples(p.getSecond());
      this.lookup.set(p.getFirst());
      this.lookupProps.set(props);

      log.info("Created lookup {}", this.lookup);
      return ResponseEntity.ok().body(mapper.writeValueAsString(this.lookupProps.get()));
    }
  }

  @Operation(description = "Get Info and Data Samples from the currently active Lookup")
  @GetMapping("/info")
  public ResponseEntity<String> getInfo() throws JsonProcessingException {
    return ResponseEntity.ok().body(mapper.writeValueAsString(this.lookupProps.get()));
  }

  @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No configuration exists for the Lookup you are trying to create")
  public static class LookupConfigNotFound extends Exception {
    public LookupConfigNotFound(String message) {
      super(message);
    }
  }

}
