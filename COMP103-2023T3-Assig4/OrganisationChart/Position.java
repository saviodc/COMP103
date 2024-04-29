// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 4
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;
import java.awt.Color;

/**
 * Represents a position in an organisation
 * Normal positions have a role (and could have further information)
 * A manager is a position that has a non empty team consisting of
 *   a set of positions.
 *   (If a position has an empty team, then it is not a manager.)
 * All positions (other than the CEO) have a manager, the position
 *   that this position reports to.
 *
 * A position is drawn as a rectangle, labeled with the role
 * Every Position (except the CEO) will be drawn with a link to 
 *   their manager.
 * Every Position is drawn one layer down from its manager, at a horizontal
 *  location specified by the position's offset, which specifies how far
 *  to the right (or left) of the location of the manager.
 *   This means that if the manager is moved around on the screen, 
 *   the team members (and their team members, and ....) will
 *   automatically move with them.
 */

public class Position {

    // Fields
    private String role;
    private Position manager;
    private Set<Position> team = new HashSet<Position>();
    private double offset;  // horizontal offset relative to the manager
                            // negative value to the left of the manager,
                            // positive value to the right

    // Constants for size of box representing the position
    public static final double WIDTH = 55;   
    public static final double HEIGHT = 40;  

    // Constructors

    /**
     * Construct a new vacant Position object with unknown role
     */
    public Position(){
        this.role = null;
    }

    /**
     * Construct a new Position object with a role.
     */
    public Position(String role) {
        this.role = role;
    }

    /**
     * Construct a new Position object with the given role and offset
     * Useful for loading from file 
     */
    public Position(String role, double offset) {
        this.role = role;
        this.offset = offset;
    }
    // Adding and removing members of the team =========================

    // [STEP 1:]
    /**
     * Add a new member to the team managed by this position, and
     * ensure that the new team member has this position as their manager
     */
    public void addToTeam(Position newMemb){
        if (newMemb == null) return;
        this.team.add(newMemb);
        newMemb.manager = this;
    }

    // [STEP 2:]
    /**
     * Remove a member of the team managed by this position, and
     * ensure that the team member no longer has this position as their manager
     */
    public void removeFromTeam(Position teamMemb){
        if(teamMemb == null) return;
        if(teamMemb.getManager() == this){
            this.team.remove(teamMemb);
        }
        teamMemb.manager = null;
    }

    // Simple getters and setters  ==================================

    /**
     * Returns the manager of this Position
     */
    public Position getManager()  {return manager;}

    /**
     * Returns the role of this Position
     */
    public String getRole()  {return role;}

    /**
     * Sets the role of this Position
     */
    public void setRole(String r)  {role = r;}

    /* Clear the role of the position */
    public void clearRole(){
        role=null;
    }

    /**
     * Returns the set of positions in the team,
     */
    public Set<Position> getTeam() {
        return Collections.unmodifiableSet(team);
    }
    //Note: By returning an unmodifiable version of the team, other parts
    // of the program can access and loop through the team,
    // but cannot add or remove position from the team

    /**
     * Returns true iff this position is managing any other positions
     */
    public boolean isManager(){
        return (!team.isEmpty());
    }
    
    /**
     * Returns the offset 
     */
    public double getOffset(){
        return offset;
    }
    
    /**
     * Move the value offset so that the Position will be drawn at location x 
     * Offset specifies where to draw the position, relative to their manager.
     * Offset is the distance to the right (or left, if negative) of the manager's location.
     */
    public void moveOffset(double x){
        if (manager == null) { // this must be the CEO
            offset = x - ROOT_X;
        }
        else {
            offset = x - manager.getX();
        }
    }

    /**
     * Set the offset value (horizontal location of this position relative to manager)
     * Only needed for constructing test hierarchy or loading from a file.
     */
    public void setOffset(double off){
        offset = off;
    }

    /**
     * Return the top of this position box (internal use only)
     * Calculated 
     */
    private double getTop(){
        if (manager == null) { return ROOT_TOP; }   // this must be the top position
        return manager.getTop() + HEIGHT + V_SEP;
    }

    /**
     * Horizontal center of this position box (internal use only)
     * Recursive method, to compute center from the offset and the center of the manager.
     */
    private double getX(){
        if (manager == null) {  // this must be the CEO
            return ROOT_X + offset;
        }
        else {
            return manager.getX() + offset;
        }
    }

    /**
     * Returns true iff the point (x,y) is on top of where
     *  this position is currently drawn.
     */
    public boolean on(double x,double y){
        return (Math.abs(getX()-x)<=WIDTH/2 && (y >= getTop()) && (y <= getTop()+HEIGHT) );
    }

    /**
     * Returns a string containing the details of a position.
     * if the role or initials are null, then will be given as ""
     */
    public String toString() {
        return (role==null)?"":role;
    }

    /**
     * Returns a string containing the details of a position, plus
     * offset and number of members of their team.
     * initials and role may be "NULL"
     * May be useful for saving to files
     */
    public String toStringFull() {
        return ((role==null||role.equals("")?"NULL":role) +" "+
            (int)(offset) +" "+team.size());
    }

    // Constants related to drawing Positions.
    public static final double V_SEP = 20;   // vertical separation between layers
    public static final double ROOT_TOP = 50;
    public static final double ROOT_X = 550;
    public static final Color BACKGROUND_COL = new Color(255, 255, 180);
    public static final Color HIGHLIGHT_COL = new Color(97, 255, 89);
    public static final Color TEMP_HIGHLIGHT_COL = new Color(222, 255, 220);
    public static final Color EMPL_HIGHLIGHT_COL = new Color(9, 255, 255);

    /**
     * Draw a box representing the Position,
     * Also draws the connection to its manager (if there is a manager)
     */
    public void draw(){
        draw(false, false);
    }

    /**
     * Draw the position, but highlight it as selected
     */
    public void drawHighlighted(){
        draw(true, false);
    }

    /**
     * Draw the box with highlights
     * Three possible highlights:
     *  selected (bright green)
     *  temporary for mouse pressed on position
     *  temporary employee highlight for mouse pressed on employee
     */
    public void draw(boolean highlighted, boolean tempHighlighted){
        double left=getX()-WIDTH/2;
        double top=getTop();
        // Background colour
        UI.setColor(tempHighlighted?TEMP_HIGHLIGHT_COL:(highlighted?HIGHLIGHT_COL:BACKGROUND_COL));
        UI.fillRect(left,top,WIDTH,HEIGHT);
        // Outline colour
        UI.setColor(Color.black);
        UI.drawRect(left,top,WIDTH,HEIGHT);
        UI.drawString((role==null)?"--":role, left+5, top+12);
        if (manager != null) {
            UI.setColor(Color.black);
            // vertical line
            double x1 = manager.getX();
            double y1 = manager.getTop() + HEIGHT;
            double yMid = y1 + V_SEP/2;

            double x2 = x1 + offset;
            double y2 = y1 + V_SEP;

            UI.drawLine(x1, y1, x1, yMid);    // vertical
            UI.drawLine(x1, yMid, x2, yMid);  // horizontal
            UI.drawLine(x2, yMid, x2, y2);    // vertical
        }
    }

}
