package dev.glitch.exploratory.lookup;

public interface Lookup {
    
    Long contains(String id) throws Exception;
    Long getSize();
}
