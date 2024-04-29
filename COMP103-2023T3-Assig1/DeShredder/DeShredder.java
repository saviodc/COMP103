// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 1
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * DeShredder allows a user to sort fragments of a shredded document ("shreds") into strips, and
 * then sort the strips into the original document.
 * The program shows
 *   - a list of all the shreds along the top of the window, 
 *   - the working strip (which the user is constructing) just below it.
 *   - the list of completed strips below the working strip.
 * The "rotate" button moves the first shred on the list to the end of the list to let the
 *  user see the shreds that have disappeared over the edge of the window.
 * The "shuffle" button reorders the shreds in the list randomly
 * The user can use the mouse to drag shreds between the list at the top and the working strip,
 *  and move shreds around in the working strip to get them in order.
 * When the user has the working strip complete, they can move
 *  the working strip down into the list of completed strips, and reorder the completed strips
 *
 */
public class DeShredder {

    // Fields to store the lists of Shreds and strips.  These should never be null.
    private List<Shred> allShreds = new ArrayList<Shred>();    //  List of all shreds
    private List<Shred> workingStrip = new ArrayList<Shred>(); // Current strip of shreds
    private List<List<Shred>> completedStrips = new ArrayList<List<Shred>>();

    // Constants for the display and the mouse
    public static final double LEFT = 20;       // left side of the display
    public static final double TOP_ALL = 20;    // top of list of all shreds 
    public static final double GAP = 5;         // gap between strips
    public static final double SIZE = Shred.SIZE; // size of the shreds

    public static final double TOP_WORKING = TOP_ALL+SIZE+GAP;
    public static final double TOP_STRIPS = TOP_WORKING+(SIZE+GAP);

    //Fields for recording where the mouse was pressed  (which list/strip and position in list)
    // note, the position may be past the end of the list!
    private List<Shred> fromStrip;   // The strip (List of Shreds) that the user pressed on
    private int fromPosition = -1;   // index of shred in the strip
    private boolean loaded;         // checks if a file has been loaded in
    /**
     * Initialises the UI window, and sets up the buttons. 
     */
    public void setupGUI() {
        UI.addButton("Load library",   this::loadLibrary);
        UI.addButton("Rotate",         this::rotateList);
        UI.addButton("Shuffle",        this::shuffleList);
        UI.addButton("Complete Strip", this::completeStrip);
        UI.addButton("Save Image", this::enactSave);
        UI.addButton("Suggest", this::doSuggest);
        UI.addButton("Quit",           UI::quit);
        UI.setMouseListener(this::doMouse);
        UI.setWindowSize(1000,800);
        UI.setDivider(0);
    }

    /**
     * Asks user for a library of shreds, loads it, and redisplays.
     * Uses UIFileChooser to let user select library
     * and finds out how many images are in the library
     * Calls load(...) to construct the List of all the Shreds
     */
    public void loadLibrary(){
        try{
            Path filePath = Path.of(UIFileChooser.open("Choose first shred in directory"));
            Path directory = filePath.getParent(); //subPath(0, filePath.getNameCount()-1);
            int count=1;
            while(Files.exists(directory.resolve(count+".png"))){ count++; }
            //loop stops when count.png doesn't exist
            count = count-1;
            load(directory, count);   // YOU HAVE TO COMPLETE THE load METHOD
            loaded = true;
        }catch (Exception e){
            UI.println("Please select valid file");
        }

        display();
    }

    /**
     * Empties out all the current lists (the list of all shreds,
     * the working strip, and the completed strips).
     * Loads the library of shreds into the allShreds list.
     * Parameters are the directory containing the shred images and the number of shreds.
     * Each new Shred needs the directory and the number/id of the shred.
     */
    public void load(Path dir, int count) {
        allShreds.clear();
        workingStrip.clear();
        completedStrips.clear();
        //iterates through and adds every shred to the default strip (allShreds)
        for(int i = 1; i <= count; i++){
            allShreds.add(new Shred(dir, i));
        }
    }

    /**
     * Rotate the list of all shreds by one step to the left
     * and redisplay;
     * Should not have an error if the list is empty
     * (Called by the "Rotate" button)
     */
    public void rotateList(){
        if(allShreds.size() == 0){
            return;
        }
        //removing the first shifts all left then add first to the end to rotate it around
        Shred copy = allShreds.remove(0);
        allShreds.add(copy);
        display();
    }

