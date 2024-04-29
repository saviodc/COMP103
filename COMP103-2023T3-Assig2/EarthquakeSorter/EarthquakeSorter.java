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
import java.nio.file.*;

/**
 * EarthquakeSorter
 * Sorts data about a collection of 4335 NZ earthquakes from May 2016 to May 2017
 * Each line of the file "earthquake-data.txt" has a description of one earthquake:
 *   ID time longitude latitude magnitude depth region
 * Data is from http://quakesearch.geonet.org.nz/
 *  Note the earthquakes' ID have been modified to suit this assignment.
 *  Note bigearthquake-data.txt has just the 421 earthquakes of magnitude 4.0 and above
 *   which may be useful for testing, since it is not as big as the full file.
 *   
 */

public class EarthquakeSorter{

    private List<Earthquake> earthquakes = new ArrayList<Earthquake>();

    /**
     * Load data from the specified data file into the earthquakes field:
     */
    public void loadData(String filename){
        earthquakes.clear();
        try {
            List<String> allEarthquakes = Files.readAllLines(Path.of(filename)); //get all the lines of earthquake data
            for(String earthquake : allEarthquakes){
                Scanner sc = new Scanner(earthquake);
                //populate the list of earthquakes
                earthquakes.add(new Earthquake(sc.next(), sc.next(), sc.next(), sc.nextDouble(), sc.nextDouble(), sc.nextDouble(), sc.nextDouble(), sc.next()));
            }

            UI.printf("Loaded %d earthquakes into list\n", this.earthquakes.size());
            UI.println("----------------------------");
        } catch(IOException e){UI.println("File reading failed");}
    }

    /**
     * Sorts the earthquakes by ID
     */
    public void sortByID(){
        UI.clearText();
        UI.println("Earthquakes sorted by ID");
        //As the natural ordering is by ID, the sort method works without the need for 
        //custom comparator, as it orders by natural ordering
        Collections.sort(earthquakes);
        for (Earthquake e : this.earthquakes){
            UI.println(e);
        }
        UI.println("------------------------");
    }

    /**
     * Sorts the earthquakes by magnitude, largest first
     */
    public void sortByMagnitude(){
        UI.clearText();

        UI.println("Earthquakes sorted by magnitude (largest first)");
        
        Comparator<Earthquake> compareMagnitude = new Comparator<Earthquake>(){
                @Override
                public int compare(Earthquake a, Earthquake b){
                    //the magnitude is a double and there is a '.compare()' method to sort by 
                    //the b and a are switched to change to descending order as the default is ascending
                    return Double.compare(b.getMagnitude(), a.getMagnitude()); 
                }
            };
        Collections.sort(earthquakes, compareMagnitude);
        for (Earthquake e : this.earthquakes){
            UI.println(e);
        }
        UI.println("------------------------");
    }

    /**
     * Sorts the list of earthquakes according to the date and time that they occurred.
     */
    public void sortByTime(){
        UI.clearText();
        UI.println("Earthquakes sorted by time");
        Comparator<Earthquake> compareTime = new Comparator<>(){
                @Override
                public int compare(Earthquake a, Earthquake b){
                    //compare date string first
                    int compareDate = String.CASE_INSENSITIVE_ORDER.compare(a.getDate(), b.getDate());
                    if(compareDate != 0){
                        return compareDate;
                    }else {
                        //if the same date, comparing the time string
                        return String.CASE_INSENSITIVE_ORDER.compare(a.getTime(), b.getTime());
                    }
                }
            };

        Collections.sort(earthquakes, compareTime);

        for (Earthquake e : this.earthquakes){
            UI.println(e);
        }
        UI.println("------------------------");
    }

    /**
     * Sorts the list of earthquakes according to region. If two earthquakes have the same
     *   region, they should be sorted by magnitude (highest first) and then depth (more shallow first)
     */
    public void sortByRegion(){
        UI.clearText();
        UI.println("Earthquakes sorted by region, then by magnitude and depth");
        Comparator<Earthquake> compareRegion = new Comparator<>(){
                @Override
                public int compare(Earthquake a, Earthquake b){
                    //comparing region by string value
                    int compareRegion = String.CASE_INSENSITIVE_ORDER.compare(a.getRegion(), b.getRegion());
                    if(compareRegion != 0){
                        return compareRegion;
                    }else{
                        //if same region, compare magnitude in ascending order
                        int compareMagnitude = Double.compare(b.getMagnitude(), a.getMagnitude());
                        if(compareMagnitude != 0){
                            return compareMagnitude; 
                        }else{
                            //if same magnitude (must also have been same region, compare depth in ascending order
                            return Double.compare(a.getDepth(), b.getDepth());
                        }
                    }
                }
            };
        Collections.sort(earthquakes, compareRegion);
        for (Earthquake e : this.earthquakes){
            UI.println(e);
        }
        UI.println("------------------------");
    }

    /**
     * Sorts the earthquakes by proximity to a specified location
     */
    public void sortByProximity(double longitude, double latitude){
        UI.clearText();
        UI.println("Earthquakes sorted by proximity");
        UI.println("Longitude: " + longitude + " Latitude: " + latitude );

        Comparator<Earthquake> compareCloser = new Comparator<>(){
                @Override
                public int compare(Earthquake a, Earthquake b){
                    double distanceToFirst = a.distanceTo(longitude, latitude);
                    double distanceToSecond = b.distanceTo(longitude, latitude);
                    if(Math.abs(distanceToFirst - distanceToSecond) < 1){
                        if(distanceToFirst > distanceToSecond){
                            return 1;
                        }else if(distanceToFirst < distanceToSecond){
                            return -1;
                        }else{// bascially if the same distance from, sort by id
                            return String.CASE_INSENSITIVE_ORDER.compare(a.getID(), b.getID()); //or could be a.compareTo(b)
                        }
                    }else{
                        return (int)(distanceToFirst - distanceToSecond); // will return appropriate 'int' value for the difference between distances
                    }
                }
            };
        
        Collections.sort(earthquakes, compareCloser);
        for (Earthquake e : this.earthquakes){
            UI.printf("%s Distance: %.2fkm \n", e.toString(),e.distanceTo(longitude, latitude));
        }
        UI.println("------------------------");
    }

    /**
     * Add the buttons
     */
    public void setupGUI(){
        UI.initialise();
        UI.addButton("Load", this::loadData);
        UI.addButton("sort by ID",  this::sortByID);
        UI.addButton("sort by Magnitude",  this::sortByMagnitude);
        UI.addButton("sort by Time",  this::sortByTime);
        UI.addButton("sort by Region", this::sortByRegion);
        UI.addButton("sort by Proximity", this::sortByProximity);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(900,400);
        UI.setDivider(1.0);  //text pane only 
    }

    public static void main(String[] arguments){
        EarthquakeSorter obj = new EarthquakeSorter();
        obj.setupGUI();

        //WRITE HERE WHICH PARTS OF THE ASSIGNMENT YOU HAVE COMPLETED
        // so the markers know what to look for.
        UI.println("""
        I have done all the methods provided/completed all the differnt sorts INCLUDING challenge
         --------------------
         """);

    }   

    public void loadData(){
        this.loadData(UIFileChooser.open("Choose data file"));
    }

    public void sortByProximity(){
        UI.clearText();
        this.sortByProximity(UI.askDouble("Give longitude: "), UI.askDouble("Give latitude: "));
    }

}
