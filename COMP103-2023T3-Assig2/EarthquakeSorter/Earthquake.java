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
import java.text.*;

/**
 * Earthquake
 * Describes a single earthquake, with information from the earthquake data
 * available from http://quakesearch.geonet.org.nz/
 * Has methods for 
 * - computing the distance between two earthquakes, 
 * - computing the distance between this earthquake and a given location,
 * - getting an integer representation of the year, month or day.
 */

public class Earthquake implements Comparable<Earthquake> {

    private String ID;
    private String date, time;
    private String region;
    private double longitude, latitude;
    private double depth;
    private double magnitude;

    /** 
     * Construct a new EarthQuake object 
     */
    public Earthquake(String ID, String date, String time,
    double longitude, double latitude,
    double magnitude, double depth, String region){
        this.ID = ID;
        this.date = date;
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
        this.magnitude = magnitude;
        this.depth = depth;
        this.region = region;
    }

    /**
     * The default ordering is alphabetically by ID
     */
    public int compareTo(Earthquake other){
        return String.CASE_INSENSITIVE_ORDER.compare(this.ID, other.ID); 
    }  


    /**
     * Distance from this earthquake to the other earthquake, in km
     * Formula from http://www.movable-type.co.uk/scripts/latlong.html
     */
    public double distanceTo(Earthquake other){
        double lat1 = this.latitude *Math.PI/180;
        double lat2 = other.latitude *Math.PI/180;
        double difLong = other.longitude*Math.PI/180 - this.longitude*Math.PI/180;

        return Math.acos( (Math.sin(lat1)*Math.sin(lat2)) +
            (Math.cos(lat1)*Math.cos(lat2))* Math.cos(difLong) ) * 6371;
    }

    /**
     * Distance from this earthquake to a given location (longitude, latitude), in km
     * Formula from http://www.movable-type.co.uk/scripts/latlong.html
     */
    public double distanceTo(double longitude, double latitude){
        double lat1 = this.latitude *Math.PI/180;
        double lat2 = latitude *Math.PI/180;
        double difLong = longitude*Math.PI/180 - this.longitude*Math.PI/180;

        return Math.acos( (Math.sin(lat1)*Math.sin(lat2)) +
            (Math.cos(lat1)*Math.cos(lat2))* Math.cos(difLong) ) * 6371;
    }

    /**
     * Get depth of the earthquake
     */
    public double getDepth(){
        return this.depth;
    }

    /**
     * Get magnitude of the earthquake
     */
    public double getMagnitude(){
        return this.magnitude;
    }

    /**
     * Get earthquake ID
     */
    public String getID(){
        return this.ID;
    }

    /**
     * Get earthquake region
     */
    public String getRegion(){
        return this.region;
    }

    /**
     * Get date
     */
    public String getDate(){
        return this.date;
    }

    /**
     * Get year
     */
    public int getYear(){
        return Integer.valueOf(this.date.substring(0, 4));
    }

    /**
     * Get month
     */
    public int getMonth(){
        return Integer.valueOf(this.date.substring(5, 7));
    }

    /**
     * Get day
     */
    public int getDay(){
        return Integer.valueOf(this.date.substring(8));
    }

    /**
     * Get time
     */
    public String getTime(){
        return this.time;
    }

    /**
     * Returns a nicely formatted String describing the earthquake
     */
    public String toString(){
        return (this.ID +
            " on " + this.date + " at " + this.time +
            " mag:" + this.magnitude +
            " depth:" + this.depth +
            " at (" + this.longitude +","+ this.latitude +")" +
            " in " + this.region);
    }

}
