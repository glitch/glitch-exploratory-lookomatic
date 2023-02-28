package dev.glitch.exploratory.spring.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import dev.glitch.exploratory.model.RecordPair;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "lookup")
@Data
public class LookupConfig {

  @Value("${default:smal}")
  private String defaultLookup;

  private Map<String, Properties> lookups;

  @Data
  public static class Properties {
    private String lookupType;
    private Integer batchSize;
    private Long nElems;
    private List<RecordPair> samples = new ArrayList<>();
  }
}
