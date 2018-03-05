package io.github.fedimser.nonolab;


import org.junit.jupiter.api.Test;

import java.util.List;

import static java.lang.String.join;
import static org.junit.jupiter.api.Assertions.*;

class NonogramDescriptionTest {

    //First columns, then rows.
    private void testDescription(String nonogram, String cols, String rows) {
        NonogramDescription desc1 = new NonogramDescription(cols, rows);
        NonogramSolution sol = new NonogramSolution(nonogram);
        NonogramDescription desc2 = new NonogramDescription(sol);
        assertEquals(desc1, desc2);
    }

    @Test
    public void canDescribeSolution() {
        Integer[] empty = new Integer[]{};

        testDescription(" ", "", "");
        testDescription("X", "1", "1");
        testDescription("XX", "1;1", "2");
        testDescription("XX\nXX", "2;2", "2;2");
        testDescription("X X\n X \nX X", "1,1;1;1,1", "1,1;1;1,1");
    }

}