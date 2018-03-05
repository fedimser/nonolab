package io.github.fedimser.nonolab;

import io.github.fedimser.nonolab.util.WebLoader;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static org.junit.jupiter.api.Assertions.*;

class SolverTest {
    private static final String[] sources = new String[]{
            "https://raw.githubusercontent.com/mikix/nonogram-db/master/db/gnonograms/42.non",
            "https://raw.githubusercontent.com/mikix/nonogram-db/master/db/gnonograms/ubuntu.non",
            "https://raw.githubusercontent.com/mikix/nonogram-db/master/db/gnonograms/blender.non",
            "https://raw.githubusercontent.com/mikix/nonogram-db/master/db/gnonograms/gnome.non",
            "https://raw.githubusercontent.com/mikix/nonogram-db/master/db/gnonograms/kde.non",
            "https://raw.githubusercontent.com/mikix/nonogram-db/master/db/gnonograms/spade.non",
            "https://raw.githubusercontent.com/mikix/nonogram-db/master/db/gnonograms/ubuntu.non",
            "https://raw.githubusercontent.com/mikix/nonogram-db/master/db/gnonograms/wikimedia.non",
            "https://raw.githubusercontent.com/mikix/nonogram-db/master/db/webpbn/1.non",
            "https://raw.githubusercontent.com/mikix/nonogram-db/master/db/webpbn/16.non",
            "https://raw.githubusercontent.com/mikix/nonogram-db/master/db/webpbn/21.non",
            "https://raw.githubusercontent.com/mikix/nonogram-db/master/db/webpbn/26167.non",
            "https://raw.githubusercontent.com/mikix/nonogram-db/master/db/webpbn/529.non",
            "https://raw.githubusercontent.com/mikix/nonogram-db/master/db/webpbn/6.non"
    };


    private void fullTest(String source, Path testDir) throws IOException {
        String name = source.substring(source.lastIndexOf("/")+1);
        System.out.println(name);
        File file = testDir.resolve(name).toFile();

        if(!file.exists()) {
            // Loading <source> from web to <file>.
            String data = WebLoader.loadString(source);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(data);
            writer.close();
        }
        NonogramDescription descr = NonogramDescription.fromFile(file);
        NonogramSolution sol = NonogramSolution.fromFile(file);
        NonogramDescription descr2 = new NonogramDescription(sol); // Explicit description for goal.
        assertTrue(descr.equals(new NonogramDescription(sol)));

        Solver solver = new Solver();
        assertEquals(solver.solve(descr), Solver.SolveResult.SOLVED);
        assertEquals(solver.getSolution(), sol);
        NonogramDrawer.drawAll(sol,testDir, name.substring(0, name.length()-4));
    }

    private Path getTestDir() throws IOException {
        Path testDir = (new java.io.File("").getAbsoluteFile().toPath()).resolve("temp");
        if(!Files.exists(testDir)) {
            Files.createDirectories(testDir);
        }
        System.out.println("Testing directory: " + testDir.toString());
        return testDir;
    }

    @Test
    public void testAll() throws IOException  {
        Path testDir = getTestDir();
        for(String s: sources) {
            fullTest(s, testDir);
        }
    }

}