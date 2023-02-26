package dev.glitch.exploratory.spring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.glitch.exploratory.lookup.Lookup;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lookup")
@RequiredArgsConstructor
public class LookupRestController {

  private final Lookup lookup;

  @GetMapping("/{id}")
  public Long lookupId(@Parameter(required = true, name = "id") @PathVariable("id") String id) {
    try {
        return this.lookup.contains(id);
    } catch (Exception e) {
        return -1L;
    }
  }

}
