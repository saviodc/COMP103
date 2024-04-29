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
import java.util.Map.Entry;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;

/**
 * MilanoSubway
 * A program to answer queries about Milan Metro subway lines and timetables for
 *  the subway services on those subway lines.
 *
 * See the assignment page for a description of the program and what you have to do.
 */

public class MilanoSubway{
    //Fields to store the collections of Stations and Lines
    private Map<String, Station> allStations = new HashMap<String, Station>(); // all stations, indexed by station name
    private Map<String, SubwayLine> allSubwayLines = new HashMap<String, SubwayLine>(); // all subway lines, indexed by name of the line
    private Map<String, Station> exchangeStations = new HashMap<String, Station>(); //all stations with multiple lines, for challenge

    // Fields for the GUI  (with default values)
    private String currentStationName = "Zara";     // station to get info about, or to start journey from
    private String currentLineName = "M1-north";    // subway line to get info about.
    private String destinationName = "Brenta";      // station to end journey at
    private int startTime = 1200;                   // time for enquiring about

    /**
     * main method:  load the data and set up the user interface
     */
    public static void main(String[] args){
        MilanoSubway milan = new MilanoSubway();
        milan.setupGUI();   // set up the interface

        //WRITE HERE WHICH PARTS OF THE ASSIGNMENT YOU HAVE COMPLETED
        // so the markers know what to look for.
        UI.println("I have finished all of the core and completion methods");
        UI.println("I have also completed the challenge (using exchange stations when there is no direct line between stations) with added fields and methods to preform the task");
        UI.println("Basically i have done all including challenge");
        UI.println("----------------------------");
        milan.loadData();   // load all the data
    }

    /**
     * Load data files
     */
    public void loadData(){
        loadStationData();
        UI.println("Loaded Stations");
        loadSubwayLineData();
        UI.println("Loaded Subway Lines");
        // The following is only needed for the Completion and Challenge
        loadLineServicesData();
        UI.println("Loaded Line Services");
        //below is for my challenge
        loadAllExchangeStations();
        UI.println("Loaded all exchange stations");
    }

