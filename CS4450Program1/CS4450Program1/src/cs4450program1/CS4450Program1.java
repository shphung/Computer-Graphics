/*******************************************************************************
* 
*   File: CS4450Program1.java
*   Author: Steven Phung
*   Class: CS 4450.01 - Computer Graphics
*
*   Assignment: Program 1
*   Date last modified: 2/6/2020
*
*   Purpose: This class uses the LWJGL Library to draw a window, read from a
*           list of coordinates from a text file, and draw these primitives in
*           said window.
*
*******************************************************************************/
package cs4450program1;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.input.Keyboard;

public class CS4450Program1 {
    
    //2d array to sort and store txt file data
    //listOfShapes[i][] holds each shape
    //listOfShapes[][i] holds the point values of each shape
    String[][] listOfShapes;
    
    //Method: start()
    //Purpose: Performs tasks needed to draw window, initialize gl, read from
    //file, and then render file into window.
    public void start() {
        try {
            createWindow();
            initGL();
            parseFile();
            render();
        } catch (Exception e) {
        }
    }
    
    //Method: createWindow()
    //Purpose: Creates the display with the desired size (640x480)
    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(640, 480));
        Display.setTitle("CS4450 Program 1");
        Display.create();
    }
    
    //Method: initGL()
    //Purpose: initiate GL properties to be able to draw
    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, 640, 0, 480, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }
    
    //Method: parseFile()
    //Purpose: reads a coordinates.txt file specifically in the projects folder
    private void parseFile() throws IOException {
        //File is named 'coordinates.txt' and located in projects directory
        File file = new File("coordinates.txt");
        Scanner scanner = new Scanner(file);
        int lines = 0;
        
        //Count how much data is in the file
        //Allows program to read a coordinates.txt file of any size
        while(scanner.hasNextLine()) {
            lines++;
            scanner.nextLine();
        }
        
        //Allocate 2D array based on file size
        listOfShapes = new String[lines][5];
        
        //listOfShapes[i][0] = letter indicating shape
        //listOfShapes[i][1] = x0 of point 1
        //listOfShapes[i][2] = y0 of point 1
        //listOfShapes[i][3] = x1 of point 2 or rx value or radius of circle
        //listOfShapes[i][4] = y1 of point 2 or ry value or empty
        
        //Reset scanner
        scanner = new Scanner(file);
        //For each line, get specific strings and place into listOfShapes[][]
        for(int i = 0; i < lines && scanner.hasNextLine(); i++) {
            //Beginning of each string is letter to indicate shape
            String circleEclipseOrLine = scanner.next();
            //Place this string into first array index
            listOfShapes[i][0] = circleEclipseOrLine;
            
            //Next part of the string is always an end point
            String firstPoint = scanner.next();
            //Third and final part of the string can be two or one point
            String secondPoint = scanner.next();
            
            //Since first point will always be a coordinate with both x,y value
            //This splits firstPoint string by , and places each point into
            //respective x0 and y0 string
            int positionOfComma = firstPoint.indexOf(",");
            String x0 = firstPoint.substring(0, positionOfComma);
            String y0 = firstPoint.substring(positionOfComma + 1, firstPoint.length());
            
            //x0 and y0 points respectively
            listOfShapes[i][1] = x0;
            listOfShapes[i][2] = y0;
            
            //This is only performed if secondPoint is an endpoint, containing ,
            String x1 = "", y1 = "";
            if(secondPoint.contains(",")) {
                //Split string based on ,
                positionOfComma = secondPoint.indexOf(",");
                x1 = secondPoint.substring(0, positionOfComma);
                y1 = secondPoint.substring(positionOfComma + 1, secondPoint.length());
                //x1 and y1 point respectively
                listOfShapes[i][3] = x1;
                listOfShapes[i][4] = y1;
            } else {
                //If secondPoint doesn't contain a comma, it is a single point
                //and will simply be saved as is
                listOfShapes[i][3] = secondPoint;
            }
       }
    }
    
    //Method: render()
    //Purpose: Draws the shapes based on the given coordinates.txt file
    private void render() {
        //Display stays open until x'd or escape is pressed
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            try {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                //Draw all shapes in listOfShapes[][]
                for(int i = 0; i < listOfShapes.length; i++) {
                    //Draw a line if l is in the first index
                    if(listOfShapes[i][0].equals("l")) {
                        drawLine(Integer.parseInt(listOfShapes[i][1]),
                                Integer.parseInt(listOfShapes[i][2]),
                                Integer.parseInt(listOfShapes[i][3]),
                                Integer.parseInt(listOfShapes[i][4]));
                    //Draw a circle if c is in the first index
                    } else if(listOfShapes[i][0].equals("c")) {
                        drawCircle(Integer.parseInt(listOfShapes[i][1]),
                                Integer.parseInt(listOfShapes[i][2]),
                                Integer.parseInt(listOfShapes[i][3]));
                    //Draw an ellipse if e is in the first index
                    } else if(listOfShapes[i][0].equals("e")) {
                        drawEllipse(Integer.parseInt(listOfShapes[i][1]),
                                Integer.parseInt(listOfShapes[i][2]),
                                Integer.parseInt(listOfShapes[i][3]),
                                Integer.parseInt(listOfShapes[i][4]));
                    }
                }
                //Update everytime we draw
                Display.update();
                Display.sync(60);
            } catch (NumberFormatException e) {}
        }
        Display.destroy();
    }
    
    //Method: drawEllipse(int x0, int y0, int rx, int ry)
    //Purpose: Draw ellipse based on given origin and rx/ry values
    private void drawEllipse(int X0, int Y0, int RX, int RY) {
        glColor3f(0, 1, 0);     //green
        glBegin(GL_POINTS);     //using GL_POINTS
        //360 to draw entire ellipse
        for(int i = 0; i < 360; i ++) {
            //x and y calculated using trig formula
            float x = (float)(X0 + RX*Math.cos(i));
            float y = (float)(Y0 + RY*Math.sin(i));
            //Draw
            glVertex2f(x, y);
        }
        glEnd();
    }
    
    //Method: drawCircle(int x0, int y0, int radius)
    //Purpose: Draw a circle based on given x,y values and radius
    private void drawCircle(int X0, int Y0, int radius) {
        glColor3f(0, 0, 1);     //blue
        glBegin(GL_POINTS);     //using GL_POINTS
        //360 to draw entire circle
        for(int i = 0; i < 360; i ++) {
            //x and y calculated using trig formula
            float x = (float)(X0 + radius*Math.cos(i));
            float y = (float)(Y0 + radius*Math.sin(i));
            //Draw
            glVertex2f(x, y);
        }
        glEnd();
    }
    
    //Method: drawLine(int x0, int y0, int x1, int y1)
    //Purpose: Draws a line using midpoint algorithm using 2 given end points
    private void drawLine(int X0, int Y0, int X1, int Y1) {
        glColor3f(1, 0, 0);     //Red
        glBegin(GL_POINTS);     //using GL_POINTS
        
        //Beginning x y values
        int x = X0, y = Y0;
        
        //dx and dy values
        int dx = (X1 - X0);
        int dy = (Y1 - Y0);
        
        //incrementRight and incrementUpRight values
        int incrementRight = 2*dy;
        int incrementUpRight = 2*(dy-dx);
        
        //initial d value
        int d = 2*dy - dx;
        
        //draw first end point
        glVertex2f(x, y);
        
        //While we still have points to draw
        while(x < X1) {
            //if d is positive
            if(d > 0) {
                //move up right
                d = d + incrementUpRight;
                x++;
                y++;
            } else {
                //move right
                d = d + incrementRight;
                x++;
            }
            //If rate of change for x is negative
            if(dx < 0) {
                x--;
            }
            //If rate of change for y is negative
            if(dy < 0) {
                y--;
            }
            //Draw after finished calculating
            glVertex2f(x, y);
        }
        glEnd();
    }
    
    //Method: main()
    //Purpose: creates an instance of the program and starts it
    public static void main(String[] args) {
        CS4450Program1 main = new CS4450Program1();
        main.start();
    }
}
