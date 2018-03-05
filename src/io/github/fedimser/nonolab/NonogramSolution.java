package io.github.fedimser.nonolab;



import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


public class NonogramSolution {
    public final static char EMPTY_MARKER = ' ';
    public final static char FILL_MARKER = 'X';


    private int width;
    private int height;
    private Boolean[][] pixels;

    public NonogramSolution(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new Boolean[width][height];
    }

    public NonogramSolution(Boolean[][] pixels) {
        this.width = pixels.length;
        this.height = pixels[0].length;
        this.pixels = pixels;
    }

    public NonogramSolution(Solver.CellState[][] cells) {
        this.width = cells.length;
        this.height = cells[0].length;
        this.pixels = new Boolean[width][height];
        for(int x=0;x<width;x++) {
            for (int y = 0; y < height; y++) {
                pixels[x][y] = (cells[x][y]== Solver.CellState.FILLED);
            }
        }
    }

    /**
     * Constructor from string representation.
     */
    public NonogramSolution(String picture) throws IllegalArgumentException {
        String[] lines = picture.split("\n");
        this.height = lines.length;
        this.width = lines[0].length();
        this.pixels = new Boolean[width][height];
        for(int y = 0;y<height;y++){
            if(lines[y].length() != width) {
                throw new IllegalArgumentException("Different size of lines.");
            }
            for(int x=0;x<width;x++) {
                char c = lines[y].charAt(x);
                if(c == EMPTY_MARKER) {
                    pixels[x][y] = false;
                } else if (c== FILL_MARKER) {
                    pixels[x][y]=true;
                } else {
                    throw new IllegalArgumentException("Bad marker.");
                }
            }
        }
    }

    /**
     * @return String representation is a string with picture of this nonogram.
     * It has HEIGHT lines, with WIDTH character in each line, splitted with newline character.
     * Each character in line marks corresponding pixel.
     * Space marks empty pixel.
     * "X" marks filled pixel.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int y=0;y<height;y++) {
            for(int x=0;x<width;x++) {
                sb.append(pixels[x][y]? FILL_MARKER : EMPTY_MARKER);
            }
            if(y!= height-1) sb.append('\n');
        }
        return sb.toString();
    }

    public boolean fitsDescription(NonogramDescription description) {
        if(description.getWidth() != this.width) return false;
        if(description.getHeight() != this.height) return false;

        for(int x=0;x<width;x++) {
            if(!this.getColumnDescription(x).equals(description.getColumnDescription(x))) return false;
        }

        for(int y=0;y<width;y++) {
            if(!this.getRowDescription(y).equals(description.getRowDescription(y))) return false;
        }

        return true;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean getPixel(int x, int y) {
        return pixels[x][y];
    }

    public void SetPixel(int x, int y, boolean value) {
        pixels[x][y]=value;
    }

    public List<Boolean> getRow(int y){
        List<Boolean> ans = new ArrayList<Boolean>();
        for(int x=0;x<width;x++) {
            ans.add(pixels[x][y]);
        }
        return ans;
    }

    public List<Boolean> getColumn(int x){
        return Arrays.asList(pixels[x]);
    }

    public List<Integer> getRowDescription(int y) {
        List<Integer> ans = new ArrayList<Integer>();
        int cum = 0;
        for (int x=0;x<width;x++) {
            if (pixels[x][y]) cum++;
            else if (cum > 0) {ans.add(cum); cum = 0;}
        }
        if (cum > 0) ans.add(cum);
        return ans;
    }

    public List<Integer> getColumnDescription(int x) {
        List<Integer> ans = new ArrayList<Integer>();
        int cum = 0;
        for (int y=0;y<height;y++) {
            if (pixels[x][y]) cum++;
            else if (cum > 0) {ans.add(cum); cum = 0;}
        }
        if (cum > 0) ans.add(cum);
        return ans;
    }




    public static NonogramSolution fromFile(File file) throws IOException {
        List<String> lines = Files.lines(file.toPath()).collect(Collectors.toCollection(ArrayList::new));

        int width = 0;
        int height = 0;
        String goal="";

        for (String line : lines) {
            String[] s = line.split(" ");
            if (s[0].equals("width")) {
                width = Integer.valueOf(s[1]);
            } else if (s[0].equals("height")) {
                height = Integer.valueOf(s[1]);
            } else if (s[0].equals("goal")) {
                goal = s[1].replace("\"","");
            }
        }
        Boolean[][] pixels = new Boolean[width][height];
        for(int y=0;y<height;y++) {
            for(int x=0;x<width;x++) {
                char c = goal.charAt(y*width+x);
                pixels[x][y] = (c=='1');
            }
        }
        return new NonogramSolution(pixels);
    }

    public static NonogramSolution random(int width, int height, Random random, double prob) {
        Boolean[][] pixels = new Boolean[width][height];
        for(int x=0;x<width;x++) {
            for(int y=0;y<height;y++) {
                pixels[x][y] = (random.nextDouble()<prob);
            }
        }
        return new NonogramSolution(pixels);
    }


    @Override
    public boolean equals(Object obj) {
        if(this==obj)return true;
        if(obj.getClass() != NonogramSolution.class) return false;
        NonogramSolution sol = (NonogramSolution)obj;
        if (sol.width != this.width || sol.height!= this.height) {
            return false;
        }
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++) {
                if(pixels[x][y]!=sol.getPixel(x,y)) return false;
            }
        }
        return true;
    }
}
