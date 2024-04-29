// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 1
 * Name: 
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/** 
 * Sokoban
 */

public class Sokoban {
    private Cell[][] cells;             // the array representing the warehouse
    private int rows;                   // the height of the warehouse
    private int cols;                   // the width of the warehouse
    private int level = 1;              // current level 

    private Position workerPos;         // the position of the worker
    private String workerDir = "left";  // the direction the worker is facing

    private Stack<ActionRecord> history = new Stack<>();        //stack of all moves user makes
    private Stack<ActionRecord> undoneMoves = new Stack<>();    //stack of all moves that have been undone 
    
    /**
     *  Constructor: load the 0th level.
     */
    public Sokoban() {
        doLoad();
    }
                            
    /** 
     *  Moves the worker in the given direction, if possible.
     *  If there is box in front of the Worker and a space in front of the box,
     *  then push the box.
     *  Otherwise, if the worker can't move, do nothing.
     */
    public void moveOrPush(String direction) {
        workerDir = direction;                       // turn worker to face in this direction

        Position nextP = workerPos.next(direction);  // where the worker would move to
        Position nextNextP = nextP.next(direction);  // where a box would be pushed to

        // is there a box in that direction which can be pushed?
        if ( cells[nextP.row][nextP.col].hasBox() &&
        cells[nextNextP.row][nextNextP.col].isFree() ) { 
            push(direction);
            history.push(new ActionRecord("push", direction)); //adding to move history
            undoneMoves.clear();    //clears the undone as moving elsewhere means redoing chain would not work  
            if (isSolved()) { reportWin(); }
        }
        // is the next cell free for the worker to move into?
        else if ( cells[nextP.row][nextP.col].isFree() ) { 
            move(direction);
            history.push(new ActionRecord("move", direction)); //adding to move history
            undoneMoves.clear();    //clears the undone as moving elsewhere means redoing chain would not work  
        }
    }

    /**
     * Moves the worker into the new position (guaranteed to be empty) 
     * @param direction the direction the worker is heading
     */
    public void move(String direction) {
        drawCell(workerPos);                   // redisplay cell under worker
        workerPos = workerPos.next(direction); // put worker in new position
        drawWorker();                          // display worker at new position
        Trace.println("Move " + direction);    // for debugging
    }

    /**
     * Push: Moves the Worker, pushing the box one step 
     *  @param direction the direction the worker is heading
     */
    public void push(String direction) {
        Position boxPos = workerPos.next(direction);   // where box is
        Position newBoxPos = boxPos.next(direction);   // where box will go

        cells[boxPos.row][boxPos.col].removeBox();     // remove box from current cell
        cells[newBoxPos.row][newBoxPos.col].addBox();  // place box in its new position

        drawCell(workerPos);                           // redisplay cell under worker
        drawCell(boxPos);                              // redisplay cell without the box
        drawCell(newBoxPos);                           // redisplay cell with the box

        workerPos = boxPos;                            // put worker in new position
        drawWorker();                                  // display worker at new position

        Trace.println("Push " + direction);   // for debugging
    }

    /**
     * Pull: (could be useful for undoing a push)
     *  move the Worker in the direction,
     *  pull the box into the Worker's old position
     */
    public void pull(String direction) {
         Position boxPos = workerPos.next(opposite(direction));   // where box is
         Position newBoxPos = workerPos;                // where box will go 
         
         cells[boxPos.row][boxPos.col].removeBox();     // remove box from current cell
         cells[newBoxPos.row][newBoxPos.col].addBox();  // place box in its new position
         
         drawCell(workerPos);                           // redisplay cell under worker
         drawCell(boxPos);                              // redisplay cell without the box
         drawCell(newBoxPos);                           // redisplay cell with the box
         
         //moves to right position
         workerPos = workerPos.next(direction);
         drawWorker();
    }

    /**
     * Report a win by flickering the cells with boxes
     */
    public void reportWin(){
        for (int i=0; i<12; i++) {
            for (int row=0; row<cells.length; row++)
                for (int column=0; column<cells[row].length; column++) {
                    Cell cell=cells[row][column];

                    // toggle shelf cells
                    if (cell.hasBox()) {
                        cell.removeBox();
                        drawCell(row, column);
                    }
                    else if (cell.isEmptyShelf()) {
                        cell.addBox();
                        drawCell(row, column);
                    }
                }

            UI.sleep(100);
        }
    }

    /**
     *  Returns true if the warehouse is solved, 
     *  i.e., all the shelves have boxes on them 
     */
    public boolean isSolved() {
        for(int row = 0; row<cells.length; row++) {
            for(int col = 0; col<cells[row].length; col++)
                if(cells[row][col].isEmptyShelf())
                    return  false;
        }
                            
        return true;
    }

