/*******************************************************************************
* 
*   File: Matrix.java
*   Author: Steven Phung
*   Class: CS 4450.01 - Computer Graphics
*
*   Assignment: Program 2
*   Date last modified: 2/22/2020
*
*   Purpose: This class is used to make matrices to calculate transformations.
*
*******************************************************************************/
package cs4450program2;

public class Matrix {
    
    //2D Array
    float[][] matrix;
    //Size of matrix
    int row;
    int column;
    
    //Method: constructor Matrix()
    //Purpose: Create an empty matrix
    public Matrix() {
        matrix = null;
        row = 0;
        column = 0;
    }
    
    //Method: constructor Matrix(matrix[][])
    //Purpose: Create a new matrix
    public Matrix(float[][] newMatrix) {
        row = newMatrix.length;
        column = newMatrix[0].length;
        matrix = new float[row][column];
        for(int i=0; i<row; i++){
            for(int j = 0; j < column; j++){
                matrix[i][j] = newMatrix[i][j];
            }
        }
    }
    
    //Method: getVertexMatrix(float x, float y)
    //Purpose: Create a matrix with given vertices
    public float[][] getVertexMatrix(float x, float y) {
        float[][] newMatrix = {{x},{y},{1}};
        row = newMatrix.length;
        column = newMatrix[0].length;
        matrix = newMatrix;
        return newMatrix;
    }
    
    //Method: getTranslationMatrix(float x, float y)
    //Purpose: Create a matrix for our x and y translations
    public float[][] getTranslationMatrix(float x, float y) {
        float[][] newMatrix = {{1,0,x},{0,1,y},{0,0,1}};
        row = newMatrix.length;
        column = newMatrix[0].length;
        matrix = newMatrix;
        return newMatrix;
    }
    
    //Method: getRotationMatrix(float degree)
    //Purpose: Create a matrix for our rotations
    public float[][] getRotationMatrix(float degree) {
        double radian = degree*Math.PI/180;
        float[][] newMatrix = {{(float)(Math.cos(radian)), -(float)Math.sin(radian), 0},{(float)Math.sin(radian), (float)Math.cos(radian), 0},{0,0,1}};
        row = newMatrix.length;
        column = newMatrix[0].length;
        matrix = newMatrix;
        return newMatrix;
    }
    
    //Method: getScalingMatrix(float x, float y)
    //Purpose: Create a new matrix for scaling
    public float[][] getScalingMatrix(float x, float y){
        float[][] newMatrix = {{x,0,0},{0,y,0},{0,0,1}};
        row = newMatrix.length;
        column = newMatrix[0].length;
        matrix = newMatrix;
        return newMatrix;
    }
    
    //Method: getMatrix()
    //Purpose: Return matrix
    public float[][] getMatrix() {
        return matrix;
    }
    
    //Method: getRowSize()
    //Purpose: Return matrix's row size
    public int getRowSize() {
        return row;
    }
    
    //Method: getColumnSize()
    //Purpose: Return matrix's column size
    public int getColumnSize() {
        return column;
    }
    
    //Method: getRow(int row)
    //Purpose: Return a matrix's specific row
    public float[] getRow(int row) {
        return matrix[row];
    }
    
    //Method: getRow(int row)
    //Purpose: Return a matrix's specific column
    public float[] getColumn(int col){
        float[] colArray = new float[this.row];
        for(int i=0; i<colArray.length; i++){
            colArray[i] = matrix[i][col];
        }
        return colArray;
    }
    
    //Method: multiplication(Matrix rightHandSide)
    //Purpose: Return product of two multiplied matrices
    public Matrix multiplication(Matrix rightHandSide) {
        float[][] product = new float[row][rightHandSide.getColumnSize()];
        for(int i = 0; i < product.length; i++){
            for(int j = 0; j < product[0].length; j++){
                product[i][j] = getRowTimesColumn(this.getRow(i), rightHandSide.getColumn(j));
            }
        }
        return new Matrix(product);
    }
    
    //Method: getRowTimesColumn(float[] row, float[] column)
    //Purpose: Return row value multiplied by column to get multiplication product
    public int getRowTimesColumn(float[] row, float[] column) {
        int product = 0;
        for(int i = 0; i < row.length; i++){
            product += row[i]*column[i];
        }
        return product;
    }
}
