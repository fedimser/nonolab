package io.github.fedimser.nonolab;

import org.junit.jupiter.api.Test;

import static java.lang.String.join;
import static org.junit.jupiter.api.Assertions.*;

class NonogramSolutionTest {

    private void checkString(String repr) {
        assertEquals(repr, (new NonogramSolution(repr)).toString());
    }

    @Test
    public void testToString(){
        checkString(" ");
        checkString("X");
        checkString("       ");
        checkString(" \nX\n \nX");
        checkString("X \n X");
        checkString("XXX\nXXX\nXXX");
        checkString(String.join("\n",
                "X X X X X",
                " X X X X ",
                "X X X X X",
                " X X X X "
        ));
    }




}