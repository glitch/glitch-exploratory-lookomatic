package dev.glitch.exploratory.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RecordPair {
  private final String key;
  private final Long value;
}
