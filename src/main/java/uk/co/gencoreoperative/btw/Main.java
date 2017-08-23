package uk.co.gencoreoperative.btw;

import java.util.Arrays;
import java.util.List;

import static java.text.MessageFormat.format;

public class Main {

    // Check 1.5.2 versions
    // Check internet connection
    // Check "jar" on command line
    private static List<Check> prerequisites = Arrays.asList(new FileCheck(new MineCraftPathResolver()));

    public static void main(String... args) {
        // Check all prerequisite conditions are met
        final boolean[] failed = {false};
        prerequisites.forEach(c -> {
            boolean check = c.check();
            if (!check) {
                failed[0] = true;
            }
            System.out.println(format("{0} {1}", check ? "✓" : "✗", c.item()));
        });

        if (failed[0]) {
            System.exit(-1);
        }

        // Perform installation.


        // Remove previous BTW installation
        // Download JSON file
        // Download latest BTW installation
        // Copy 1.5.2 to BetterThanWolves
        // Unpack zip and update Jar
        // Update Jar META-INF
        // Copy JSON


        // Signal User
    }
}
