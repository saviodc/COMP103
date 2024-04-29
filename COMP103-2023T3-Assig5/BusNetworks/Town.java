// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 5
 * Name:
 * Username:
 * ID:
 */

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import ecs100.*;

/**
 *  Information about a town:
 *  name and neighbours
 */

public class Town {

    private String name;
    private Set<Town> neighbours = new HashSet<Town>();

    /**
     * Constructor
     */
    public Town(String name) {
        this.name = name;
    }

    /**
     * return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * return the neighbours (unmodifiable)
     */
    public Set<Town> getNeighbours() {
        return Collections.unmodifiableSet(neighbours);
    }

    /**
     * Add a new neighbour town to this town
     */
    public void addNeighbour(Town node) {
        neighbours.add(node);
    }

    /**
     * return a string description
     */
    public String toString(){
        return name+" ("+neighbours.size()+" connections)";
    }

}
