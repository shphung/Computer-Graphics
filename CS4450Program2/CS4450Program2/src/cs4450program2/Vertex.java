/*******************************************************************************
* 
*   File: Vertex.java
*   Author: Steven Phung
*   Class: CS 4450.01 - Computer Graphics
*
*   Assignment: Program 2
*   Date last modified: 2/22/2020
*
*   Purpose: This class is to make a vertex object with an x and y value.
*
*******************************************************************************/
package cs4450program2;

public class Vertex {
    
    //Respective x and y values
    private float x;
    private float y;
    
    //Method: constructor Vertex(x, y)
    //Purpose: Create a vertex object with an x and y value
    public Vertex(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    //Method: getX()
    //Purpose: Return vertex's x value
    public float getX() {
        return x;
    }
    
    //Method: getX()
    //Purpose: Return vertex's y value
    public float getY() {
        return y;
    }
    
    //Method: setX()
    //Purpose: Set vertex's x value to a new value
    public void setX(float x) {
        this.x = x;
    }
}
