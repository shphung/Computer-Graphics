/*******************************************************************************
* 
*   File: Edge.java
*   Author: Steven Phung
*   Class: CS 4450.01 - Computer Graphics
*
*   Assignment: Program 2
*   Date last modified: 2/22/2020
*
*   Purpose: This class is to make an edge object with 2 vertexes.
*
*******************************************************************************/
package cs4450program2;

public class Edge {
    
    //Two end points
    private Vertex v1;
    private Vertex v2;
    //Keep track of which one has larger y value
    private Vertex maxYVertex;
    private Vertex minYVertex;
    //dy / dx
    private float dy;
    private float dx;
    
    //Method: constructor Edge(Vertex, Vertex)
    //Purpose: Create an edge object with two vertices
    public Edge(Vertex v1, Vertex v2) {
        this.v1 = v1;
        this.v2 = v2;
        if(v1.getY() > v2.getY()) {
            maxYVertex = v1;
            minYVertex = v2;
        } else {
            maxYVertex = v2;
            minYVertex = v1;
        }
        dy = v2.getY() - v1.getY();
        dx = v2.getX() - v1.getX();
    }
    
    //Method: getMaxYVertex()
    //Purpose: Return the edge's biggest y value
    public Vertex getMaxYVertex() {
        return maxYVertex;
    }
    
    //Method: getMinYVertex()
    //Purpose: Return the edge's smallest y value
    public Vertex getMinYVertex() {
        return minYVertex;
    }
    
    //Method: getSlope()
    //Purpose: Return the edge's rise over run
    public float getSlope() {
        return dy/dx;
    }
    
    //Method: getInverseSlope()
    //Purpose: Return the edge's 1/m needed for the edge table
    public float getInverseSlope() {
        //Cannot divide by 0
        if(dy == 0)
            return Float.MAX_VALUE;
        return dx/dy;
    }
}