    /**
     * Shuffle the list of all shreds into a random order
     * and redisplay;
     */
    public void shuffleList(){
        //makes a blank list of Shreds to fill shreds into randomly
        List<Shred> shuffled = new ArrayList<>();
        for(int i = 0; i < allShreds.size(); i++){
            shuffled.add(i,null);
        }
        
        //iterating through the list, adding a shred to a random position in the list
        for(int i = 0; i < allShreds.size();){
            int randomPosition = (int) (Math.random() * allShreds.size());
            //only adding if there is a space to add
            if(shuffled.get(randomPosition) == null){
                shuffled.set(randomPosition,allShreds.get(i));
                i++; // only move to next shred if added
            }
        }
        allShreds = shuffled; // set strip to the shuffled copy 
        display();
    }

    /**
     * Move the current working strip to the end of the list of completed strips.
     * (Called by the "Complete Strip" button)
     */
    public void completeStrip(){
        if(this.workingStrip == null || this.workingStrip.isEmpty()){
            return;
        }
        List<Shred> copy = new ArrayList<>();
        copy.addAll(this.workingStrip);
        completedStrips.add(copy);
        this.workingStrip.clear();
        display();
    }

    /**
     * Simple Mouse actions to move shreds and strips
     *  User can
     *  - move a Shred from allShreds to a position in the working strip
     *  - move a Shred from the working strip back into allShreds
     *  - move a Shred around within the working strip.
     *  - move a completed Strip around within the list of completed strips
     *  - move a completed Strip back to become the working strip
     *    (but only if the working strip is currently empty)
     * Moving a shred to a position past the end of a List should put it at the end.
     * You should create additional methods to do the different actions - do not attempt
     *  to put all the code inside the doMouse method - you will lose style points for this.
     * Attempting an invalid action should have no effect.
     * Note: doMouse uses getStrip and getColumn, which are written for you (at the end).
     * You should not change them.
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("pressed")){
            fromStrip = getStrip(y);      // the List of shreds to move from (possibly null)
            fromPosition = getColumn(x);  // the index of the shred to move (may be off the end)
        }
        if (action.equals("released")){

            List<Shred> toStrip = getStrip(y); // the List of shreds to move to (possibly null)
            int toPosition = getColumn(x);     // the index to move the shred to (may be off the end)
            // perform the correct action, depending on the from/to strips/positions
            
            // guards for 'stray drag' which would throw an exception 
            if(toPosition <0 || !loaded){
                return;
            }
            
            // will invoke either method based on pattern of mouse movement (drag)
            if(fromStrip == toStrip){
                if (fromStrip == null){
                    return;
                }
                if(fromStrip.isEmpty()){
                    return;
                }
                shiftAround(toStrip, toPosition);
            }else{
                moveTo(toStrip, toPosition);
            }

            display();
        }
    }

    // Additional methods to perform the different actions, called by doMouse
    /**
     * Handles moving a shred (or a completed strip) between strips 
     */
    public void moveTo(List<Shred> toStrip, int toPosition){
        if(fromStrip == null){
            return;
        }
        // if dragging from an index that doesnt contain anything will not do anything
        if(fromPosition >= fromStrip.size()){
            return;
        }
        
        if((fromStrip == this.allShreds) && (toStrip == this.workingStrip)){
            Shred copy = this.allShreds.remove(this.fromPosition);
            //either dragging to the end if index dragged to is out bound or dragging to correct index
            if(toPosition > this.workingStrip.size()-1){
                this.workingStrip.add(copy);
            }else{
                this.workingStrip.add(toPosition, copy);
            }   
        }else if((fromStrip == this.workingStrip) && (toStrip == this.allShreds)){
            Shred copy = this.workingStrip.remove(this.fromPosition);
            if(toPosition > this.allShreds.size()-1){
                this.allShreds.add(copy);
            }
            else{
                this.allShreds.add(toPosition, copy);
            }
        }else if(completedStrips.contains(fromStrip)){
            if(completedStrips.contains(toStrip)){
                int indexToMove = completedStrips.indexOf(toStrip);
                int indexToGetFrom = completedStrips.indexOf(fromStrip);
                //swaps arround using the set methods return functionailty
                completedStrips.set(indexToGetFrom,completedStrips.set(indexToMove, fromStrip));
            }else if(toStrip == workingStrip && workingStrip.isEmpty()){
                List<Shred> copy = fromStrip;
                workingStrip = copy;
                completedStrips.remove(fromStrip);
            }
        }

    }

