package io.github.fedimser.nonolab;



import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.nio.file.Path;

public class Main {
    private final static String help = String.join("\n",
        "exit - exit application.",
        "help - display help.",
        "solve <filename>.non - solve nonogram",
        "create <filename>.txt - create nonogram (from ASCII-art)",
        "check <filename>.non - check uniqness of nonogram"
    );

    public static void main(String[] argv) {
        check("D:/temp/n1.non");

        System.out.println("*** Nonolab by fedimser ***");
        Scanner in = new Scanner(System.in);
        while(true){
            String[] input = in.nextLine().split(" ");
            if(input[0].equals("exit")) {
                break;
            } else if(input[0].equals("help")) {
                System.out.println(help);
            } else if(input[0].equals("solve")) {
                solve(input[1]);
            } else if(input[0].equals("create")) {
                create(input[1]);
            } else if(input[0].equals("check")) {
                check(input[1]);
            }
        }
    }

    private static void solve(String fileName) {
        File file = new File(fileName);
        NonogramDescription desc;
        try {
            desc = NonogramDescription.fromFile(file);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        Solver solver = new Solver(desc);
        solver.setVerbose(true);
        NonogramSolution sol = solver.solve();
        if(sol==null) {
            System.out.println("No solution.");
            return;
        }
        String solAscii = sol.toString();
        System.out.println(solAscii);

        Path dir = file.getParentFile().toPath();
        String name = file.getName();
        if(name.substring(name.length()-4).equals(".non")){
            name = name.substring(0, name.length()-4);
        }


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dir.resolve(name+".txt").toFile()));
            writer.write(solAscii);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            NonogramDrawer.drawAll(sol, dir, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void create(String fileName) {

    }

    private static void check(String fileName) {
        File file = new File(fileName);
        NonogramDescription desc;
        try {
            desc = NonogramDescription.fromFile(file);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        Solver solver = new Solver(desc);
        NonogramSolution sol1 = solver.solve();
        if (sol1==null) {
            System.out.println("No solution.");
        } else if (solver.hasUniqueSolution()) {
            System.out.println("Has unique solution.");
        } else {
            System.out.println("Solution is not unique.");

            NonogramSolution sol2 = solver.getCounterExample();

            RenderedImage pic1 = NonogramDrawer.drawFull(sol1);
            RenderedImage pic2 = NonogramDrawer.drawFull(sol2);

            Path dir = file.getParentFile().toPath();
            String name = file.getName();

            try {
                ImageIO.write(pic1, "jpg", dir.resolve(name + "_sol1.jpg").toFile());
                ImageIO.write(pic2, "jpg", dir.resolve(name + "_sol2.jpg").toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
