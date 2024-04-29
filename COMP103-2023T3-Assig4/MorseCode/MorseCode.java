// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 4
 * Name:
 * Username:
 * ID:
 */

/**
 * Implements a binary tree that represents the Morse code symbols, named after its inventor
 *   Samuel Morse.
 * Each Morse code symbol is formed by a sequence of dots and dashes.
 *
 * A Morse code chart has been provided with this assignment. This chart only contains the 26 letters
 * and 10 numerals. These are given in alphanumerical order. 
 *
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class MorseCode {

    public SymbolNode root = new SymbolNode(null,           // root of the morse code binary tree;
            new SymbolNode("E",                             
                new SymbolNode("I"),
                new SymbolNode("A")),
            new SymbolNode("T",                             
                new SymbolNode("N"),
                new SymbolNode("M")));
    /**
     * Setup the GUI and creates the morse code with characters up to 2 symbols
     */
    public static void main(String[] args){
        new MorseCode().setupGUI();

        //WRITE HERE WHICH PARTS OF THE ASSIGNMENT YOU HAVE COMPLETED
        // so the markers know what to look for.
        UI.println("""
        I have done all parts including challenge with the encode and attempted the tree drawer 
      
         --------------------
         """);

    }

    /**
     * Set up the interface
     */
    public void setupGUI(){
        UI.addButton("Print Tree", this::printTree);
        UI.addTextField("Decode ", this::decode);
        UI.addTextField("Add Code (Core)", this::addCodeCore);
        UI.addTextField("Add Code (Compl)", this::addCodeCompl);
        UI.addButton("Load File", this::loadFile);
        UI.addButton("Draw", this::drawTree);
        UI.addButton("Print Depth of Tree", this::printDepth);
        UI.addTextField("Encode", this::encode);
        UI.addButton("Reset", ()->{root = new SymbolNode();});
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1000,400);
        UI.setDivider(0.25);
    }

    /**
     * Challenge Method - This enacts the helper recursive method to find and match the code to the input
     * If not found prints not found
     */
    public void encode(String phrase){
        String out = encode("",root, phrase);
        if(out != null) UI.println("Encoded: " + out);
        else UI.println("No Match");
    }

    /**
     * Challenge Method - Traverse the tree appropriately and return the string value if found else returns null
     */
    public String encode(String soFar,SymbolNode n, String target){
        if(n != null){
            if(n != root && n.getSymbol() != null){
                if(n.getSymbol().equals(target)){
                    return soFar;
                }
            }
            //String to carry the code through
            String continueDash = soFar + "-";
            String continueDot = soFar + ".";
            String fromDash = encode(continueDash, n.getDashChild(), target);
            String fromDot = encode(continueDot, n.getDotChild(), target);
            //if a matching code is found return it
            if(fromDot != null){
                return fromDot;
            }
            if(fromDash != null){
                return fromDash;
            }
        }
        return null;
    }

    /**
     * Decode a code by starting at the top (root), and working
     * down the tree following the dot or dash nodes according to the
     * code
     */
    public void decode(String code){
        if ( ! isValidCode(code)){return;}
        SymbolNode start = root;
        //starts from root and traverses til and of code
        for(char c : code.toCharArray()){
            if(c == '.')start = start.getDotChild();
            else start = start.getDashChild();
            if(start == null) {
                UI.println("There is no symbol for " + code);
                return;
            }
        }
        UI.println(code + " corresponds to " +start.getSymbol());
    }

    /**  
     * Print out the contents of the decision tree in the text pane.
     * The root node should be at the top, followed by its "dot" subtree, and then
     * its "dash" subtree.
     * Each node should be indented by how deep it is in the tree.
     * Needs a recursive "helper method" which is passed a node and an indentation string.
     *  (The indentation string will be a string of space characters plus the morse code leading
     *  to the node)
     */
    public void printTree(){
        UI.clearText();
        printTree(root, 0,"");
    }

    /**
     * Recursive method that carries the code representation and the indentation appropriate to depth to traverse and print the values of the tree
     */
    public void printTree(SymbolNode node, int depth, String code){
        if(node == null) return;
        String space = "";
        //adding the indent by adding space(s) to the start depending on how far down
        for(int i = 0; i < depth; i++){
            space += " ";
        }
        if(node != root){
            UI.println(space + code + " = " + node.getSymbol());
        }else{
            UI.println();//blank line for root
        }
        printTree(node.getDotChild(), depth + 1, code+".");
        printTree(node.getDashChild(), depth + 1, code+"-");
    }

    /**
     * Add a new code to the tree (as long as it will be in a node just below an existing node).
     * Follows the code down the tree (like decode)
     * If it finds a node for the code, then it reports the current symbol for that code
     * If it it gets to a node where there is no child for the next . or - in the code then
     *  If this is the last . or - in the code, it asks for a new symbol and adds a new node
     *  If there is more than one . or - left in the code, it gives up and says it can't add it.
     * For example,
     *  If it is adding the code (.-.) and the tree has "A" (.-) but doesn't have (.-.),
     *   then it should ask for the symbol (R) add a child node with R
     *  If it is adding the code (.-..) and the tree has "A" (.-) but doesn't have (.-.),
     *   then it would not attempt to add a node for (.-..) (L) because that would require
     *   adding more than one node.
     */
    public void addCodeCore (String code) {
        SymbolNode lastNode = root;
        //goes through code to get to the node to add to if there is a break cannot add
        for(char c : code.toCharArray()){
            if(lastNode == null) {
                UI.println("Cannot add code to tree");
                return;
            }
            if(c == '.'){
                lastNode = lastNode.getDotChild();
            }
            else{
                lastNode = lastNode.getDashChild();  
            }
        }
        //if the node has a value do not replace
        if(lastNode != null) {
            UI.println("Node already contains values");
            return;
        }
        //asks for value off
        String value = UI.askString("What value would u like to place here?");
        //goes to one less and finds the node
        SymbolNode toUpdate = root;
        for(int i = 0; i < code.length() - 1; i++){
            if(code.charAt(i) == '.'){
                toUpdate = toUpdate.getDotChild();
            }else{
                toUpdate = toUpdate.getDashChild();
            }
        }
        //adds to appropriate child value
        if(code.charAt(code.length() - 1) == '-') {
            toUpdate.setDashChild(new SymbolNode(value));
        }
        else {
            toUpdate.setDotChild(new SymbolNode(value));
        }
    }

    // COMPLETION ======================================================
    /**
     * Grow the tree by allowing the user to add any symbol, whether there is a path leading to it.
     * Like addCodeCore, it starts at the top (root), and works its way down the tree
     *  following the dot or dash nodes according to the given sequence of the code.
     * If an intermediate node does not exist, it needs to be created with a text set to null.
     */
    public void addCodeCompl(String code) {
        SymbolNode lastNode = root;
        List<String> traversed = new ArrayList<>();
        //Same as core but adds 'bridging' null node
        for(char c : code.toCharArray()){
            if(c == '.'){
                if(lastNode.getDotChild() == null){
                    lastNode.setDotChild(new SymbolNode());
                }
                lastNode = lastNode.getDotChild();
            }
            else{
                if(lastNode.getDotChild() == null){
                    lastNode.setDashChild(new SymbolNode());
                }
                lastNode = lastNode.getDashChild();  
            }
        }

        if(lastNode.getDotChild() != null || lastNode.getDotChild() != null){
            UI.println("Node already contains values");
            return;
        }
        String value = UI.askString("What value would u like to place here?");
        SymbolNode toUpdate = root;
        for(int i = 0; i < code.length() - 1; i++){
            if(code.charAt(i) == '.'){
                toUpdate = toUpdate.getDotChild();
            }else{
                toUpdate = toUpdate.getDashChild();
            }
        }

        if(code.charAt(code.length() - 1) == '-') {
            toUpdate.setDashChild(new SymbolNode(value));
        }
        else {
            toUpdate.setDotChild(new SymbolNode(value));
        }

    }

    /** 
     * Load a collection of symbols and their codes from a file
     * Each line contains the symbol and the corresponding morse code.
     */
    public void loadFile () { 
        try
        {
            root = new SymbolNode(); // resets the root node as a blank node
            List<String> codes = Files.readAllLines(Path.of(UIFileChooser.open()));
            for(String element : codes){
                //gets the values and codes
                Scanner sc = new Scanner(element);
                String value = sc.next();
                String code = sc.next();

                SymbolNode start = root;
                
                for(char c : code.toCharArray()){
                    if(c == '.'){
                        if(start.getDotChild() == null){
                            start.setDotChild(new SymbolNode());
                        }
                        start = start.getDotChild();
                    }else{
                        if(start.getDashChild() == null){
                            start.setDashChild(new SymbolNode());
                        }
                        start = start.getDashChild();                        
                    }
                }
                //getting one less
                SymbolNode toUpdate = root;
                for(int i = 0; i < code.length() - 1; i++){
                    if(code.charAt(i) == '.'){
                        toUpdate = toUpdate.getDotChild();
                    }else{
                        toUpdate = toUpdate.getDashChild();
                    }
                }
                
                //allows to save the rest of the values so doesnt lose values already added
                SymbolNode saveDot = null;
                SymbolNode saveDash = null;
                if(start != null){
                    SymbolNode checkDot = start.getDotChild();
                    SymbolNode checkDash = start.getDashChild();
                    if(checkDot != null){
                        saveDot = checkDot;
                    }
                    if(checkDash != null){
                        saveDash = checkDash;
                    }
                }
                
                //adds the code
                if(code.charAt(code.length() - 1) == '-') {
                    toUpdate.setDashChild(new SymbolNode(value));
                    toUpdate = toUpdate.getDashChild();
                }
                else {
                    toUpdate.setDotChild(new SymbolNode(value));
                    toUpdate = toUpdate.getDotChild();
                }

                //and if there was a value restore as its children
                if(saveDot != null){
                    toUpdate.setDotChild(saveDot);
                }
                if(saveDash != null){
                    toUpdate.setDashChild(saveDash);
                }
            }
        }
        catch (Exception ioe)
        {
            UI.println(ioe);
        }

    }
    
    /**
     * Tries to draw Tree by calling recursive method
     */
    public void drawTree(){
        double height = 500 / getDepth(root, 0);
        drawTree(root, false, 0, getDepth(root, 0), 400, 10, 200, height); 
    }
    
    /**
     * Tries to draw tree recursively
     */
    public void drawTree(SymbolNode n, boolean dotChild, int depth, int totalDepth, double x, double y, double width, double height){
        if(n == null)return;
        if(n==root){
            UI.drawRect(x,y,20, 20);
            drawTree(n.getDotChild(), true, depth +1, totalDepth, x, y, width, height);
            drawTree(n.getDashChild(), false, depth + 1, totalDepth, x,y, width, height);
        }else{
            if(dotChild){
                UI.drawRect(x - width, y + height, 20, 20);
            }else{
                UI.drawRect(x + width, y + height, 20, 20);
            }   
            drawTree(n.getDotChild(), true, depth + 1, totalDepth, x - width, y + height, width / 2, height);
            drawTree(n.getDashChild(), false, depth +1, totalDepth, x + width, y + height, width /2, height);
        }
    }

    public void printDepth(){
        UI.println(getDepth(root, 0));
    }

    /**
     * Helper method that gets how many nodes deep for challenge tree drawing
     */
    public int getDepth(SymbolNode n, int depth){
        if(n == null)return 0;

        return Math.max(depth+1, Math.max(getDepth(n.getDashChild(), depth + 1), getDepth(n.getDotChild(), depth +1)));
    }

    // Utility methods ===============================================

    /**
     * Checks whether the code is a sequence of . and - only.
     */
    public boolean isValidCode (String code) {
        if (code.equals("")){
            UI.println("Code is empty");
            return false;
        }
        for (int index=0; index<code.length(); index++){
            char c = code.charAt(index);
            if (c != '-' && c != '.'){
                UI.println("Code has an invalid character: "+c);
                return false;
            }
        }
        return true;
    }
}
