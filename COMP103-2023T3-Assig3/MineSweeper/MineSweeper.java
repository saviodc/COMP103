// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 3
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import javax.swing.JButton;

/**
 *  Simple 'Minesweeper' program.
 *  There is a grid of squares, some of which contain a mine.
 *  
 *  The user can click on a square to either expose it or to
 *  mark/unmark it.
 *  
 *  If the user exposes a square with a mine, they lose.
 *  Otherwise, it is uncovered, and shows a number which represents the
 *  number of mines in the eight squares surrounding that one.
 *  If there are no mines adjacent to it, then all the unexposed squares
 *  immediately adjacent to it are exposed (and so on)
 *
 *  If the user marks a square, then they cannot expose the square,
 *  (unless they unmark it first)
 *  When all the squares without mines are exposed, the user has won.
 */
public class MineSweeper {
    public static final int ROWS = 15;
    public static final int COLS = 15;

    public static final double LEFT = 10; 
    public static final double TOP = 10;
    public static final double SQUARE_SIZE = 20;

    // Fields
    private boolean marking;
    private boolean gameOver;

    private Square[][] squares;
    //private int[][] grid = new int[ROWS][COLS];
    private JButton mrkButton;
    private JButton expButton;
    Color defaultColor;
    private JButton suggestButton;

