/*******************************************************************************
* 
*   File: CS4450Program2.java
*   Author: Steven Phung
*   Class: CS 4450.01 - Computer Graphics
*
*   Assignment: Program 2
*   Date last modified: 2/22/2020
*
*   Purpose: This class uses the LWJGL Library to draw a window, read from a
*           list of coordinates from a text file, and draw polygons, transform
*           them, and fill them.
*
*******************************************************************************/
package cs4450program2;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.input.Keyboard;

public class CS4450Program2 {
    
    //Edge table indexing
    private final int MINIMUM_Y_VALUE = 0, MAXIMUM_Y_VALUE = 1;
    private final int X_VALUE = 2, INVERSE_SLOPE = 3;
    
    //Method: start()
    //Purpose: Performs tasks needed to draw window, initialize gl, read from
    //file, and then render file into window.
    public void start() {
        try {
            createWindow();
            initGL();
            render();
        } catch (Exception e) { }
    }
    
    //Method: createWindow()
    //Purpose: Creates the display with the desired size (640x480)
    public void createWindow() throws Exception {
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(640,480));
        Display.setTitle("CS4450.01 Program 2");       
        Display.create();
    }
    
    //method: initGL
    //purpose: intitalize properties for GL
    public void initGL(){
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-640/2f, 640/2f, -480/2f, 480/2f, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }
    
    //Method: render()
    //Purpose: Draws the shapes based on the given coordinates.txt file 
    public void render() throws IOException {
        
        //Use HashMap to stores all polygons
        HashMap<ArrayList<Vertex>,ArrayList<String>> polygons;
        
        //File is in default project folder
        File file = new File("coordinates.txt");
        
        //HashMap based on coordinates file
        polygons = parseFile(file);
        
        //Render polygons until escape key is pressed or user exits window
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            
            //Iterate through polygons
            Iterator<ArrayList<Vertex>> polygonIterator = polygons.keySet().iterator();
            try {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                
                //While there are still polygons
                while(polygonIterator.hasNext()) {
                    
                    //Split up information in file
                    //Get vertexes
                    ArrayList<Vertex> listOfVertexes = polygonIterator.next();
                    //Get color values / transformation values
                    ArrayList<String> attributes = polygons.get(listOfVertexes);
                    
                    //Get first line, which are the color values
                    String[] color = attributes.get(0).split(" ");
                    
                    //Set to appropriate color
                    glColor3f(Float.parseFloat(color[1]), Float.parseFloat(color[2]), Float.parseFloat(color[3]));
                    
                    //Apply all transformations to old list of vertex and get new list
                    listOfVertexes = transformations(listOfVertexes, attributes);
                    
                    //Draw polygons and fill them based on new list of transformed vertexes
                    fillInPolygon(getAllEdgeTable(listOfVertexes));
                }
                Display.update();
                Display.sync(60);
            } catch(NumberFormatException e){ }     
        }   
        Display.destroy();
    }
    
    //Method: transformations(ArrayList<Vertex> listOfVertexes, ArrayList<String> transformations)
    //Purpose: Given a list of vertexes, transform by translation, rotation, or scaling
    //and then return the new set of vertexes
    public ArrayList<Vertex> transformations(ArrayList<Vertex> listOfVertexes, ArrayList<String> transformations) {
        
        //Vertexes placed in this ArrayList when finished transforming
        ArrayList<Vertex> finishedTransforming = new ArrayList<>();
        
        //Start at 1, skip colors, go through all transformations
        for(int i = 1; i < transformations.size(); i++) {
            
            //Split regex to read transformations
            String[] currentTransformation = transformations.get(i).split(" ");
            //First letter indicates what type of transformation we are doing
            char type = currentTransformation[0].charAt(0);
            
            //r - rotate vertex, t - translate vertex, s - scale vertex
            //Once transformation is complete, add to new list
            switch(type) {
                case 'r': 
                    for(int j = 0; j < listOfVertexes.size(); j++) {
                        finishedTransforming.add(rotation(listOfVertexes.get(j),Float.parseFloat(currentTransformation[1]),Float.parseFloat(currentTransformation[2]),Float.parseFloat(currentTransformation[3])));
                    }
                    break;
                case 't':
                    for(int j = 0; j < listOfVertexes.size(); j++) {
                        finishedTransforming.add(translation(listOfVertexes.get(j),Float.parseFloat(currentTransformation[1]),Float.parseFloat(currentTransformation[2])));
                    }
                    break;
                case 's':
                    for(int j = 0; j < listOfVertexes.size(); j++) {
                        finishedTransforming.add(scaling(listOfVertexes.get(j),Float.parseFloat(currentTransformation[1]),Float.parseFloat(currentTransformation[2]),Float.parseFloat(currentTransformation[3]),Float.parseFloat(currentTransformation[4])));
                    }
                    break;
                default:
                    break;
            }
            //Set referenced list of vertexes
            listOfVertexes = finishedTransforming;
            finishedTransforming = new ArrayList<>();
        }
        //Return new list
        return listOfVertexes;
    }
    
    //Method: parseFile(File file)
    //Purpose: Read in coordinates.txt
    public HashMap<ArrayList<Vertex>,ArrayList<String>> parseFile(File file) throws IOException {
        
        //Use HashMap to stores all polygons
        HashMap<ArrayList<Vertex>,ArrayList<String>> polygons = new HashMap<>();
        
        //Store list of vertexes
        ArrayList<Vertex> listOfVertexes = new ArrayList<>();
        //Store color values and transformation values
        ArrayList<String> attributes = null;
        
        //Scanner to read in file
        Scanner scanner = new Scanner(file);
        
        //Keep state of whether we're reading a polygon or a translation
        char state = 'p';
        //Read until end of file
        while(scanner.hasNextLine()) {
            //Read in line by line
            String currentLine = scanner.nextLine();
            //Split line by " "
            String readLine[] = currentLine.split(" ");

            //If we're adding in polygons
            if(state == 'p') {
                switch (readLine[0]) {
                    //If P is at the start, it means there are color values to add
                    case "P":
                        attributes = new ArrayList<>();
                        attributes.add(currentLine);
                        break;
                    //If T, no longer reading polygon, switch state and break out
                    case "T":
                        state = 't';
                        break;
                    //If no P or T, add vertex
                    default:
                        listOfVertexes.add(new Vertex(Float.parseFloat(readLine[0]), Float.parseFloat(readLine[1])));
                        break;
                }
            }

            //If we're adding transformations
            if(state == 't') {
                switch (readLine[0]) {
                    //Nothing to do when line read is just "T"
                    case "T":
                        break;
                    //If P, switch to polygon, but new polygon, so new lists
                    case "P":
                        state = 'p';
                        polygons.put(listOfVertexes, attributes);
                        attributes = new ArrayList<>();
                        listOfVertexes = new ArrayList<>();
                        attributes.add(currentLine);
                        break;
                    //If not P or T, add transformations
                    default:
                        attributes.add(currentLine);
                        break;
                }
            }
        }
        //Add vertexes and attributes to polygons
        polygons.put(listOfVertexes,attributes);
        //Return polygons
        return polygons;
    }
    
    //Method: fillInPolygon(LinkedList<ArrayList<Float>> edges)
    //Purpose: Use fill in algorithm to fill in polygons
    public void fillInPolygon(LinkedList<ArrayList<Float>> edges) {
        
        //Global and active edge table
        LinkedList<ArrayList<Float>> globalEdgeTable = getGlobalEdgeTable(edges);
        LinkedList<ArrayList<Float>> activeEdgeTable = new LinkedList<>();
        
        //Initial parity value is 0
        int parity = 0;
        
        //Scan line is set to the smallest y value in global edge table
        float scanLine = globalEdgeTable.getFirst().get(MINIMUM_Y_VALUE);
        
        //Move edges from globalTable to activeTable based on minimum y value
        while(!globalEdgeTable.isEmpty() && globalEdgeTable.getFirst().get(MINIMUM_Y_VALUE) == scanLine) {
            activeEdgeTable.add(globalEdgeTable.removeFirst());
        }
        
        //Fill based on active edge table
        while(!activeEdgeTable.isEmpty()) {
            //Iterator for active edge table
            ListIterator<ArrayList<Float>> activeEdgeTableList = activeEdgeTable.listIterator();
            
            //Furthest left x value
            float currentX = -320;
            //X value to be determined based on active edge table
            float nextX;
            
            //Iterator through active edge table
            while(activeEdgeTableList.hasNext()) {
                //Get next x value in edge table, table already sorted
                nextX = activeEdgeTableList.next().get(X_VALUE);
                
                //Keep incrementing x until we run into x value found in active edge table
                while(currentX < nextX){
                    //If parity == 1, we're inside polygon, so draw point
                    if(parity == 1) {
                        glBegin(GL_POINTS);
                            glVertex2f(currentX, scanLine);
                        glEnd();
                    }
                    currentX++;
                }
                
                //if parity == 1, set it to 0, else set it to 1
                if(parity == 1) {
                    parity = 0;
                } else {
                    parity = 1;
                }
                
                //Draw point
                glBegin(GL_POINTS);
                    glVertex2f(currentX, scanLine);
                glEnd();
            }
            
            //Increment scan line after going through x's
            scanLine++;
            
            activeEdgeTableList = activeEdgeTable.listIterator();
            //Updates X values in the active edge table
            while(activeEdgeTableList.hasNext()) {
                ArrayList<Float> edge = activeEdgeTableList.next();
                edge.set(X_VALUE, edge.get(X_VALUE) + edge.get(INVERSE_SLOPE));
            }
            //Add all edges from global edge table that have the same y min value as the scan line
            while(!globalEdgeTable.isEmpty() && globalEdgeTable.getFirst().get(MINIMUM_Y_VALUE) == scanLine) {
                activeEdgeTable.add(globalEdgeTable.removeFirst());
            }
            
            activeEdgeTableList= activeEdgeTable.listIterator();
            //Remove any edges in the active edge table that has y max value same as scan line value
            while(activeEdgeTableList.hasNext()) {
                if(activeEdgeTableList.next().get(MAXIMUM_Y_VALUE) == scanLine) {
                    activeEdgeTableList.remove();
                }
            }
            //Re-sort active edge table
            activeEdgeTable = (sortEdges(activeEdgeTable));
        }       
    }
    
    //Method: getGlobalEdgeTable(LinkedList<ArrayList<Float>> edges)
    //Purpose: Return a global edge table based on given edges
    public LinkedList<ArrayList<Float>> getGlobalEdgeTable(LinkedList<ArrayList<Float>> edges) {
        LinkedList<ArrayList<Float>> globalEdgeList = new LinkedList<>();
        ListIterator<ArrayList<Float>> edgeList = edges.listIterator();
        
        //Add all edges to global edge table where 1/m is not infinity
        while(edgeList.hasNext()) {
            ArrayList<Float> currentEdge = edgeList.next();
            //Don't add edges where 1/m = infinity
            if(currentEdge.get(INVERSE_SLOPE) != Float.MAX_VALUE) {
                globalEdgeList.add(currentEdge);
            }
        }
        //Sort and return
        return sortGlobalEdge(globalEdgeList);
    }
    
    //Method: getAllEdgeTable(ArrayList<Vertex> listOfVertexes)
    //take a list of vertex and store them to all edge table
    public LinkedList<ArrayList<Float>> getAllEdgeTable(ArrayList<Vertex> listOfVertexes) {
        LinkedList<ArrayList<Float>> allEdges = new LinkedList<>();
        Vertex initialVertex = listOfVertexes.get(0);
        
        //For all edges, add all pairs of vertexes and attributes
        for(int i = 0; i < listOfVertexes.size(); i++) {
            ArrayList<Float> attributes = new ArrayList<>();
            Edge currentEdge;
            
            //Last vertex needs to match with initial vertex
            if(i == (listOfVertexes.size()-1)) {
                currentEdge = new Edge(listOfVertexes.get(i), initialVertex);
            } else {
                currentEdge = new Edge(listOfVertexes.get(i), listOfVertexes.get(i+1));
            }
            //Store x, y min, y max, 1/m values
            attributes.add(currentEdge.getMinYVertex().getY());
            attributes.add(currentEdge.getMaxYVertex().getY());
            attributes.add(currentEdge.getMinYVertex().getX());
            attributes.add(currentEdge.getInverseSlope()); 
            //Add to linked list
            allEdges.add(attributes);
        }
        //Return all edges
        return allEdges;
    }
    
    //Method: translation(Vertex v, float x, float y)
    //Purpose: Perform matrix multiplication to translate by given x and y values
    public Vertex translation(Vertex v, float x, float y){
        //Gets a matrix to multiple for translation
        Matrix translateMatrix = new Matrix();
        translateMatrix.getTranslationMatrix(x, y);
        
        //Gets our 3 by 1 vertex matrix to multiply with
        Matrix vertexMatrix = new Matrix();
        vertexMatrix.getVertexMatrix(v.getX(), v.getY());
        
        //Multiply translation matrix and vertex matrix and return the product's new vertex
        Matrix product = translateMatrix.multiplication(vertexMatrix);
        return new Vertex(product.getMatrix()[0][0], product.getMatrix()[1][0]);
    }
    
    //Method: rotation(Vertex v, float degree, float x, float y)
    //Purpose: Perform matrix multiplication to totate by a certain degree by given pivot point
    public Vertex rotation(Vertex v, float degree, float x, float y) {
        //Gets a matrix to multiple for rotation
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.getRotationMatrix(degree);
        
        //Gets our 3 by 1 vertex matrix to multiply with
        Matrix vertexMatrix = new Matrix();
        vertexMatrix.getVertexMatrix(v.getX(), v.getY());
        
        //Multiply rotation matrix and vertex matrix and return the product's new vertex
        Matrix after = rotationMatrix.multiplication(vertexMatrix);
        return new Vertex(after.getMatrix()[0][0], after.getMatrix()[1][0]);
    }
    
    //Method: scaling(Vertex v, float x, float y, float pivotPointX, float pivotPointY)
    //Purpose: Perform matrix multiplication to scale polygon
    public Vertex scaling(Vertex v, float x, float y, float pivotPointX, float pivotPointY) {
        //Gets a matrix to multiple for scaling
        Matrix scalingMatrix = new Matrix();
        scalingMatrix.getScalingMatrix(x, y);
        
        //Gets our 3 by 1 vertex matrix to multiply with
        Matrix vertexMatrix = new Matrix();
        vertexMatrix.getVertexMatrix(v.getX(), v.getY());
        
        //Multiply scaling matrix and vertex matrix and return the product's new vertex
        Matrix after = scalingMatrix.multiplication(vertexMatrix);
        return new Vertex(after.getMatrix()[0][0], after.getMatrix()[1][0]);
    }
    
    //Method: sortEdges(LinkedList<ArrayList<Float>> edges)
    //Purpose: Use insertion sort to sort edges
    public LinkedList<ArrayList<Float>> sortEdges(LinkedList<ArrayList<Float>> edges) {
        LinkedList<ArrayList<Float>> sortedEdge = new LinkedList<>();
        ListIterator<ArrayList<Float>> iterator = edges.listIterator();
        //Iterate through all edges
        while(iterator.hasNext()) {
            ArrayList<Float> edge = iterator.next();
            //Add first edge without checking
            if(sortedEdge.isEmpty()) {
                sortedEdge.add(edge);
            } else {
                ListIterator<ArrayList<Float>> iterator2 = sortedEdge.listIterator();
                boolean inList = false;
                //Add in based on x value
                while(iterator2.hasNext()) {
                    ArrayList<Float> currentEdge = iterator2.next();
                    if(edge.get(X_VALUE)< currentEdge.get(X_VALUE)) {	
                        iterator2.previous();
                        iterator2.add(edge);	
                        inList = true;			
                        break;
                    }
                }
                //If went through entire list without adding, add at the end
                if(!inList) {				
                    iterator2.add(edge);		
                }
            }
        }
        //Return edges sorted
        return sortedEdge;
    }
    
    //Method: sortGlobalEdge(LinkedList<ArrayList<Float>> globalEdge)
    //Purpose: Use insertion sort to sort edges
    public LinkedList<ArrayList<Float>> sortGlobalEdge(LinkedList<ArrayList<Float>> globalEdge){
        LinkedList<ArrayList<Float>> sortedEdge = new LinkedList<>();
        ListIterator<ArrayList<Float>> iterator = globalEdge.listIterator();
        //Iterate through all edges
        while(iterator.hasNext()) {
            ArrayList<Float> edge = iterator.next();
            //Add first edge without checking
            if(sortedEdge.isEmpty()){
                sortedEdge.add(edge);
            } else {
                ListIterator<ArrayList<Float>> sortedEdgeList = sortedEdge.listIterator();
                boolean inList = false;
                
                //First sort by minimum y values
                //If they have the same minimum y values, sort by their x values
                //If they also have same x values, sort by maximum y values
                while(sortedEdgeList.hasNext()) {	
                    ArrayList<Float> currentEdge = sortedEdgeList.next();
                    if(edge.get(MINIMUM_Y_VALUE) < currentEdge.get(MINIMUM_Y_VALUE)) {	
                        sortedEdgeList.previous();
                        sortedEdgeList.add(edge);	
                        inList = true;			
                        break;
                    } else if(Objects.equals(edge.get(MINIMUM_Y_VALUE), currentEdge.get(MINIMUM_Y_VALUE))) {
                        if(edge.get(X_VALUE) < currentEdge.get(X_VALUE)) {	
                            sortedEdgeList.previous();
                            sortedEdgeList.add(edge);	
                            inList = true;			
                            break;
                        } else if(Objects.equals(edge.get(X_VALUE), currentEdge.get(X_VALUE))) {
                            if(edge.get(MAXIMUM_Y_VALUE) < currentEdge.get(MAXIMUM_Y_VALUE)) {	
                                sortedEdgeList.previous();
                                sortedEdgeList.add(edge);	
                                inList = true;			
                                break;
                            }
                        }
                    }
                }
                //Add to the end of the list if failed to match any of the above conditions
                if(!inList) {				
                    sortedEdgeList.add(edge);		
                }
            }
        }
        //Return edges sorted
        return sortedEdge;
    }
    
    //Method: main()
    //Purpose: Creates an instance of the program and starts it
    public static void main(String[] args) {
        CS4450Program2 main = new CS4450Program2();
        main.start();
    }
}
