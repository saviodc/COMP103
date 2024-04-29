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
 * LineService
 * A particular train service running on a subway line.
 * A line service is specified by a list of times that train leaves
 *  each station along the subway line.
 * A LineService object contains
 *  - The SubwayLine that the service runs on
 *  - a ID of the train (the name of the line concatenated with the starting time of the train)
 *  - a list of times (integers representing 24-hour time, eg 1425 for 2:45pm), one for
 *     each station on the subway line.
 * The getStart() method will return the first real time in the list of times
 */

public class LineService{
    // Fields
    private SubwayLine subwayLine;  
    private String trainID;    // train line name + starting time of the train
    private List<Integer> times = new ArrayList<Integer>();

    //Constructor
    /**
     * Make a new LineService on a particular subway line.
     */
    public LineService(SubwayLine line){
        subwayLine = line;
    }

    //getters
    public SubwayLine getSubwayLine(){
        return subwayLine;
    }

    public String getTrainID(){
        return this.trainID;
    }

    public List<Integer> getTimes(){
        return Collections.unmodifiableList(times);  // unmodifiable version of the list of times.
    }

    // Other methods.
    /**
     * Add the next time to the LineService
     */
    public void addTime(int time){
        times.add(time);
        if (trainID==null){
            trainID = subwayLine.getName()+"-"+time;
        }
    }

    /**
     * Return the start time of this Train Service
     */
    public int getStart(){
        if (times.size() != 0) return times.get(0);
        else                   return -1;
    }

    /**
     * ID plus number of stops
     */
    public String toString(){
        if (trainID==null){return subwayLine.getName()+"-unknownStart";}
        return trainID+" ("+times.size()+" stops)";
    }

}
