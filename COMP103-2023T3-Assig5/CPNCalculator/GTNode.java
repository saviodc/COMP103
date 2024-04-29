// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 5
 * Name:
 * Username:
 * ID:
 */

import java.util.*;

/**
 * Implements a generic general tree node where the type of the
 *  item in each node is specified by the type variable E
 *  The children of a node are ordered.
 *  There are many ways of designing such a tree class;
 *  this is a very simple one which does not have many features.
 */
public class GTNode <E> implements Iterable <GTNode<E>>{

    private final E item;
    private List<GTNode<E>> children;

    /**
     * Constructor for objects of class GTNode
     */
    public GTNode(E item){
        this.item = item;
        this.children = new ArrayList<GTNode<E>>();
    }

    /**
     * Get item from node
     */
    public E getItem(){ return item; }

    /**
     * Get number of children of node
     */
    public int numberOfChildren() {
        return children.size();
    }

    /**
     * Get the i'th child of node
     */
    public GTNode<E> getChild(int i){
        return children.get(i);
    }

    /**
     * Add child at end
     */
    public void addChild(GTNode<E> node){
        children.add(node);
    }

    /**
     * Add child at position i
     */
    public void addChild(int i, GTNode<E> node){
        children.add(i,node);
    }

    /**
     * Remove child at position i
     */
    public GTNode<E> removeChild(int i){
        return children.remove(i);
    }

    /**
     * Set child at position i
     */
    public GTNode<E> setChild(int i, GTNode<E> node){
        return children.set(i, node);
    }

    /**
     * Enables foreach:
     *      for (GTNode<E> child : node){..do something to each child..}
     * to loop through the children of a node
     */
    public Iterator<GTNode<E>> iterator(){
        return children.iterator();
    }

}