    /**
     * Moves a shred across the same list by removing it and adding it to the relevant index
     */
    public void shiftAround(List<Shred> toStrip, int toPosition){
        /*if(toStrip == null){
            return;
        }*/
        if(fromPosition >= fromStrip.size()){
            return;
        }
        int size = toStrip.size();
        if (toStrip == this.allShreds){
            Shred copy = this.allShreds.remove(this.fromPosition);
            if(toPosition < size){
                this.allShreds.add(toPosition, copy);
            }else{
                this.allShreds.add(size - 1, copy);
            }
        }
        
        if (toStrip == this.workingStrip && (this.workingStrip.get(this.fromPosition) != null || !this.workingStrip.isEmpty())){
            Shred copy = this.workingStrip.remove(this.fromPosition);
            if(toPosition < size){
                this.workingStrip.add(toPosition, copy);
            }else{
                this.workingStrip.add(size - 1, copy);
            }
        }
    }

    //=============================================================================
    // Completed for you. Do not change.
    // loadImage and saveImage may be useful for the challenge.

    /**
     * Displays the remaining Shreds, the working strip, and all completed strips
     */
    public void display(){
        UI.clearGraphics();

        // list of all the remaining shreds that haven't been added to a strip
        double x=LEFT;
        for (Shred shred : allShreds){
            UI.setColor(Color.black);
            shred.drawWithBorder(x, TOP_ALL);
            x+=SIZE;
        }

        //working strip (the one the user is workingly working on)
        x = LEFT;
        for (Shred shred : workingStrip){
            shred.draw(x, TOP_WORKING);
            x+=SIZE;
        }
        UI.setColor(Color.red);
        UI.drawRect(LEFT-1, TOP_WORKING-1, SIZE*workingStrip.size()+2, SIZE+2);
        UI.setColor(Color.black);

        //completed strips
        double y = TOP_STRIPS;
        for (List<Shred> strip : completedStrips){
            x = LEFT;
            for (Shred shred : strip){
                shred.draw(x, y);
                x+=SIZE;
            }
            UI.drawRect(LEFT-1, y-1, SIZE*strip.size()+2, SIZE+2);
            y+=SIZE+GAP;
        }
    }

    /**
     * Returns which column the mouse position is on.
     * This will be the index in the list of the shred that the mouse is on, 
     * (or the index of the shred that the mouse would be on if the list were long enough)
     */
    public int getColumn(double x){
        return (int) ((x-LEFT)/(SIZE));
    }

    /**
     * Returns the strip that the mouse position is on.
     * This may be the list of all remaining shreds, the working strip, or
     *  one of the completed strips.
     * If it is not on any strip, then it returns null.
     */
    public List<Shred> getStrip(double y){
        int row = (int) ((y-TOP_ALL)/(SIZE+GAP));
        if (row<=0){
            return allShreds;
        }
        else if (row==1){
            return workingStrip;
        }
        else if (row-2<completedStrips.size()){
            return completedStrips.get(row-2);
        }
        else {
            return null;
        }
    }

    /**
     * Load an image from a file and return as a two-dimensional array of Color.
     * Maybe useful for the challenge. Not required for the core or completion.
     */
    public Color[][] loadImage(String imageFileName) {
        if (imageFileName==null || !Files.exists(Path.of(imageFileName))){
            return null;
        }
        try {
            BufferedImage img = ImageIO.read(Files.newInputStream(Path.of(imageFileName)));
            int rows = img.getHeight();
            int cols = img.getWidth();
            Color[][] ans = new Color[rows][cols];
            for (int row = 0; row < rows; row++){
                for (int col = 0; col < cols; col++){                 
                    Color c = new Color(img.getRGB(col, row));
                    ans[row][col] = c;
                }
            }
            return ans;
        } catch(IOException e){UI.println("Reading Image from "+imageFileName+" failed: "+e);}
        return null;
    }

