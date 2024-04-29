// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 4
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

/**
 * A Binary Tree is a data structure in which each node has at most two
 * children.
 * Each node has a field to hold the text representing the symbol, if known,
 * or null otherwise.
 * The combination of dots and dashes leading to this symbol represents its
 * morse code.
 * The two possible nodes are dot and dash.
 */

public class SymbolNode{

    private String symbol;
    private SymbolNode dotChild;
    private SymbolNode dashChild;

    /**
     * Construct a new node with a null symbol and no children
     */
    public SymbolNode(){
    }

    /**
     * Construct a new node with a symbol and no children
     */
    public SymbolNode(String symb){
        setSymbol(symb);
    }

    /**
     * Construct a new node with symbol and two children
     */
    public SymbolNode(String symb, SymbolNode dotCh, SymbolNode dashCh){
        setSymbol(symb);
        dotChild = dotCh;
        dashChild = dashCh;
    }

    /**
     * Get the symbol of a node
     */
    public String getSymbol(){return symbol;}

    /**
     * Get the dotChild of a node
     */
    public SymbolNode getDotChild(){return dotChild;}

    /**
     * Get the dashChild of a node
     */
    public SymbolNode getDashChild(){return dashChild;}


    /**
     * Set the Symbol of a node
     */
    public void setSymbol(String symb ){
        symbol = (symb == null)?null:symb.toUpperCase();
    }

    /**
     * Set the dotChild of a node
     */
    public void setDotChild(SymbolNode dotCh){
        dotChild = dotCh;
    }

    /**
     * Set the dashChild of a node
     */
    public void setDashChild(SymbolNode dashCh){
        dashChild = dashCh;
    }

    public static final int WIDTH = 18;
    public static final int HEIGHT = 18;

    /**
     * Draw the node (as a box with the symbol) on the graphics pane
     * (x,y) is the center of the box
     * The box should be WIDTH wide, and HEIGHT high.
     */
    public void draw(double x, double y){
        double left = x-WIDTH/2;
        String toDraw;
        if (symbol == null) toDraw = "";
        else                toDraw = symbol;

        UI.eraseRect(left, y-HEIGHT/2, WIDTH, HEIGHT);  // to clear anything behind it
        UI.drawRect(left, y-HEIGHT/2, WIDTH, HEIGHT);
        UI.drawString(toDraw, left+4, y+6);             // assume height of symbols = 12
    }

}