    /** Set up the GUI: buttons and mouse to play the game */
    public void setupGUI(){
        UI.setMouseListener(this::doMouse);
        UI.addButton("New Game", this::makeGrid);
        this.expButton = UI.addButton("Expose", ()->setMarking(false));
        this.mrkButton = UI.addButton("Mark", ()->setMarking(true));
        this.suggestButton = UI.addButton("Suggest", () -> this.suggest(readState()));
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.0);
        setMarking(false);
        makeGrid();
    }

    /** Respond to mouse events */
    public void doMouse(String action, double x, double y) {
        if (action.equals("released")){
            int row = (int)((y-TOP)/SQUARE_SIZE);
            int col = (int)((x-LEFT)/SQUARE_SIZE);
            if (row>=0 && row < ROWS && col >= 0 && col < COLS){
                if (marking) { mark(row, col);}
                else         { tryExpose(row, col); }
            }
        }
    }

    /**
     * Challenge method, reads the state of the game to the grid field
     */
    public int[][] readState(){
        //new grid to be fed into recommomend method with same dimensions as the game
        int[][] grid = new int[ROWS][COLS];
        //iterate through 'squares' to populate 'grid'
        for(int i = 0; i < squares.length ; i++){
            for(int j = 0; j < squares[0].length ; j++){
                if(!squares[i][j].isExposed())grid[i][j] = -1; //if not exposed give a -1
                else grid[i][j] = squares[i][j].getAdjacentMines(); //else the valiue
            }
        }
        return grid;
    }

    /**
     * Challenge helper method that suggests moves
     * This suggests player moves based on common patterns in the game 
     * The use of '+1' can bee seen throughout showing a shift from index to position 
     */
    public void suggest(int[][] currentState){
        
        int seen = 0;//this moves the list of suggestions down by the x axis and holds the value of how many suggestions there have been
        
        //this iteration checks if a mine is surrounded by a circle of revealed squares with adjacent squares, this means it has to be mine
        for(int i = 1; i < ROWS - 1; i++){
            for(int j = 1; j < COLS -1 ; j++){
                if(currentState[i][j] == -1){
                    if((currentState[i][j-1] > 0 && currentState[i+1][j-1] > 0&& currentState[i+1][j-1] > 0&& currentState[i][j+1] > 0
                        && currentState[i][j+1] > 0&& currentState[i-1][j+1] > 0&& currentState[i-1][j] > 0&& currentState[i+1][j] >0)){
                        UI.drawString("Square at "+ (i+1) +"x" + (j+1)+" is safe to mark as mine." , 350, 50 + seen);
                        UI.sleep(2000);
                        UI.eraseString("Square at "+ (i+1) +"x" + (j+1)+" is safe to mark as mine." , 350, 50 + seen);
                        seen += 15;
                    }
                }
            }
        }
        // this iteration checks for mines on the left most edge of the grid, if surrounded has to be a mine
        for(int i = 1; i < ROWS -1;i++){
            if(currentState[i][0] == -1){
                if(currentState[i][1] >0 && currentState[i-1][1] >0 && currentState[i+1][1] >0 && currentState[i-1][0] >0 && currentState[i+1][0] >0){
                    UI.drawString("The square at " + (i+1) + "x" + 1 + "is safe to mark as a mine", 350, 50 + seen);
                    UI.sleep(2000);
                    UI.eraseString("The square at " + (i+1) + "x" + 1 + "is safe to mark as a mine", 350, 50 + seen);
                    seen +=15;
                }
            }
        }

        //this iteration checks for the mines on the right most edge of the grid, if surrounded has to be a mine
        for(int i = 1; i < ROWS -1;i++){
            if(currentState[i][COLS-1] == -1){
                if(currentState[i][COLS-2] >0 && currentState[i-1][COLS-2] >0 && currentState[i+1][COLS-2] >0 && currentState[i-1][COLS-1] >0 && currentState[i+1][COLS-1] >0){
                    UI.drawString("The square at " + (i+1) + "x" + (COLS) + " is safe to mark as a mine", 350, 50 + seen);
                    UI.sleep(2000);
                    UI.eraseString("The square at " + (i+1) + "x" + (COLS) + " is safe to mark as a mine", 350, 50 + seen);
                    seen +=15;
                }
            }
        }
        //this iteration checks for the mines on the top most edge of the grid, if surrounded has to be a mine
        for(int j = 1; j < COLS -1; j++){
            if(currentState[0][j] == -1){
                if(currentState[0][j+1] > 0 && currentState[0][j-1] >0 && currentState[1][j] > 0 && currentState[1][j+1] > 0 && currentState[1][j-1] > 0){
                    UI.drawString("The square at " + 1 + "x" + (j+1) + " is safe to mark as a mine", 350, 50 + seen);
                    UI.sleep(2000);
                    UI.eraseString("The square at " + 1 + "x" + (j+1) + " is safe to mark as a mine", 350, 50 + seen);
                    seen +=15;
                }
            }
        }
        
        //this iteration checks for the mines on the bottom most edge of the grid, if surrounded has to be a mine
        for(int j = 1; j < COLS -1; j++){
            if(currentState[COLS-1][j] == -1){
                if(currentState[COLS-1][j+1] > 0 && currentState[COLS-1][j-1] >0 && currentState[COLS-2][j] > 0 && currentState[COLS-2][j+1] > 0 && currentState[COLS-2][j-1] > 0){
                    UI.drawString("The square at " + ROWS + "x" + (j+1) + " is safe to mark as a mine", 350, 50 + seen);
                    UI.sleep(2000);
                    UI.eraseString("The square at " + ROWS + "x" + (j+1) + " is safe to mark as a mine", 350, 50 + seen);
                    seen +=15;
                }
            }
        }
        
        //this iteration checks for common 3 square patterns and suggests potential mine placement that are in the line below
        for(int i = 0; i < ROWS -1; i++){//as it is suggesting downwards cannot check the bottom
            for(int j = 1; j < COLS - 1; j++){// checks all but cannot check the last or first index as a middle
                //This is a common 2, 3, 2 pattern that if there are blanks below it is likely this mine 
                if(currentState[i][j] == 3 && currentState[i][j-1] == 2 && currentState[i][j+1] == 2){
                    UI.drawString("The three squares from " + (j+1-1) + " to " + (j+1+1) + " on row " + (i+1+1) + "are likely all mines", 350, 50 + seen); 
                    UI.sleep(2000);
                    UI.eraseString("The three squares from " + (j+1-1) + " to " + (j+1+1) + " on row " + (i+1+1) + "are likely all mines", 350, 50 + seen);
                    seen += 15;
                }
                //This is a 1, 2, 1 pattern where the mines often have to be split below
                if(currentState[i][j] == 2 && currentState[i][j+1] == 1 && currentState[i][j-1]==1){
                    UI.drawString("On row " + (i+1+1) + " the squares "+ (j+1-1) + " & " + (j+1+1) + " are likely mines", 350, 50 + seen); 
                    UI.sleep(2000);
                    UI.eraseString("On row " + (i+1+1) + " the squares "+ (j+1-1) + " & " + (j+1+1) + " are likely mines", 350, 50 + seen); 
                    seen += 15;
                }
            }
        }

        //this iteration is to check for 4 in a row patterns and to suggest below
        for(int i = 0; i < ROWS -1; i++){
            for(int j = 1; j < COLS - 2; j++){
                //this checks the 2, 2 ,1, 1 pattern
                if(currentState[i][j] == 2 && currentState[i][j-1] == 2 && currentState[i][j+1] == 1 && currentState[i][j+2] == 1){
                    UI.drawString("On row " + (i+1+1) + " the squares "+ (j+1-1) + " & " + (j+1+2) + " are likely mines", 350, 50 + seen); 
                    UI.sleep(2000);
                    UI.eraseString("On row " + (i+1+1) + " the squares "+ (j+1-1) + " & " + (j+1+2) + " are likely mines", 350, 50 + seen); 
                    seen += 15;
                }
                //this checks the 1,2,2,1 pattern
                if(currentState[i][j] == 2 && currentState[i][j-1] == 1 && currentState[i][j+1] == 2 && currentState[i][j+2] == 1){
                    UI.drawString("On row " + (i+1+1) + " the squares "+ (j+1) + " & " + (j+1+1) + " are likely mines", 350, 50 + seen); 
                    UI.sleep(2000);
                    UI.eraseString("On row " + (i+1+1) + " the squares "+ (j+1) + " & " + (j+1+1) + " are likely mines", 350, 50 + seen);
                    seen += 15;
                }
            }
        }
    }

    // Other Methods
    /**
     * Mark (or unmark) the square.
     */
    public void mark(int row, int col){
        if(squares[row][col].isExposed()){
            return;
        }else if(!squares[row][col].isMarked()){
            squares[row][col].mark();
        }else{
            squares[row][col].unMark();
        }

        squares[row][col].draw(row, col);
    }

    /**
     * Respond to the player clicking on a square to expose it
     */
    public void tryExpose(int row, int col){
        if(!gameOver){
            //if square is already exposed or mark do not attempt
            if(squares[row][col].isExposed() || squares[row][col].isMarked()){
                return;
            }else if(squares[row][col].hasMine()){
                //if it has a mine have to lose 
                drawLose();
                gameOver = true;
                return;
            }else{
                exposeSquareAt(row, col);
            }
            //if the game is won, show & end game
            if (hasWon()){
                drawWin();
                gameOver = true;
            }
        }
    }

    /**
     *  Ensures that the square at row and col is exposed.
     */
    public void exposeSquareAt(int row, int col){
        //all base cases of reaching an appropriate square or a marked, exposed or mine square
        if(row >= squares.length || col >= squares[0].length  || row < 0 || col < 0) return;
        if(squares[row][col].isExposed())return;
        if(squares[row][col].hasMine()) return;
        if(squares[row][col].isMarked()) return;
        
        //if the click is on a regulare number just finish
        if(squares[row][col].getAdjacentMines() != 0){
            squares[row][col].setExposed();
            squares[row][col].draw(row, col);
        }
        //if the click is on a blank square 
        if(squares[row][col].getAdjacentMines() == 0){
            //set and draw exposed
            squares[row][col].setExposed();
            squares[row][col].draw(row, col);
            
            //checks (spreads) to the 8 surrounding squares
            exposeSquareAt(row-1, col -1);
            exposeSquareAt(row+1, col +1);
            exposeSquareAt(row+1, col -1);
            exposeSquareAt(row-1, col +1);
            exposeSquareAt(row -1, col);
            exposeSquareAt(row + 1, col);
            exposeSquareAt(row, col - 1);
            exposeSquareAt(row, col +1);
        }
    }

    /**
     * Returns true if the player has won:
     */
    public boolean hasWon(){
        //checks all squares, if there is a single squae that is covered and doesnt have a mine
        //the game is not won and returns
        for(Square[] rows : squares){
            for(Square square : rows){
                if((!square.isExposed()) && !square.hasMine()){
                    return false;
                }
            }
        }
        gameOver = true;
        return true;
    }

    // completed methods

    /** 
     * Respond to the Mark and Expose buttons
     */
    public void setMarking(boolean v){
        marking=v;
        if (marking) {
            mrkButton.setBackground(Color.red);
            expButton.setBackground(null);
        }
        else {
            expButton.setBackground(Color.red);
            mrkButton.setBackground(null);
        }
    }

    /**
     * Construct and draw a grid with random mines.
     * Compute the number of adjacent mines in each Square
     */
    public void makeGrid(){
        gameOver = false;
        UI.clearGraphics();
        this.squares = new Square[ROWS][COLS];
        for (int row=0; row < ROWS; row++){
            for (int col=0; col<COLS; col++){
                boolean isMine = Math.random()<0.1;     // approx 1 in 10 squares is a mine 
                this.squares[row][col] = new Square(isMine);
                this.squares[row][col].draw(row, col);
            }
        }
        // now compute the number of adjacent mines for each square
        for (int row=0; row<ROWS; row++){
            for (int col=0; col<COLS; col++){
                int count = 0;
                //look at each square in the neighbourhood.
                for (int r=Math.max(row-1,0); r<Math.min(row+2, ROWS); r++){
                    for (int c=Math.max(col-1,0); c<Math.min(col+2, COLS); c++){
                        if (squares[r][c].hasMine())
                            count++;
                    }
                }
                if (this.squares[row][col].hasMine())
                    count--;  // we weren't suppose to count this square, just the adjacent ones.

                this.squares[row][col].setAdjacentMines(count);
            }
        }
    }

    /**
     * Draw a message telling the player they have won
     */
    public void drawWin(){
        UI.setFontSize(28);
        UI.drawString("You Win!", LEFT + COLS*SQUARE_SIZE + 20, TOP + ROWS*SQUARE_SIZE/2);
        UI.setFontSize(12);
    }

    /**
     * Draw a message telling the player they have lost
     * and expose all the squares and redraw them
     */
    public void drawLose(){
        for (int row=0; row<ROWS; row++){
            for (int col=0; col<COLS; col++){
                squares[row][col].setExposed();
                squares[row][col].draw(row, col);
            }
        }
        UI.setFontSize(28);
        UI.drawString("You Lose!", LEFT + COLS*SQUARE_SIZE+20, TOP + ROWS*SQUARE_SIZE/2);
        UI.setFontSize(12);
    }

    /** 
     * Construct a new MineSweeper object
     * and set up the GUI
     */
    public static void main(String[] arguments){
        MineSweeper ms = new MineSweeper();
        ms.setupGUI();

        //WRITE HERE WHICH PARTS OF THE ASSIGNMENT YOU HAVE COMPLETED
        // so the markers know what to look for.
        UI.println("""
         I have done all the tasks including the AI challenge method
      
         --------------------
         """);

    }

}
