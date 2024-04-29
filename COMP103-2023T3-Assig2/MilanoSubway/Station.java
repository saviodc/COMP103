// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 2
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

/**
 * Station
 * Information about an individual station:
 * - The name
 * - The coordinates of the station on the map
 * - The set of SubwayLines that go through that station.
 * The constructor just takes the name and coordinates;
 * SubwayLines must then be added to the station, one by one.
 */

public class Station implements Comparable<Station> {

    private String name;
    private double x;   // coordinate x
    private double y;   // coordinate y
    private Set<SubwayLine> subwayLines = new HashSet<SubwayLine>();

    /**
     * Constructor requires name, zone, and coordinates
     */
    public Station(String name, double x, double y){
        this.name = name;
        this.x = x;
        this.y = y;
    }

    /** 
     * get the name field
     */
    public String getName(){
        return this.name;
    }

    /**
     * get the x coord
     */
    public double getXCoord(){
        return this.x;
    }

    /**
     * get the y coord
     */
    public double getYCoord(){
        return this.y;
    }

    /**
     * compareTo by names
     */
    public int compareTo(Station other){
        return name.compareTo(other.name);
    }

    /**
     * Add a SubwayLine to the station
     */
    public void addSubwayLine(SubwayLine line){
        subwayLines.add(line);
    }

    /**
     * Return an unmodifiable version of the set of subway lines.
     */
    public Set<SubwayLine> getSubwayLines(){
        return Collections.unmodifiableSet(subwayLines); 
    }

    /**
     * toString is the station name plus zone, plus number of subway lines
     */
    public String toString(){
        return name+" ("+ subwayLines.size()+" lines)";
    }

}
