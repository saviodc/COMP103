// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 5
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class BusNetworks {

    /** Map of towns, indexed by their names */
    private Map<String,Town> busNetwork = new HashMap<String,Town>();

    /** CORE
     * Loads a network of towns from a file.
     * Constructs a Map of Town objects in the busNetwork field
     * Each town has a name and a set of neighbouring towns
     * First line of file contains the names of all the towns.
     * Remaining lines have pairs of names of towns that are connected.
     */
    public void loadNetwork(String filename) {
        try {
            busNetwork.clear();
            UI.clearText();
            List<String> lines = Files.readAllLines(Path.of(filename));
            String firstLine = lines.remove(0);
            //adds all towns in a file to the network and creates the towns
            Scanner firstLineScan = new Scanner(firstLine);
            while(firstLineScan.hasNext()){
                String townName = firstLineScan.next();
                busNetwork.put(townName, new Town(townName));
            }

            //Read all the lines and adds to the set of neighbours
            for(String connection : lines){
                Scanner sc = new Scanner(connection); 
                String town1 = sc.next();
                String town2 = sc.next();
                busNetwork.get(town1).addNeighbour(busNetwork.get(town2));
                busNetwork.get(town2).addNeighbour(busNetwork.get(town1));
            }

            UI.println("Loaded " + busNetwork.size() + " towns:");

        } catch (IOException e) {throw new RuntimeException("Loading data.txt failed" + e);}
    }

    /**  CORE
     * Print all the towns and their neighbours:
     * Each line starts with the name of the town, followed by
     *  the names of all its immediate neighbours,
     */
    public void printNetwork() {
        UI.println("The current network: \n====================");
        //Gets all the towns in the map
        for(Map.Entry<String, Town> town : busNetwork.entrySet()){
            UI.print(town.getKey() + " -> ");
            //then prints for all neighbours, using print for same line
            for(Town t : town.getValue().getNeighbours()){
                UI.print(t.getName() + " ");
            }
            UI.println();//end line
        }
    }

    /** CORE
     * Print out the towns on a route (not necessarily the shortest)
     * from a starting town to a destination town.
     * OK to print the towns on the route in reverse order.
     * Use a recursive (post-order) depth first search.
     * Use a helper method with a visited set.
     */
    public void findRoute(Town town, Town dest) {
        UI.println("Looking for route between "+town.getName()+" and "+dest.getName()+":");
        if(findRoute(town, dest, new HashSet<Town>())){
            UI.println("Found route");
        }else{
            UI.println("No route");
        }
    }

    /**
     * Recursive helper method that checks if the current town is equal to the destination we want
     * Else calls on the children if havent visited before
     */
    public boolean findRoute(Town town, Town dest, Set<Town> visited){
        visited.add(town);
        if(town == dest) {
            UI.println("At end " + town.getName());
            return true; // is equal means found
        }
        for(Town neighbour : town.getNeighbours()){
            if(!visited.contains(neighbour) && findRoute(neighbour, dest, visited)){
                UI.println("from " + town.getName());
                return true;
            }
        }
        return false;
    }

    /**  COMPLETION
     * Print all the towns that are reachable through the network from
     * the given town. The Towns should be printed in order of distance from the town
     * where distance is the number of stops along the way.
     */
    public void printReachable(Town town){
        UI.println("\nFrom "+town.getName()+" you can get to:");
        Set<Town> found = new HashSet<>();//found set
        Queue<Town> townOut = new ArrayDeque<>();//processing queue so can sort by order (in terms of connection)
        townOut.offer(town);
        while(!townOut.isEmpty()){
            Town front = townOut.poll();
            if(!found.contains(front)){//only if not visited check
                found.add(front);
                if(front != town){
                    UI.println(front.getName());
                }
                for(Town neighbour : front.getNeighbours()){
                    if(!found.contains(neighbour)){
                        townOut.offer(neighbour);
                    }
                }
            }
        }
        UI.println("=====Done=====");

    }

    /**  COMPLETION
     * Print all the connected sets of towns in the busNetwork
     * Each line of the output should be the names of the towns in a connected set
     * Works through busNetwork, using findAllConnected on each town that hasn't
     * yet been printed out.
     */
    public void printConnectedGroups() {
        UI.println("Groups of Connected Towns: \n================");
        List<Town> copy = new ArrayList<>(busNetwork.values());//gets all the towns
        Set<Set<Town>> connected = new HashSet<>();//a set of all connected nodes
        while(!copy.isEmpty()){
            Set<Town> toSave = new HashSet<>();//every connected set
            findAllConnected(copy.get(0), toSave);//find set from first in copy
            copy.removeAll(toSave);//remove all in that connection from copy, so can move onto next connected group
            connected.add(toSave);//and dave
        }
        //print all the connected groups
        int i = 1;
        for(Set<Town> set : connected){
            UI.print("Set " + i + ": ");
            for(Town t : set){
                UI.print(t.getName() + " ");
            }
            UI.println();
            i++;
        }
    }

    // Suggested helper method for printConnectedGroups
    /**
     * Updates a visited set for one node
     * Traverse the network from this node using a
     * visited set 
     */
    public void findAllConnected(Town town, Set<Town> visited) {
        if(town == null) return;
        visited.add(town);
        for(Town t : town.getNeighbours()){
            if(!visited.contains(t)){
                findAllConnected(t, visited);
            }
        }
    }

    /**
     * Set up the GUI (buttons and mouse)
     */
    public void setupGUI() {
        UI.addButton("Load", ()->{loadNetwork(UIFileChooser.open());});
        UI.addButton("Print Network", this::printNetwork);
        UI.addButton("Find Route", () -> {findRoute(askTown("From"), askTown("Destination"));});
        UI.addButton("All Reachable", () -> {printReachable(askTown("Town"));});
        UI.addButton("All Connected Groups", this::printConnectedGroups);
        UI.addButton("Clear", UI::clearText);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1100, 500);
        UI.setDivider(1.0);
        loadNetwork("data-small.txt");
    }

    // Main
    public static void main(String[] arguments) {
        new BusNetworks().setupGUI();

        //WRITE HERE WHICH PARTS OF THE ASSIGNMENT YOU HAVE COMPLETED
        // so the markers know what to look for.
        UI.println("""
        I have done core and completion
        
      
         --------------------
         """);

    }

    // Utility method
    /**
     * Method to get a Town from a dialog box with a list of options
     */
    public Town askTown(String question){
        Object[] possibilities = busNetwork.keySet().toArray();
        Arrays.sort(possibilities);
        String townName = (String)javax.swing.JOptionPane.showInputDialog
            (UI.getFrame(),
                question, "",
                javax.swing.JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                possibilities[0].toString());
        return busNetwork.get(townName);
    }

}
