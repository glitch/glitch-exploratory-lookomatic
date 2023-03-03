package dev.glitch.exploratory.lookup;

public interface Lookup {

  String contains(String id) throws Exception;

  Long getSize();
}
