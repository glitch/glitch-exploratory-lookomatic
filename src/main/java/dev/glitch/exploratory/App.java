package dev.glitch.exploratory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class App {

    @Parameter(names = {"--help"}, description = "Print help message", required = false)
    boolean help = false;

    public static void main(String[] args) {
        App app = new App();
        JCommander jcmdr = JCommander.newBuilder().programName("App").addObject(app).build();
        jcmdr.parse(args);

        if (app.help) {
            jcmdr.usage();
            return;
        }

        System.out.println("Boilerplate project startup");
    }
}