    /**
     * Save a 2D array of Color as an image file
     * Maybe useful for the challenge. Not required for the core or completion.
     */
    public  void saveImage(Color[][] imageArray, String imageFileName) {
        int rows = imageArray.length;
        int cols = imageArray[0].length;
        BufferedImage img = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Color c =imageArray[row][col];
                img.setRGB(col, row, c.getRGB());
            }
        }
        try {
            if (imageFileName==null) { return;}
            ImageIO.write(img, "png", Files.newOutputStream(Path.of(imageFileName)));
        } catch(IOException e){UI.println("Image reading failed: "+e);}

    }


    /**
     * Saves progress of user as ana image by creating a large 2D-array depending on the completed strips
     * Then uses the load image to turn the appropriate strips to 2D array
     * Then adds the various values to the larger array
     */
    public Color[][] saveCompleted(){
        int largest = 0;
        // this loop gets which strip has the most shreds, so the dimension of the saved accomodates for the largest
        for(List<Shred> s: completedStrips){ 
            int count = s.size();
            if(count > largest){
                largest = count;
            }
        }
        //A large 2D array to accomodate for the size of the completed strips
        Color[][] output = new Color[completedStrips.size() * 40][largest * 40];
        
        //getting the files to read for the larger array
        List<List<String>> fileNames = new ArrayList<>();
        for(List<Shred> s: completedStrips){
            ArrayList<String> names = new ArrayList<>();
            int i = 0;
            for(Shred shred: s){
                names.add(i, shred.getFileName());
                i++;
            }
            fileNames.add(names);
        }
                
        //these variables allow for remembering how far down and right to move in terms of pixels/indices 
        //this is because the each shred is 40x40 so have to move by 40 at appropriate time
        int rowOfFinal = 0;
        int columnOfFinal = 0;
        for(List<String> names :fileNames){
            for(String name : names){
                Color[][] currentShred = loadImage(name);
                for(int i = 0; i < currentShred.length; i++){
                    for(int j = 0; j < currentShred[0].length; j++){
                        output[rowOfFinal + i][columnOfFinal+j] = currentShred[i][j];
                    }
                }
                columnOfFinal += 40;
            }
            rowOfFinal +=40;
            columnOfFinal = 0;
        }

        // 'pads' in all blank indices/pixels to white 
        for(int i = 0; i < output.length; i++){
            for(int j = 0; j < output[0].length; j++){
                if(output[i][j] == (null)){
                    output[i][j] = new Color(255, 255, 255);
                }
            }
        }

        return output;
    }

    /**
     * Performs the saveImage method by inputting the output of the saveCompleted and a user choosen file
     * Printing out a message alerting the user if the save occured or not
     */
    public void enactSave(){
        if(completedStrips.size() ==0){
            UI.println("No completed strips to save!");
            return;
        }
        String outfile = UI.askString("Title to save under: ") + ".png";
        saveImage(saveCompleted(), outfile);
        UI.println("Completed Strips Saved");
    }

    /**
     * Suggests the shred to add to strip in terms of match with the last domino in the active hand
     * Prints out what and how many could match, and also highlighting with blue outline
     */
    public void doSuggest(){
        //checking for appropriate use of suggestion
        if(workingStrip == null || workingStrip.isEmpty()){
            UI.println("Add shred to active hand for a suggestion!");
            return;
        }
        if(allShreds == null || allShreds.isEmpty()){
            UI.println("No shreds in hand to suggest!");
            return;
        }
        
        //getting the last shred in the active hand and loading it into an array to test
        Color[][] shredToTest = loadImage(workingStrip.get(workingStrip.size() - 1).getFileName());
        
        //mapping shreds to their index
        Map<Shred, Integer> shredsThatMatch = new HashMap<>();
        
        int atIndex = 0; //using for each so need a count to read 
        for(Shred s : allShreds){
            Color[][] checkShredColors = loadImage(s.getFileName());
            int matchesLeft = 0;
            List<Integer> indiecesOfMatch = new ArrayList<>();
            //checking left side the shred to right of the active shred
            for(int i = 0; i < 2; i++){
                for(int j = 0; j < checkShredColors.length; j++){
                    if (shredToTest[j][39 - 1 + i].equals(checkShredColors[j][i])){
                        matchesLeft++;
                    }
                }
            }
            //set threshold for how many that match for there to be a potential match to be 70 
            //this threshold seemed to work the best, mainly for pictures specificially
            if(matchesLeft >= 70){
                shredsThatMatch.put(s, atIndex);
            }
            atIndex++;
        }
        
        //creating a set of all that could match
        Set<Shred> allThatMatch = new HashSet<>();
        if(!shredsThatMatch.isEmpty()){
             allThatMatch = shredsThatMatch.keySet(); //set of Shreds that we want to tell the user could match
        }
        
        int count = 0;
        for(Shred s : allThatMatch){
            UI.println("Shred at Position: " + (shredsThatMatch.get(s) + 1) + " might match");
            UI.setColor(Color.cyan); // for a highlighted border
            s.drawWithBorder(LEFT + (double)shredsThatMatch.get(s) * SIZE, TOP_ALL);
            count++;
        }
        UI.println("Suggested matches " + count);
    }

    /**
     * Creates an object and set up the user interface
     */
    public static void main(String[] args) {
        DeShredder ds =new DeShredder();
        ds.setupGUI();
    }

}
