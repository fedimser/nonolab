package io.github.fedimser.nonolab;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NonogramDescription {
    private int width;
    private int height;
    private List<List<Integer>> columnDescriptions;
    private List<List<Integer>> rowDescriptions;


    // Constructor which explicitly describes given solution.
    NonogramDescription(NonogramSolution solution) {
        width = solution.getWidth();
        height = solution.getHeight();
        columnDescriptions = new ArrayList<List<Integer>>();
        rowDescriptions = new ArrayList<List<Integer>>();

        for (int x = 0; x < width; x++) {
            columnDescriptions.add(solution.getColumnDescription(x));
        }

        for (int y = 0; y < height; y++) {
            rowDescriptions.add(solution.getRowDescription(y));
        }
    }

    NonogramDescription(List<List<Integer>> columnDescriptions, List<List<Integer>> rowDescriptions) {
        width = columnDescriptions.size();
        height = rowDescriptions.size();
        this.columnDescriptions = columnDescriptions;
        this.rowDescriptions = rowDescriptions;
    }

    // From compact string representation
    NonogramDescription(String cols, String rows) {
        String[] colsSplit = cols.split(";");
        String[] rowsSplit = rows.split(";");
        width = colsSplit.length;
        height = rowsSplit.length;
        columnDescriptions = new ArrayList<List<Integer>>();
        rowDescriptions = new ArrayList<List<Integer>>();

        for(int x=0;x<width;x++) {
            List<Integer> nums = new ArrayList<Integer>();
            if(colsSplit[x].length()>0) {
                for (String s : colsSplit[x].split(",")) {
                    nums.add(Integer.valueOf(s));
                }
            }
            columnDescriptions.add(nums);
        }

        for(int y=0;y<height;y++) {
            List<Integer> nums = new ArrayList<Integer>();
            if(rowsSplit[y].length()>0) {
                for (String s : rowsSplit[y].split(",")) {
                    nums.add(Integer.valueOf(s));
                }
            }
            rowDescriptions.add(nums);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Integer> getRowDescription(int y) {
        return rowDescriptions.get(y);
    }

    public List<Integer> getColumnDescription(int x) {
        return columnDescriptions.get(x);
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj) return true;
        if(obj.getClass() != NonogramDescription.class) return false;
        NonogramDescription descr = (NonogramDescription)obj;
        if (descr.width != this.width || descr.height!= this.height) {
            return false;
        }
        return (this.rowDescriptions.equals(descr.rowDescriptions) &&
                this.columnDescriptions.equals(descr.columnDescriptions));
    }

    public static NonogramDescription fromFile(File file) throws IOException {
        List<String> lines = Files.lines(file.toPath()).collect(Collectors.toCollection(ArrayList::new));

        int width = 0;
        int height = 0;
        List<List<Integer>> cols = new ArrayList<List<Integer>>();
        List<List<Integer>> rows = new ArrayList<List<Integer>>();


        String mode = "";
        for (String line : lines) {
            String[] s = line.split(" ");
            if (s[0].equals("width")) {
                width = Integer.valueOf(s[1]);
            } else if (s[0].equals("height")) {
                height = Integer.valueOf(s[1]);
            } else if (s[0].equals("rows")) {
                mode = "rows";
            } else if (s[0].equals("columns")) {
                mode = "columns";
            } else if (mode.equals("rows")) {
                if(rows.size() >= height) continue;
                rows.add(parseRowDesc(line));
            } else if(mode.equals("columns")) {
                if(cols.size() >= width) continue;
                cols.add(parseRowDesc(line));
            }
        }
        return new NonogramDescription(cols, rows);
    }

    private static List<Integer> parseRowDesc(String s) {
        List<Integer> ans = new ArrayList<Integer>();
        for(String x: s.split(",")) {
            int num=0;
            try {
                num = Integer.valueOf(x);
            } catch (NumberFormatException e) {
                continue;
            }
            if(num!=0)ans.add(num);
        }
        return ans;
    }

    /**
     * @return Maximal length of column description.
     */
    public int getDescriptionHeight() {
        int ans=0;
        for(int x=0;x<width;x++){
            ans = Math.max(ans, columnDescriptions.get(x).size());
        }
        return ans;
    }

    /**
     * @return Maximal length of row description.
     */
    public int getDescriptionWidth() {
        int ans=0;
        for(int y=0;y<height;y++){
            ans = Math.max(ans, rowDescriptions.get(y).size());
        }
        return ans;
    }

}

