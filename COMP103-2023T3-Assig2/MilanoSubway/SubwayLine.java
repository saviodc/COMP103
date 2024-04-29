// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 2
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;
import java.io.*;

/**
 * SubwayLine
 * Information about a Subway Line.
 * Note, we treat the two directions on a physical line as two separate lines.
 * There is therefore an M5-north line that goes from San-Siro-Stadio to Bignami,
 *    and an M5-south line that goes in the reverse direction from Bignami to San-Siro-Stadio. 
 * Although they have the same stations, the stations will be in opposite orders.
 *
 * A SubwayLine contains 
 * - the name of the SubwayLine (M1, M2, etc followed by its direction north, south, west or east)
 * - The list of stations on the line
 * - a list of LineServices running on the line (eg the 10:00 am service from Bignami to San-Siro-Stadio)
 *   (in order of time - services earlier in the list are always earlier times (at any station) than later services)
 */

public class SubwayLine{
    //Fields
    private String name;
    private List<Station> stations = new ArrayList<Station>();               // list of stations on the line
    private Map<Station, Double> distances = new HashMap<Station, Double>(); // all distances from the beginning of the line, indexed by station
    private List<LineService> lineServices = new ArrayList<LineService>();   // set of LineServices running on the line

    /**
     * Constructor, requires name of line
     */
    public SubwayLine(String name){
        this.name = name;
    }

    // Methods to add values to the SubwayLine
    /**
     * Add a Station to the list of Stations on this line
     */
    public void addStation(Station station, double distance){
        stations.add(station);
        distances.put(station, distance);
    }

    /**
     * Add a LineService to the set of LineServices for this line
     */
    public void addLineService(LineService line){
        lineServices.add(line);
    }

    //Getters
    /**
     * return the name
     */
    public String getName(){
        return name;
    }

    /**
     * return the list of stations (but don't allow them to be modified)
     */
    public List<Station> getStations(){
        return Collections.unmodifiableList(stations); // an unmodifiable version of the list of stations
    }

    /**
     * return the list of LineServices (but don't allow them to be modified)
     */
    public List<LineService> getLineServices(){
        return Collections.unmodifiableList(lineServices); // an unmodifiable version of the list of lineServices
    }

    /**
     * Compute and return the distance from the beginning of the line to the given station
     */
    public double getDistanceFromStart(Station station){
        Double distance =  distances.get(station);
        if (distance==null) {UI.println(station.getName() +" is missing from the list of stations"); return -1;}
        return distance;
    }
    

    /**
     * toString returns the name of the subway line combined with the first and last stations on the line
     */
    public String toString(){
        return (name+" from "+stations.get(0).getName()+
                " to "+stations.get(stations.size()-1).getName());
    }

}
