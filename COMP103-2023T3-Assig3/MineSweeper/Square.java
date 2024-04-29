// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 3
 * Name:
 * Username:
 * ID:
 */

import java.util.*;
import ecs100.*;
import java.awt.Color;
import java.io.*;

/**
 *   Represents information about one square in a MineSweeper map.
 *   It records
 *     its location (Row and Column)
 *     whether it has a mine or not
 *     how many squares around it have mines
 *     whether it is marked or unmarked
 *     whether it is hidden or exposed
 *   Its constructor must specify the location and
 *     whether it has a mine or not
 *   It has methods to 
 *     draw itself, (showing its state appropriately) given origin of the map.
 *     set the number of mines around it.
 *     report whether it has a mine and whether it is exposed
 *     change its state between marked and unmarked
 *     change its state to exposed
 */
public class Square{
    // Fields
    private boolean mine;
    private int adjacentMines = 0;
    private boolean marked = false;
    private boolean exposed = false;

    public static final Color EXPOSED_COLOUR = new Color(196,208,231);
    public static final Color HIDDEN_COLOUR = new Color(58,79,191);

    // Constructors
    /**
     * Construct a new Square object
     */
    public Square(boolean mine){
        this.mine = mine;
    }

    // Methods
    /**
     * Get the number of mines adjacent to this square  
     */
    public int getAdjacentMines(){
        return adjacentMines;
    }

    /**
     * Record the number of adjacent mines 
     */
    public void setAdjacentMines(int num){
        adjacentMines = num;
    }

    /**
     * Does the square contain a mine? 
     */
    public boolean hasMine(){
        return mine;
    }

    /**
     * Is the square exposed already? 
     */
    public boolean isExposed(){
        return this.exposed;
    }

    /**
     * Is the square currently marked?
     */
    public boolean isMarked(){
        return this.marked;
    }

    /**
     * Set the square to be exposed? 
     */
    public void setExposed(){
        this.exposed = true;
    }

    /**
     * Set the square to be marked 
     */
    public void mark(){
        this.marked = true;
    }

    /**
     * Set the square to be not marked 
     */
    public void unMark(){
        this.marked = false;
    }

    /**
     * Draw the square 
     */
    public void draw(int row, int col){
        double x = MineSweeper.LEFT + col*MineSweeper.SQUARE_SIZE;
        double y = MineSweeper.TOP + row*MineSweeper.SQUARE_SIZE;
        if (exposed){ drawExposed(x, y, MineSweeper.SQUARE_SIZE); }
        else        { drawHidden(x, y, MineSweeper.SQUARE_SIZE); }
    }

    /**
     * Draw white outline and red number or mine 
     */
    private void drawExposed(double x, double y, double size){
        UI.setColor(EXPOSED_COLOUR);
        UI.fillRect(x, y, size, size);
        UI.setColor(Color.white);
        UI.drawRect(x, y, size, size);
        UI.setColor(Color.red);
        if (mine){
            UI.setFontSize(16);
            UI.drawString("X", x+size/2-5, y+size/2+7);
            UI.drawString("O", x+size/2-6, y+size/2+7);
            UI.drawString("X", x+size/2-4, y+size/2+7);
            UI.drawString("O", x+size/2-5, y+size/2+7);
            UI.setFontSize(12);
        }
        else if (adjacentMines > 0){
            UI.drawString(""+adjacentMines, x+size/2-5, y+size/2+5);
            UI.drawString(""+adjacentMines, x+size/2-4, y+size/2+5);
        }
    }

    /**
     * Fill dark green with red mark 
     */
    private void drawHidden(double x, double y, double size){
        UI.setColor(HIDDEN_COLOUR);
        UI.fillRect(x+1, y+1, size-2, size-2);
        UI.setColor(Color.black);
        UI.drawRect(x, y, size, size);
        if (marked){
            UI.setLineWidth(2);
            UI.setColor(Color.red);
            UI.drawLine(x+1, y+1, x+size-1, y+size-1);
            UI.drawLine(x+1, y+size-1, x+size-1, y+1);
            UI.setLineWidth(1);
        }
    }

}
