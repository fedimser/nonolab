package io.github.fedimser.nonolab;




import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class NonogramDrawer {
    final static int CELL_SIZE = 25;
    final static Color BACKGROUND_COLOR = Color.white;
    final static Color CELL_COLOR = Color.black;

    public static RenderedImage drawSolution(NonogramSolution sol) {
        int w = sol.getWidth();
        int h = sol.getHeight();

        BufferedImage image = new BufferedImage(w*CELL_SIZE,h*CELL_SIZE,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, w*CELL_SIZE,h*CELL_SIZE);
        g.setColor(CELL_COLOR);

        // Draw cells.
        for(int x=0;x<w;x++) {
            for(int y=0;y<h;y++) {
                if(sol.getPixel(x,y)) {
                    g.fillRect(x*CELL_SIZE,y*CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
        g.dispose();
        return (RenderedImage)image;
    }

    public static RenderedImage drawDescription(NonogramDescription des) {
        return drawFull(des, null);
    }

    public static RenderedImage drawFull(NonogramDescription des, NonogramSolution sol) {
        int w1 = des.getDescriptionWidth();
        int h1 = des.getDescriptionHeight();
        int w2 = des.getWidth();
        int h2 = des.getHeight();

        BufferedImage image = new BufferedImage((w1+w2)*CELL_SIZE,(h1+h2)*CELL_SIZE,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, (w1+w2)*CELL_SIZE,(h1+h2)*CELL_SIZE);
        g.setColor(CELL_COLOR);

        Stroke stroke1 = new BasicStroke(1);
        Stroke stroke2 = new BasicStroke(2);
        Stroke stroke3 = new BasicStroke(4);


        // Draw horizontal lines.
        for(int y=-h1;y<=h2;y++) {
            Stroke stroke=stroke1;
            if(y%5==0) stroke=stroke2;
            if(y==-h1 || y==0 || y==h2) stroke=stroke3;
            g.setStroke(stroke);
            g.drawLine(0, CELL_SIZE*(y+h1), CELL_SIZE*(w1+w2), CELL_SIZE*(y+h1));
        }

        // Draw vertical lines.
        for(int x=-w1;x<=w2;x++) {
            Stroke stroke=stroke1;
            if(x%5==0) stroke=stroke2;
            if(x==-w1 || x==0 || x==w2) stroke=stroke3;
            g.setStroke(stroke);
            g.drawLine(CELL_SIZE*(x+w1),0,CELL_SIZE*(x+w1), CELL_SIZE*(h1+h2));
        }


        Font font = new Font("Arial", Font.PLAIN, (int)(CELL_SIZE*0.6));
 

        // Draw column descriptions.
        for (int x=0;x<w2; x++) {
            for(int y=0;y<h2;y++) {
                List<Integer> nums = des.getColumnDescription(x);
                int pos = h1 - nums.size();
                for(int num: nums) {
                    int cellX = (w1+x)*CELL_SIZE;
                    int cellY = pos*CELL_SIZE;
                    Rectangle rect = new Rectangle(cellX, cellY, CELL_SIZE, CELL_SIZE);
                    drawCenteredString(g, String.valueOf(num), rect, font);
                    pos++;
                }
            }
        }

        // Draw row descriptions.
        for (int y=0;y<h2; y++) {
            for(int x=0;x<w2;x++) {
                List<Integer> nums = des.getRowDescription(y);
                int pos = w1 - nums.size();
                for(int num: nums) {
                    int cellX = pos*CELL_SIZE;
                    int cellY =  (h1+y)*CELL_SIZE;
                    Rectangle rect = new Rectangle(cellX, cellY, CELL_SIZE, CELL_SIZE);
                    drawCenteredString(g, String.valueOf(num), rect, font);
                    pos++;
                }
            }
        }


        // Draw solution.
        if(sol != null) {
            for(int x=0;x<w2;x++) {
                for(int y=0;y<h2;y++) {
                    if(sol.getPixel(x,y)) {
                        g.fillRect((x+w1)*CELL_SIZE,(y+h1)*CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }

        g.dispose();
        return (RenderedImage)image;
    }

    /**
     * Draw a String centered in the middle of a Rectangle.
     * @param g The Graphics instance.
     * @param text The String to draw.
     * @param rect The Rectangle to center the text in.
     */
    private static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    public static void drawAll(NonogramSolution sol, Path dir, String name) throws java.io.IOException {
        NonogramDescription desc = new NonogramDescription(sol);

        RenderedImage descPic = drawDescription(desc);
        ImageIO.write(descPic, "jpg", dir.resolve(name + "_descr.jpg").toFile());

        RenderedImage solPic = drawSolution(sol);
        ImageIO.write(solPic, "jpg", dir.resolve(name + "_solved.jpg").toFile());

        RenderedImage fullPic = drawFull(desc, sol);
        ImageIO.write(fullPic, "jpg", dir.resolve(name + ".jpg").toFile());
    }


}
