// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 5
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * Calculator for Cambridge-Polish Notation expressions
 * (see the description in the assignment page)
 * User can type in an expression (in CPN) and the program
 * will compute and print out the value of the expression.
 * The template provides the method to read an expression and turn it into a tree.
 * You have to write the method to evaluate an expression tree.
 *  and also check and report certain kinds of invalid expressions
 */

public class CPNCalculator{

    /**
     * Setup GUI then run the calculator
     */
    public static void main(String[] args){
        CPNCalculator calc = new CPNCalculator();
        calc.setupGUI();

        //WRITE HERE WHICH PARTS OF THE ASSIGNMENT YOU HAVE COMPLETED
        // so the markers know what to look for.
        UI.println("""
         I have done core and completion and attempted the printing normally 
      
         --------------------
         """);

        calc.runCalculator();
    }

    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.addButton("Clear", UI::clearText); 
        UI.addButton("Quit", UI::quit); 
        UI.setDivider(1.0);
    }

    /**
     * Run the calculator:
     * loop forever:  (a REPL - Read Eval Print Loop)
     *  - read an expression,
     *  - evaluate the expression,
     *  - print out the value
     * Invalid expressions could cause errors when reading or evaluating
     * The try-catch prevents these errors from crashing the program - 
     *  the error is caught, and a message printed, then the loop continues.
     */
    public void runCalculator(){
        UI.println("Enter expressions in pre-order format with spaces");
        UI.println("eg   ( * ( + 4 5 8 3 -10 ) 7 ( / 6 4 ) 18 )");
        while (true){
            UI.println();
            try {
                GTNode<ExpElem> expr = readExpr();
                double value = evaluate(expr);
                printExpr(expr);//added for challenge
                UI.println();
                UI.println(" -> " + value);
            }catch(Exception e){UI.println("Something went wrong! "+e);}
        }
    }

    
    /**
     * Evaluate an expression and return the value
     * Returns Double.NaN if the expression is invalid in some way.
     * If the node is a number
     *  => just return the value of the number
     * or it is a named constant
     *  => return the appropriate value
     * or it is an operator node with children
     *  => evaluate all the children and then apply the operator.
     */
    public double evaluate(GTNode<ExpElem> expr){
        if (expr==null){
            return Double.NaN;
        }

        if(expr.getItem().getOperator().equals("#")){
            return expr.getItem().getValue();
        }else if(expr.getItem().getOperator().equals("PI")){
            return Math.PI;
        }else if(expr.getItem().getOperator().equals("E")){
            return Math.E;
        }

        //adds to a total and returns from all children
        else if(expr.getItem().getOperator().equals("+")){
            double total = 0;
            for(GTNode<ExpElem> child : expr){
                total += evaluate(child);
            }
            return total;
        }

        //subtacts from the first input and returns
        else if(expr.getItem().getOperator().equals("-")){
            double subbed = evaluate(expr.getChild(0));
            for(int i = 1; i < expr.numberOfChildren(); i++){
                subbed -= evaluate(expr.getChild(i));
            }
            return subbed;
        }

        //multiplies all inputs together 
        else if(expr.getItem().getOperator().equals("*")){
            double multiply = evaluate(expr.getChild(0));
            for(int i = 1; i < expr.numberOfChildren(); i++){
                multiply *= evaluate(expr.getChild(i));
            }
            return multiply;
        }

        //divides all inputs 
        else if(expr.getItem().getOperator().equals("/")){
            double divide = evaluate(expr.getChild(0));
            for(int i = 1; i < expr.numberOfChildren(); i++){
                Double child = evaluate(expr.getChild(i));
                if(child != 0){
                    divide /= child;
                }else{
                    UI.println("Cannot divide by 0");
                    return Double.NaN;
                }
            }
            return divide;
        }

        //returns the power output
        else if(expr.getItem().getOperator().equals("^")){
            if(expr.numberOfChildren() == 2){
                return Math.pow(evaluate(expr.getChild(0)), evaluate(expr.getChild(1)));
            }else{
                UI.println("Invalid number of arguments for exponent");
                return Double.NaN;
            }
        }

        //outputs sqrt of a input
        else if(expr.getItem().getOperator().equals("sqrt")){
            if(expr.numberOfChildren() == 1){
                return Math.sqrt(evaluate(expr.getChild(0)));
            }else{
                UI.println("Invalid number of arguments for root");
                return Double.NaN;
            }
        }

        //Calculates the log of a input 
        else if(expr.getItem().getOperator().equals("log")){
            if(expr.numberOfChildren() == 1){
                return Math.log10(evaluate(expr.getChild(0)));
            }else if(expr.numberOfChildren() == 2){
                return Math.log(evaluate(expr.getChild(0))) / Math.log(evaluate(expr.getChild(1)));
            }else{
                UI.println("Invalid number of arguments for log(10)");
                return Double.NaN;
            }
        }

        //Calculates the natural log of a input 
        else if(expr.getItem().getOperator().equals("ln")){
            if(expr.numberOfChildren() == 1){
                return Math.log(evaluate(expr.getChild(0)));
            }else{
                UI.println("Invalid number of arguments for natural log");
                return Double.NaN;
            }
        }

        //Calculates the sine of an input
        else if(expr.getItem().getOperator().equals("sin")){
            if(expr.numberOfChildren() == 1){
                return Math.sin(evaluate(expr.getChild(0)));
            }else{
                UI.println("Invalid number of arguments for trig function 'sin'");
                return Double.NaN;
            }
        }

        //Calculates the cosine of an input
        else if(expr.getItem().getOperator().equals("cos")){
            if(expr.numberOfChildren() == 1){
                return Math.cos(evaluate(expr.getChild(0)));
            }else{
                UI.println("Invalid number of arguments for trig function 'cos'");
                return Double.NaN;
            }
        }

        //Calculates the tangent of an input
        else if(expr.getItem().getOperator().equals("tan")){
            if(expr.numberOfChildren() == 1){
                return Math.tan(evaluate(expr.getChild(0)));
            }else{
                UI.println("Invalid number of arguments for trig function 'tan'");
                return Double.NaN;
            }
        }

        //Calculates the average of all inputs
        else if(expr.getItem().getOperator().equals("avg")){
            double total = 0;
            for(GTNode<ExpElem> child : expr){
                total += evaluate(child);
            }
            return total / expr.numberOfChildren();
        }

        //returns the distance between two points
        //either 2D || 3D
        else if(expr.getItem().getOperator().equals("dist")){
            if(expr.numberOfChildren() == 4){
                double x1 = evaluate(expr.getChild(0));
                double y1 = evaluate(expr.getChild(1));
                double x2 = evaluate(expr.getChild(2));
                double y2 = evaluate(expr.getChild(3));
                return Math.sqrt(Math.pow((x2-x1),2) + Math.pow((y2-y1),2));
            }else if(expr.numberOfChildren() == 6){
                double x1 = evaluate(expr.getChild(0));
                double y1 = evaluate(expr.getChild(1));
                double z1 = evaluate(expr.getChild(2));
                double x2 = evaluate(expr.getChild(3));
                double y2 = evaluate(expr.getChild(4));
                double z2 = evaluate(expr.getChild(5));
                return Math.sqrt(Math.pow((x2-x1),2) + Math.pow((y2-y1),2) + Math.pow((z2 - z1), 2));
            }else{
                UI.println("Invalid number of arguments for Euclidean ditance");
                return Double.NaN;
            }
        }
        else {
            UI.println("Ineligible operater '" + expr.getItem().getOperator() + "'");
            return Double.NaN;
        }
    }

    /**
     * Reads an expression from the user and constructs the tree.
     */ 
    public GTNode<ExpElem> readExpr(){
        String expr = UI.askString("expr:");
        return readExpr(new Scanner(expr));   // the recursive reading method
    }

    public void printExpr(GTNode<ExpElem> n){
        if(n==null) return;        
        if(n.getItem().getOperator().equals("*") || n.getItem().getOperator().equals("-") || n.getItem().getOperator().equals("+") || n.getItem().getOperator().equals("/")){
        UI.print("(");
            for(int i = 0; i < n.numberOfChildren()-1; i++){
                if(n.getChild(i).getItem().getOperator().equals("#")){
                    UI.print(n.getChild(i).getItem().toString() + n.getItem().toString());
                }else{
                    printExpr(n.getChild(i));
                }
            }
            if(n.getChild(n.numberOfChildren()-1).equals("#")){
                UI.print(n.getChild(n.numberOfChildren()-1).getItem().toString());
            }else{
                printExpr(n.getChild(n.numberOfChildren()-1));
            }
        }else if(n.getItem().getOperator().equals("sqrt") || (n.getItem().getOperator().equals("sin")) || (n.getItem().getOperator().equals("cos")) ||(n.getItem().getOperator().equals("tan")) || (n.getItem().getOperator().equals("log")) ||(n.getItem().getOperator().equals("ln")) || (n.getItem().getOperator().equals("avg")) || (n.getItem().getOperator().equals("dist"))){
            UI.print(n.getItem().toString());
            UI.print("(");
            for(int i = 0; i < n.numberOfChildren(); i++){
                if(n.getChild(i).getItem().getOperator().equals("#")){
                    UI.print(n.getChild(i).getItem().toString() + " " );
                }else{
                    printExpr(n.getChild(i));
                }
            }
            
        }
        UI.print(")");
    }

    /**
     * Recursive helper method.
     * Uses the hasNext(String pattern) method for the Scanner to peek at next token
     */
    public GTNode<ExpElem> readExpr(Scanner sc){
        if (sc.hasNextDouble()) {                     // next token is a number: return a new node
            return new GTNode<ExpElem>(new ExpElem(sc.nextDouble()));
        }
        else if (sc.hasNext("\\(")) {                 // next token is an opening bracket
            sc.next();                                // read and throw away the opening '('
            ExpElem opElem = new ExpElem(sc.next());  // read the operator
            GTNode<ExpElem> node = new GTNode<ExpElem>(opElem);  // make the node, with the operator in it.
            while (! sc.hasNext("\\)")){              // loop until the closing ')'
                GTNode<ExpElem> child = readExpr(sc); // read each operand/argument
                node.addChild(child);                 // and add as a child of the node
            }
            
            String close = sc.next();                                // read and throw away the closing ')'

            return node;
        }
        else {                                        // next token must be a named constant (PI or E)
            // make a token with the name as the "operator"
            return new GTNode<ExpElem>(new ExpElem(sc.next()));
        }
    }

}
