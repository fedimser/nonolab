package io.github.fedimser.nonolab;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.Random;

import javax.imageio.ImageIO;
import  java.nio.file.Path;

public class Main {

    private static void check1(String name) throws java.io.IOException {
        Path dir = Paths.get("D:/temp");
        NonogramSolution sol0 = NonogramSolution.fromFile(new File("D:/temp/" + name + ".non"));
        NonogramDescription desc = NonogramDescription.fromFile(new File("D:/temp/" + name + ".non"));
        if(desc.equals(new NonogramDescription(sol0))) {
            System.out.println("goal OK");
        } else {
            System.out.println("goal BAD");
            //System.exit(1);
        }

        Solver solver = new Solver(sol0);
        System.out.println(solver.solve(desc));
        NonogramSolution sol = solver.getSolution();
        NonogramDrawer.drawAll(sol, dir, name);
    }

    private static void check2(int width, int height) {
        Random random = new Random();
        Solver solver = new Solver();
        for(int i=0;i<10000;i++) {
            NonogramSolution sol = NonogramSolution.random(width,height,random, 0.6);
            NonogramDescription descr = new NonogramDescription(sol);
            if(solver.solve(descr)==Solver.SolveResult.IMPOSSIBLE) {
                System.out.println(sol.toString());
                System.exit(1);
            }
        }
        System.out.println("OK");
    }

    public static void main(String[] argv) throws Exception {
        check1("kde");
    }

}
