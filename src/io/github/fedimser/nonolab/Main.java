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
import java.util.Scanner;

public class Main {
    private final static String help = String.join("\n",
        "exit - exit application.",
        "help - display help.",
        "solve <filename>.non - solve nonogram",
        "create <filename>.txt - create nonogram"
    );

    public static void main(String[] argv) throws Exception {
        System.out.println("Nonolab by fedimser.");
        Scanner in = new Scanner(System.in);
        while(true){
            String[] input = in.nextLine().split(" ");
            if(input[0].equals("exit")) {
                break;
            } else if(input[0].equals("help")) {
                System.out.println(help);
            } else if(input[0].equals("solve")) {
                System.out.println("Not implemented");
            } else if(input[0].equals("create")) {
                System.out.println("Not implemented");
            }

        }
    }

}