    /**
     * User interface has buttons for the queries and text fields to enter stations and subway line
     * You will need to implement the methods here, or comment out the button.
     */
    public void setupGUI(){
        UI.addButton("List all Stations",    this::listAllStations);
        UI.addButton("List Stations by name",this::listStationsByName);
        UI.addButton("List all Lines",       this::listAllSubwayLines);
        UI.addButton("Set Station",          this::setCurrentStation); 
        UI.addButton("Set Line",             this::setCurrentLine);
        UI.addButton("Set Destination",      this::setDestinationStation);
        UI.addTextField("Set Time (24hr)",   this::setTime);
        UI.addButton("Lines of Station",     this::listLinesOfStation);
        UI.addButton("Stations on Line",     this::listStationsOnLine);
        UI.addButton("On same line?",        this::onSameLine);
        UI.addButton("Next Services",        this::findNextServices);
        UI.addButton("Find Trip",            this::findTrip);
        UI.addButton("Find route", this::findPath);// challenge
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1500, 750);
        UI.setDivider(0.2);
        UI.drawImage("data/system-map.jpg", 0, 0, 1000, 704);
    }

    // Methods for loading data 
    // The loadData method suggests the methods you need to write.

    /**
     * Gets all lines in appropriate file and adds to map of stations, mapping names to their station representation
     * 
     */
    public void loadStationData(){
        try{
            List<String> stationData = Files.readAllLines(Path.of("data/stations.data"));
            for(String s : stationData){
                Scanner sc = new Scanner(s);
                String name = sc.next();
                allStations.put(name, new Station(name, sc.nextDouble(), sc.nextDouble())); //Reading name, x and y to feed into Station constructor
            }
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        } //gets all lines from the file into a collection
    }

    /**
     * Get all the subway lines from the name, then fills appropriate values for a station from its files
     */
    public void loadSubwayLineData(){
        try{
            //read all subwaylines in to list
            List<String> lineNames = Files.readAllLines(Path.of("data/subway-lines.data"));
            for(String s : lineNames){
                allSubwayLines.put(s, new SubwayLine(s));
            }

            Set<String> allLineNames = allSubwayLines.keySet();
            for(String s : allLineNames){
                String filePath = "data/" + s + "-stations.data";
                List<String> allStops = Files.readAllLines(Path.of(filePath));
                for(String stop : allStops){
                    Scanner sc = new Scanner(stop);
                    String atStop = sc.next();
                    allSubwayLines.get(s).addStation(allStations.get(atStop), sc.nextDouble());
                    allStations.get(atStop).addSubwayLine(allSubwayLines.get(s));
                }
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }

    }

    /**
     * Reads all the times of every line into a new LineService and adds it to appropriate SubwayLine
     */
    public void loadLineServicesData(){
        try{
            //get all the subway lines
            List<String> lineNames = Files.readAllLines(Path.of("data/subway-lines.data"));
            
            for(String line : lineNames){
                String filePath = "data/" + line + "-services.data";
                List<String> allServices = Files.readAllLines(Path.of(filePath)); // reads the file with all timing of that service
                
                for(String service : allServices){
                    LineService addService =  new LineService(allSubwayLines.get(line));
                    Scanner sc = new Scanner(service);
                    //while there are more times on that service
                    while (sc.hasNext()){
                        addService.addTime(sc.nextInt());
                    }
                    allSubwayLines.get(line).addLineService(addService); //adds the loaded in service to that line
                }
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
    // Methods for answering the queries
    // The setupGUI method suggests the methods you need to write.

    
    /**
     * Lists all the stations of the train system
     */
    public void listAllStations(){
        UI.clearText();
        UI.println("All Stops: ");
        int counter = 1;
        for(String stationName : allStations.keySet()){
            UI.println(counter + ") " + stationName);
            counter++;
        }
    }

    
    /**
     * Prints a sorted list of the names
     */
    public void listStationsByName(){
        UI.clearText();
        UI.println("All Stops in Alphabetcal Order:");
        Set<String> sortedNames = new TreeSet<>(allStations.keySet()); //As they are strings, TreeSet organises alphabetcally as that is the natural ordering
        int counter = 1;
        for(String names : sortedNames){
            UI.println(counter + ") " + names);
            counter++;
        }
    }
    
    /**
     * Lists all the subway lines of the train system
     */
    public void listAllSubwayLines(){
        UI.clearText();
        UI.println("All Lines:");
        int counter = 1;
        for(SubwayLine line : allSubwayLines.values()){
            UI.println(counter + ") " + line);
            counter++;
        }
    }
    /**
     * List all the lines at the set station
     */
    public void listLinesOfStation(){
        UI.clearText();
        Set<SubwayLine> allLines = allStations.get(currentStationName).getSubwayLines();
        int count = 1;
        for(SubwayLine line : allLines){
            UI.println(count + ") " + line);
            count++;
        }
    }

    
    /**
     * Lists all the station at the set line
     */
    public void listStationsOnLine(){
        UI.clearText();
        List<Station> stationsOnLine = allSubwayLines.get(currentLineName).getStations();
        int counter = 1;
        UI.println("On Line " + currentLineName);
        for(Station station : stationsOnLine){
            UI.println(counter + ") " + station.getName());
            counter++;
        }
    }

    /**
     * Checks if the currentStation and the destination are on the same line and return what line that is for later use 
     */    
    public SubwayLine onSameLine(){
        UI.clearText();
        //It wouldnt make sense to get on a train to get to the same spot so breaks out of checking early
        if(currentStationName.equals(destinationName)){
            UI.println("Unwise to catch train to same spot");
            return null;
        }
        
        for(SubwayLine line : allSubwayLines.values()){
            List<Station> stationsOnLine = line.getStations();
            int index = 0;
            int indexOrigin = -1;
            int indexDestination = -1;
            double originDistance = 0; 
            double destinationDistance = 0;

            for(Station station : stationsOnLine){
                if(station.getName().equals(destinationName)){
                    //get the index of the line is the destination for later as you cannot travel backwards
                    indexDestination = index;
                    destinationDistance= line.getDistanceFromStart(station); //distance for later calculation
                }else if(station.getName().equals(currentStationName)){
                    //get the index of the line is the current for later as you cannot travel backwards
                    indexOrigin = index;
                    originDistance= line.getDistanceFromStart(station);//distance for later calculation
                }
                index++;
            }

            //checks if destination is past the origin to check if moving forward
            //also checks if the index has changed if not does not return any value
            if(indexDestination > indexOrigin && indexOrigin >=0 && indexDestination >=0){
                double distanceBetween = destinationDistance - originDistance;
                UI.println("Travel from " + currentStationName + " to " + destinationName);
                UI.printf("Line: %s \nDistance: %.2f km\n", line, distanceBetween);
                return line;
            }
        }

        UI.println("No direct line found");
        return null;
    }
    
    /**
     * Checks the same onSameLine, but independant of the variables
     * This is for challenge and is only to try see if i can solve routing problem by using this method to aid the search
     */
    public SubwayLine onSameLine(Station start, Station end){
        
        //It wouldnt make sense to get on a train to get to the same spot so breaks out of checking early
        if(currentStationName.equals(destinationName)){
            UI.println("Unwise to catch train to same spot");
            return null;
        }
        
        for(SubwayLine line : allSubwayLines.values()){
            List<Station> stationsOnLine = line.getStations();
            int index = 0;
            int indexOrigin = -1;
            int indexDestination = -1;
            double originDistance = 0; 
            double destinationDistance = 0;

            for(Station station : stationsOnLine){
                if(station.getName().equals(end.getName())){
                    //get the index of the line is the destination for later as you cannot travel backwards
                    indexDestination = index;
                    destinationDistance= line.getDistanceFromStart(station); //distance for later calculation
                }else if(station.getName().equals(start.getName())){
                    //get the index of the line is the current for later as you cannot travel backwards
                    indexOrigin = index;
                    originDistance= line.getDistanceFromStart(station);//distance for later calculation
                }
                index++;
            }

            //checks if destination is past the origin to check if moving forward
            //also checks if the index has changed if not does not return any value
            if(indexDestination > indexOrigin && indexOrigin >=0 && indexDestination >=0){
                double distanceBetween = destinationDistance - originDistance;
                UI.printf("Line: %s Travelling Distance: %.2f km\n", line, distanceBetween);
                return line;
            }
        }

        UI.println("No direct line found");
        return null;
    }

    /**
     * Finds when the next service is at after the set time from the set destination, returns what line and time for potential further use
     */
    public Map<SubwayLine, Integer> findNextServices(){
        UI.clearText();
        Map<SubwayLine, Integer> allRoutesAndTimes = new HashMap<>(); //map of the service and time
        for(SubwayLine line : allSubwayLines.values()){
            List<Station> allStationsOnLine = line.getStations();
            int indexOfStation = -1;
            //if the stations on the line do not contain the station continue onto next line (next iteration)
            if(!allStationsOnLine.contains(allStations.get(currentStationName))) continue; 
            else indexOfStation = allStationsOnLine.indexOf(allStations.get(currentStationName));
            
            //if the stop is the last stop on the line you technically cant catch the train as its at its final stop so does not include this either
            //this is different to the demo but i feel this is how it should be as you cannot catch the train at the final destination 
            if(indexOfStation == allStationsOnLine.size() -1) continue;

            List<LineService> allServices = line.getLineServices(); 
            List<Integer> allTimes = new ArrayList<>();

            for(LineService service : allServices){
                List<Integer> timesForAService = service.getTimes();
                int compare = timesForAService.get(indexOfStation);

                if(compare > startTime) allTimes.add(compare); //only if the time greater than the set time
            }
            int closest = 0;
            int timeAt = 0;
            int difference = Integer.MAX_VALUE;
            //checks what is the closest time to the set time by checking all differences 
            if(!allTimes.isEmpty()){
                for(int time : allTimes){
                    if((time - startTime) < difference){
                        difference = time - startTime;
                        timeAt = time;
                    }
                }
            }else{
                UI.println("No services found");
                return null;
            }
            
            UI.println(line.getName() + " = Next Service: " + timeAt);
            allRoutesAndTimes.put(line, timeAt);
        }
        
        return allRoutesAndTimes;
    }

    /**
     * Finds the next trip if there is one between the current and destination, using the onSameLine() method to do some of the work as would be uneccessary code repetion
     */
    public void findTrip(){
        UI.clearText();
        if(onSameLine() == null){
            UI.println("Cannot find trip between");
            return;
        }
        SubwayLine tripLine = onSameLine();

        try{
            List<String> allStations = Files.readAllLines(Path.of("data/" + tripLine.getName() + "-stations.data"));
            //getting the difference in distance and the index to check time from the services file
            double start = 0, end = 0;
            int indexStart= 0, indexEnd = 0;
            int i = 0;
            for(String s : allStations){
                Scanner eachLine = new Scanner(s);
                String place = eachLine.next();
                if(place.equals(currentStationName)){
                    start = eachLine.nextDouble();
                    indexStart = i;
                }else if ((place.equals(destinationName))){
                    end = eachLine.nextDouble();
                    indexEnd = i;
                }
                i++;
            }
            double distanceBetween = end - start;

            List<String> allTimes = Files.readAllLines(Path.of("data/" + tripLine.getName() + "-services.data"));
            int lineOfClosest = 0;
            int j = 0;
            int timeLeave = 0, timeArrive = 0;
            List<Integer> allTimesAtDepart = new ArrayList<>();
            List<Integer> allTimesAtArrival = new ArrayList<>();
            //getting all the times at the depart and arrival to check when is closest
            for(String s : allTimes){
                j=0;
                Scanner eachLine = new Scanner(s);
                while (eachLine.hasNext()){
                    if (j == indexStart) {
                        allTimesAtDepart.add(eachLine.nextInt());
                        j++;
                    }
                    else if (j == indexEnd){
                        allTimesAtArrival.add(eachLine.nextInt());
                        j++;
                    } else{
                        eachLine.next();
                        j++;
                    }
                }
            }
            int indexOfClosest = 0;
            int closest = Integer.MAX_VALUE;
            int k = 0;
            for(int time : allTimesAtDepart){
                if(time > startTime){
                    if((time - startTime) < closest){
                        indexOfClosest = k;
                        closest = (time - startTime);
                    }
                }
                k++;
            }
            // getting the time at the same index which is the time depart and arrive
            int timeToLeave = allTimesAtDepart.get(indexOfClosest);
            int timeArriving = allTimesAtArrival.get(indexOfClosest);

            UI.printf("Leaving: %d Arriving: %d\n", timeToLeave, timeArriving);
        }
        catch (IOException ioe){
            UI.println("No File found");
        }

    }

    
    /**
     * Loading a map of all the exchange stations where you can hop onto a different line
     */
    public void loadAllExchangeStations(){
        //these are all the stations that have multiple lines running through (not including going either direction as multiple)
        exchangeStations.put("Zara", allStations.get("Zara"));
        exchangeStations.put("Garibaldi", allStations.get("Garibaldi"));
        exchangeStations.put("Centrale", allStations.get("Centrale"));
        exchangeStations.put("Loreto", allStations.get("Loreto"));
        exchangeStations.put("Duomo", allStations.get("Duomo"));
        exchangeStations.put("Cadorna", allStations.get("Cadorna"));
        exchangeStations.put("Lotto", allStations.get("Lotto"));
        exchangeStations.put("Pagano", allStations.get("Pagano"));
    }

    
    
    
    
    /**
     * Find path by running through the all the exchange paths and finding a way to the destination through alternate stations
     * This works by finding a common exchange point to travel through and change lines 
     * This displays all the possible routes that intersect with these exchange points frm the start and end point
     */
    public void findPath(){
        // if on same line no point in routing through exchange points
        SubwayLine tripLine = onSameLine();
        if(tripLine != null){
            return;
        }
        
        //the set of all subwaylines running through the start and end point
        Set<SubwayLine> fromCurrent = allStations.get(currentStationName).getSubwayLines();
        Set<SubwayLine> fromDest = allStations.get(destinationName).getSubwayLines();
        
        //maps the stations to the appropriate subwaylines for the exchange stations
        Map<Station, SubwayLine> allExchangeCurrent = new HashMap<>();
        Map<Station, SubwayLine> allExchangeDest = new HashMap<>();
        
        //if the subwayline contains a exchange point add to the relevent map of exchange points
        for(SubwayLine c : fromCurrent){
            List<Station> allStations = c.getStations();
            for(Station s : allStations){
                if(exchangeStations.containsValue(s)){
                    allExchangeCurrent.put(s, c);
                }
            }
        }
        for(SubwayLine d : fromDest){
                List<Station> allStations = d.getStations();
            for(Station s : allStations){
                if(exchangeStations.containsValue(s)){
                    allExchangeDest.put(s, d);
                }
            }
        }
        
        //for all the exchange points that are common to the start and destination, print out the lines taken and the roe
        for(Station cur : allExchangeCurrent.keySet()){
            for(Station dest : allExchangeDest.keySet()){
                if(cur.equals(dest)){
                    UI.println("Leg 1: Travel from " + currentStationName + " to " + cur + " via " + onSameLine(allStations.get(currentStationName), cur));
                    UI.println("Leg 2: Then travel from " + dest + " to " + destinationName + " via " + onSameLine(dest, allStations.get(destinationName)));
                    UI.println();
                }
            }
        }
    }
    
    
   
    // ======= written for you ===============
    // Methods for asking the user for station names, line names, and time.

    /**
     * Set the startTime.
     * If user enters an invalid time, it reports an error
     */
    public void setTime(String time){
        int newTime = startTime; //default;
        try{
            newTime=Integer.parseInt(time);
            if (newTime >=0 && newTime<2400){
                startTime = newTime;
            }
            else {
                UI.println("Time must be between 0000 and 2359");
            }
        }catch(Exception e){UI.println("Enter time as a four digit integer");}
    }

    /**
     * Ask the user for a station name and assign it to the currentStationName field
     * Must pass a collection of the names of the stations to getOptionFromList
     */
    public void setCurrentStation(){
        String name = getOptionFromList("Choose current station", allStations.keySet());
        if (name==null ) {return;}
        UI.println("Setting current station to "+name);
        currentStationName = name;
    }

    /**
     * Ask the user for a destination station name and assign it to the destinationName field
     * Must pass a collection of the names of the stations to getOptionFromList
     */
    public void setDestinationStation(){
        String name = getOptionFromList("Choose destination station", allStations.keySet());
        if (name==null ) {return;}
        UI.println("Setting destination station to "+name);
        destinationName = name;
    }

    /**
     * Ask the user for a subway line and assign it to the currentLineName field
     * Must pass a collection of the names of the lines to getOptionFromList
     */
    public void setCurrentLine(){
        String name =  getOptionFromList("Choose current subway line", allSubwayLines.keySet());
        if (name==null ) {return;}
        UI.println("Setting current subway line to "+name);
        currentLineName = name;
    }

    // 
    /**
     * Method to get a string from a dialog box with a list of options
     */
    public String getOptionFromList(String question, Collection<String> options){
        Object[] possibilities = options.toArray();
        Arrays.sort(possibilities);
        return (String)javax.swing.JOptionPane.showInputDialog
        (UI.getFrame(),
            question, "",
            javax.swing.JOptionPane.PLAIN_MESSAGE,
            null,
            possibilities,
            possibilities[0].toString());
    }

}
