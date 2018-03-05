package io.github.fedimser.nonolab;


import io.github.fedimser.nonolab.util.BitArray;
import javafx.scene.control.Cell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solver {

    public enum SolveResult {
        SOLVED,
        AMBIGUOUS,
        IMPOSSIBLE
    }

    public enum CellState{
        EMPTY,
        FILLED,
        NOT_DECIDED
    }

    private int width;
    private int height;
    private CellState[][] cells;
    private NonogramDescription descr;

    private List<CellState> curRow;
    private BitArray curRowBits;
    private BitArray curRowFilled;  // True if necessarily filled.
    private BitArray curRowNotEmpty;  // False if necessarily empty.
    private int curRowLength;
    private List<Integer> curDescription;
    private boolean impossible;

    private boolean verbose=false;
    public NonogramSolution goal;

    public Solver() {

    }

    public Solver(NonogramSolution goal) {
        this.goal = goal;
    }

    /**
     * Returns true if row was modified.
     */
    private boolean solveRow(List<CellState> row, List<Integer> descr) {
        if(verbose) {
            StringBuilder sb = new StringBuilder("Solving row: ");
            for (Integer x : descr) {
                sb.append(String.valueOf(x) + " ");
            }
            sb.append("  ");
            for (CellState cs : row) {
                sb.append(String.valueOf(cs.ordinal()));
            }
            System.out.println(sb.toString());
        }

        curRow = row;
        curDescription = descr;
        curRowLength = row.size();
        curRowFilled = new BitArray(curRowLength);
        curRowNotEmpty= new BitArray(curRowLength);
        for(int x=0;x<curRowLength;x++) {
            curRowFilled.setBit(x, true);
        }
        curRowBits = new BitArray(curRowLength);

        if(!solveRowRec(0,0)) {
            impossible = true;
            return false;
        }

        boolean changed = false;
        for(int x=0;x<curRowLength;x++) {
            if(row.get(x)==CellState.NOT_DECIDED) {
                if(curRowFilled.getBit(x)) {
                    row.set(x, CellState.FILLED);
                    changed=true;
                } else if(!curRowNotEmpty.getBit(x)) {
                    row.set(x, CellState.EMPTY);
                    changed = true;
                }
            }
        }

        if(changed && verbose) {
            StringBuilder sb = new StringBuilder("Changed to: ");
            for(CellState cs : row) {
                sb.append(String.valueOf(cs.ordinal()));
            }
            System.out.println(sb.toString());
        }

        return changed;
    }

    /**
     * Recursively builds all filling variants, which correspond to given description
     *   and existing partial filling.
     * @param pos Position of first not determined cell.
     * @param numsUsed How many numbers in description already used.
     * @return True if there is at least one feasible solution.
     */
    private boolean solveRowRec(int pos, int numsUsed) {
        if(numsUsed == curDescription.size()) {
            assert(pos<=curRowLength);
            for(int x=pos; x < curRowLength;x++) {
                if(curRow.get(x) == CellState.FILLED) return false;
            }

            //System.out.println("Rec end: " + curRowBits.toString());

            curRowFilled.andWith(curRowBits);
            curRowNotEmpty.orWith(curRowBits);

            return true;
        }

        int curLen = curDescription.get(numsUsed);

        // Go through all possible starts.
        boolean feasible = false;
        for(int x=pos;x < curRowLength;x++) {
            if (x+curLen>curRowLength) break;

            // Check that it doesn't have empty cells inside.
            boolean canBeFilled = true;
            for(int i=x;i<x+curLen;i++) {
                if(curRow.get(i)==CellState.EMPTY) {
                    canBeFilled = false;
                    break;
                }
            }

            // Check that right after his group cell can be empty.
            if(canBeFilled && x+curLen<curRowLength && curRow.get(x+curLen) == CellState.FILLED) {
                canBeFilled = false;
            }

            if(canBeFilled) {
                for (int i = x; i < x + curLen; i++) curRowBits.setBit(i, true);
                int emptyCell = ((x+curLen==curRowLength)?0:1);
                if (solveRowRec(x + curLen +  emptyCell, numsUsed + 1)) feasible = true;
                for (int i = x; i < x + curLen; i++) curRowBits.setBit(i, false);
            }

            if(curRow.get(x) == CellState.FILLED) break;
        }

        return feasible;
    }

    private boolean solveStep() {
        boolean changed=false;

        // Check all columns.
        for(int x=0;x<width;x++) {
            List<CellState> col = Arrays.asList(cells[x]);
            if(solveRow(col, descr.getColumnDescription(x))) {
                changed = true;
                for(int y=0;y<height;y++)cells[x][y] = col.get(y);
                //checkGoal();
            }
            if(impossible) return false;
        }

        // Check all rows.
        for(int y=0;y<height;y++) {
            List<CellState> row = new ArrayList<CellState>();
            for(int x=0;x<width;x++) {
                row.add(cells[x][y]);
            }
            if(solveRow(row, descr.getRowDescription(y))) {
                changed = true;
                for(int x=0;x<width;x++)cells[x][y] = row.get(x);
                //checkGoal();
            }
            if(impossible) return false;
        }

        return changed;
    }

    public SolveResult solve(NonogramDescription descr) {
        width = descr.getWidth();
        height = descr.getHeight();
        this.descr = descr;
        cells = new CellState[width][height];
        impossible = false;

        for(int x=0;x<width;x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = CellState.NOT_DECIDED;
            }
        }

        while (true) {
            boolean changed = solveStep();
            if(impossible)return SolveResult.IMPOSSIBLE;
            if(!changed) break;
        }

        for(int x=0;x<width;x++) {
            for (int y = 0; y < height; y++) {
                if(cells[x][y] == CellState.NOT_DECIDED) return SolveResult.AMBIGUOUS;
            }
        }


        return SolveResult.SOLVED;
    }

    public NonogramSolution getSolution() {
        return new NonogramSolution(cells);
    }

    private void checkGoal() {
        for(int x=0;x<width;x++) {
            for(int y=0;y<height;y++) {
                if(cells[x][y]!=CellState.NOT_DECIDED) {
                    if ( (cells[x][y]==CellState.FILLED) != goal.getPixel(x,y)) {
                        System.out.println("SPOILED at " + String.valueOf(x) + ","  + String.valueOf(y));
                        System.exit(2);
                    }
                }
            }
        }
    }
}
