// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 5
 * Name:
 * Username:
 * ID:
 */

/**
 * ExpElem: an element of an expression - either a String (an operator) or a number
 *  An operator or a named constant is stored in the operator field
 *   (and Double.NaN is stored in the value field).
 *  A number is stored in the value field,
 *   and "#" is stored in the operator field to indicate that it is a number
 */

public class ExpElem{
    private final String operator;
    private final double value;

    /**
     * Construct an Expr given an operator
     */ 
    public ExpElem(String token){
        operator = token;
        value = Double.NaN;
    }

    /**
     * Construct an ExpElem given a number
     */ 
    public ExpElem(double v){
        operator = "#";
        value = v;
    }

    /**
     * Get the operator (or constant name) (a string)
     */
    public String getOperator(){ return operator; }
        
    /**
     * Get the value (a number)
     */
    public double getValue(){ return value; }
        
    /**
     * Return a string for printing
     */
    public String toString(){
        return  (operator=="#") ? ""+value : operator;
    }


}