    /**
     * Returns the direction that is opposite of the parameter
     * useful for undoing!
     */
    public String opposite(String direction) {
        if ( direction.equals("right")) return "left";
        if ( direction.equals("left"))  return "right";
        if ( direction.equals("up"))    return "down";
        if ( direction.equals("down"))  return "up";
        throw new RuntimeException("Invalid  direction");
    }


    // Drawing the warehouse
    private static final int LEFT_MARGIN = 40;
    private static final int TOP_MARGIN = 40;
    private static final int CELL_SIZE = 25;

    /**
     * Draw the grid of cells on the screen, and the Worker 
     */
    public void drawWarehouse() {
        UI.clearGraphics();
        // draw cells
        for(int row = 0; row<cells.length; row++)
            for(int col = 0; col<cells[row].length; col++)
                drawCell(row, col);

        drawWorker();
    }

    /**
     * Draw the cell at a given position
     */
    private void drawCell(Position pos) {
        drawCell(pos.row, pos.col);
    }

    /**
     * Draw the cell at a given row,col
     */
    private void drawCell(int row, int col) {
        double left = LEFT_MARGIN+(CELL_SIZE* col);
        double top = TOP_MARGIN+(CELL_SIZE* row);
        cells[row][col].draw(left, top, CELL_SIZE);
    }

    /**
     * Draw the worker at its current position.
     */
    private void drawWorker() {
        double left = LEFT_MARGIN+(CELL_SIZE* workerPos.col);
        double top = TOP_MARGIN+(CELL_SIZE* workerPos.row);
        UI.drawImage("worker-"+workerDir+".gif",
            left, top, CELL_SIZE,CELL_SIZE);
    }

    /**
     * Load a grid of cells (and Worker position) for the current level from a file
     */
    public void doLoad() {
        Path path = Path.of("warehouse" + level + ".txt");
        
        if (! Files.exists(path)) {
            UI.printMessage("Run out of levels- Back to Level 1!");
            UI.sleep(100);
            level = 1;
            doLoad();
            return;
        }
        List<String> lines = null;
        try {
            lines = Files.readAllLines(path);
        }
        catch(IOException e) {
            UI.println("File error: " + e);
            return;
        }

        int rows = lines.size();
        cells = new Cell[rows][];

        for(int row = 0; row < rows; row++) {
            String line = lines.get(row);
            int cols = line.length();
            cells[row]= new Cell[cols];
            for(int col = 0; col < cols; col++) {
                char ch = line.charAt(col);
                if (ch=='w'){
                    cells[row][col] = new Cell("empty");
                    workerPos = new Position(row,col);
                }
                else if (ch=='.') cells[row][col] = new Cell("empty");
                else if (ch=='#') cells[row][col] = new Cell("wall");
                else if (ch=='s') cells[row][col] = new Cell("shelf");
                else if (ch=='b') cells[row][col] = new Cell("box");
                else {
                    throw new RuntimeException("Invalid char at "+row+","+col+"="+ch);
                }
            }
        }
        drawWarehouse();
        UI.printMessage("Level "+level+": Push the boxes to their target positions. Use buttons or put mouse over warehouse and use keys (arrows, wasd, ijkl, u, r)");
        history.clear(); //clears move history from game
        undoneMoves.clear();
    }

    /**
     * Add the buttons and set the key listener.
     */
    public void setupGUI(){
        UI.addButton("New Level",       () -> {level++; doLoad();});
        UI.addButton("Restart Level",   this::doLoad);
        UI.addButton("left",            () -> {moveOrPush("left");});
        UI.addButton("up",              () -> {moveOrPush("up");});
        UI.addButton("down",            () -> {moveOrPush("down");});
        UI.addButton("right",           () -> {moveOrPush("right");});
        UI.addButton("Undo", this::undo);
        UI.addButton("Redo", this::redo);
        UI.addButton("Quit",            UI::quit);
        UI.setMouseListener(this::doMouse);
        UI.setKeyListener(this::doKey);
        UI.setDivider(0.0);
    }

