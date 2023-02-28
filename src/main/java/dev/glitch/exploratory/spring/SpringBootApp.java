package dev.glitch.exploratory.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
  info = @Info(
    title = "Exploratory Lookup-o-matic!",
    description = "Did you ever want to build a Lookup Service with FSTs (Lucene) or BloomFilters (Guava) backed by Sorted K,V pair compressed files? ME TOO!",
    contact = @Contact(name = "Github", url = "https://github.com/glitch/glitch-exploratory-lookomatic")
  )
)
public class SpringBootApp {

  public static void main(String[] args) {
    SpringApplication.run(SpringBootApp.class, args);
  }
}
