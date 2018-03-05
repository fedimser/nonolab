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
        IMPOSSIBLE,
        NOT_ATTEMPTED
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
    private SolveResult solveResult;

    private List<CellState> curRow;
    private BitArray curRowBits;
    private BitArray curRowFilled;  // True if necessarily filled.
    private BitArray curRowNotEmpty;  // False if necessarily empty.
    private int curRowLength;
    private List<Integer> curDescription;
    private boolean curRowImpossible;

    private boolean verbose=false;
    private NonogramSolution counterExample;

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
            curRowImpossible = true;
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
            if(curRowImpossible) return false;
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
            if(curRowImpossible) return false;
        }

        return changed;
    }

    public Solver(NonogramDescription descr) {
        this.width = descr.getWidth();
        this.height = descr.getHeight();
        this.descr = descr;
        this.cells = new CellState[width][height];
        this.solveResult = SolveResult.NOT_ATTEMPTED;

        for(int x=0;x<width;x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = CellState.NOT_DECIDED;
            }
        }
    }

    /**
     * Solves nonogram, agreeing with <code>cells</code> and with backtracking.
     */
    private SolveResult solveRec() {
        curRowImpossible = false;
        while (true) {
            boolean changed = solveStep();
            if (curRowImpossible) return SolveResult.IMPOSSIBLE;
            if (!changed) break;
        }

        boolean solved = true;
        int badX=0, badY=0;
        for(int x=0;x<width;x++) {
            for (int y = 0; y < height; y++) {
                if(cells[x][y] == CellState.NOT_DECIDED) {
                    solved = false;
                    badX = x;
                    badY = y;
                    break;
                }
            }
        }

        if (solved) return SolveResult.SOLVED;

        // Go deeper.
        CellState[][] backupCells = cloneCells();
        cells[badX][badY]=CellState.EMPTY;
        if(solveRec()==SolveResult.SOLVED) {
            return SolveResult.SOLVED;
        } else {
          cells = backupCells;
          cells[badX][badY] = CellState.FILLED;
          return solveRec();
        }
    }

    /**
     * Returns solution, if it exists.
     * Otherwise returns null.
     */
    public NonogramSolution solve() {
        if(solveRec() == SolveResult.IMPOSSIBLE) {
            solveResult = SolveResult.IMPOSSIBLE;
            return null;
        } else {
            solveResult = SolveResult.SOLVED;
            return new NonogramSolution(cells);
        }
    }

    private CellState[][] cloneCells() {
        CellState[][] ret = new CellState[width][height];
        for(int x=0;x<width;x++) {
            for(int y=0;y<height;y++) {
                ret[x][y] = cells[x][y];
            }
            //System.arraycopy(cells,0,ret, 0, height);
        }
        return ret;
    }


    private CellState flipState(CellState x) {
        if(x==CellState.FILLED) return CellState.EMPTY;
        else if (x==CellState.EMPTY) return CellState.FILLED;
        else return x;
    }

    public boolean hasUniqueSolution() {
        if(solveResult == SolveResult.NOT_ATTEMPTED) throw new IllegalStateException("Solution haven't been performed.");
        if(solveResult == SolveResult.IMPOSSIBLE) return false;
        if(solveResult == SolveResult.AMBIGUOUS) return false;
        assert (solveResult == SolveResult.SOLVED);

        CellState[][] backupCells = cloneCells();
        boolean unique = true;
        for(int x=0;x<width;x++) {
            for(int y=0;y<height;y++) {
                for(int x1=0;x1<width;x1++)
                    for(int y1=0;y1<height;y1++)
                        cells[x1][y1]=CellState.NOT_DECIDED;
                cells[x][y] = flipState(backupCells[x][y]);

                SolveResult res = solveRec();
                if(res!=SolveResult.IMPOSSIBLE) {
                    counterExample = new NonogramSolution(cells);
                    unique = false;
                }
            }
        }
        cells = backupCells;
        return unique;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public NonogramSolution getCounterExample() {
        return counterExample;
    }
}