    //fields for storing which way the worker moves for the click
    //only used for challenge
    private String directionX = ""; //worker moving left or right
    private String directionY = ""; //worker moving up or down
    /**
     * Mouse action handler for challenge
     * This only works for moving up and down 'corridoors' and cannot avoid boxes
     * Also have not guarded against clicking out of the map so that only is a problem for challenge works fine if all is normal
     */
    public void doMouse(String action, double x, double y){
        switch (action) {
            case "released" ->{
               // gets the relative position of the click 
               Position positionOfClick = new Position(getRow(y), getCol(x));
               //if there is a path down the same row as the worker
               if(positionOfClick.col == workerPos.col && traverseCol(positionOfClick.row, positionOfClick.col)){
                //keeps on moving it to he location of click
                   while(positionOfClick.row != workerPos.row){
                   moveOrPush(directionY);
                }
                return;//just so both dont happen at the same time
               }
               //if there is a path down the same column as the worker
               if(traverseRow(positionOfClick.row, positionOfClick.col) && positionOfClick.row == workerPos.row){
                   while(positionOfClick.col != workerPos.col){
                       moveOrPush(directionX);
                   }
                   return;
               }
            }
        }
        
    }
    
    
    
    /**
     * Attempt at recursive check of simple traversal with no obstacles in between on the same column
     * hence the name traverse column
     * Return boolean of if it is possible to move down the column
     * Just learning recursion so feedback on this would be much appreciated please
     */
    public boolean traverseCol(int row, int col){
        //base case - if the row is the same as the row of the worker return true
        if((row == workerPos.row)){
            return true;
        }
        //if at any point the path is not free return false 
        if(!cells[row][col].isFree()){
            return false;
        }
        
        //two recursive steps depending on having to move up or down
        if(workerPos.row > row){
            directionY = "up";
            return traverseCol(row +1, col);
        }else{
            directionY = "down";
            return traverseCol(row -1, col);
        }        
    }
    
    /**
     * Same as traverseCol, an attempt to solve this problem recursively just trying to see if there is a free path down the row
     * Returns boolean of if it is possible to move down the column
     */
    public boolean traverseRow(int row, int col){
        //base case - if col is the same as the workers column then return true
        if((col == workerPos.col)){
            return true;
        }
        //if at any point the path is not free return false
        if(!cells[row][col].isFree()){
            return false;
        }
        
        //two recursive steps depending on if worker has to move left or right
        if(workerPos.col > col){
            directionX = "left";
            return traverseRow(row, col+1);
        }else{
            directionX = "right";
            return traverseRow(row, col-1);
        }
    }
    
    /**
     * Gets thecolumn of the click from x calculation
     * does not guard for clicking too far down off the map 
     */
    public int getCol(double x){
        if(x <= 40){
            return -1 ;
        }
        int col = (int)((x - 40) / 25.0); //-40 to allow for the buffer,  25 as the size of each col
        return col;
    }
    
    /**
     * Gets the row of the click on the map from y calculation
     * does not guard from clicking too far right off the map
     */
    public int getRow(double y){
        if(y <= 40){
            return -1;
        }
        int row = (int)((y - 40) / 25.0); //-40 to allow for the buffer, 25 as the size of each row
        return row;
    }
    /**
     * Redo button that pops the last undone move, adds to history and appropriately does a redo of a move that was undone
     */
    public void redo(){
        if(!undoneMoves.empty()){
            history.add(undoneMoves.pop()); //removes from top of undone moves and adds to history like doing it again
        }else{
            return;
        }
        //only if redo should be implemented, will read from history moves to do appropriate move
        if(history.peek().isMove()){
            String direction = history.peek().direction();
            move(direction);
        }else{
            String direction = history.peek().direction();
            push(direction);
        }
    }
    
    /**
     * Undo button that pops the top action of the stack and appropriately does undo
     */
    public void undo(){
        if(!history.empty()){
            undoneMoves.add(history.pop()); //removes move from history and adds to undone moves
        }else{
            return;
        }
        //only if undone should be implemented, will read by peeking from undone moves to do appropriate move
        if(undoneMoves.peek().isMove()){
            String direction = undoneMoves.peek().direction();
            move(opposite(direction));
        }else{
            String direction = undoneMoves.peek().direction();
            pull(opposite(direction));
        }
    }
    
    /**
     * Respond to key actions
     */
    public void doKey(String key) {
        key = key.toLowerCase();
        if (key.equals("i")|| key.equals("w") ||key.equals("up")) {
            moveOrPush("up");
        }
        else if (key.equals("k")|| key.equals("s") ||key.equals("down")) {
            moveOrPush("down");
        }
        else if (key.equals("j")|| key.equals("a") ||key.equals("left")) {
            moveOrPush("left");
        }
        else if (key.equals("l")|| key.equals("d") ||key.equals("right")) {
            moveOrPush("right");
        }else if(key.equals("r")){
            redo();
        }else if(key.equals("u")){
            undo();
        }
    }
    
    public static void main(String[] args) {
        Sokoban skb = new Sokoban();
        skb.setupGUI();
    }
}
