// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 3
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.awt.*;

/**
 * A treatment Department (Surgery, X-ray room,  ER, Ultrasound, etc)
 * Each department will need
 * - A name,
 * - A maximum number of patients that can be treated at the same time
 * - A Set of Patients that are currently being treated
 * - A Queue of Patients waiting to be treated.
 *    (ordinary queue, or priority queue, depending on argument to constructor)
 */

public class Department{

    private String name;
    private int maxPatients;   // maximum number of patients receiving treatment at one time. 

    private Set<Patient> treatmentRoom ;    // the patients receiving treatment
    private Queue<Patient> waitingRoom;    // the patients waiting for treatment
    private int waitInDepartment = -1;   //total time of patients in a department
    private int patientsSeen = 0;       //total patients made it to department
    private Color col;    
    /**
     * Construct a new Department object
     * Initialise the waiting queue and the current Set.
     */
    public Department(String name, int maxPatients, boolean usePriQueue, Color c){
        this.name = name;
        this.maxPatients = maxPatients;
        if(usePriQueue){
            waitingRoom = new PriorityQueue<>();
        }else{
            waitingRoom = new ArrayDeque<>();
        }
        treatmentRoom = new HashSet<>();
        this.col = c;
    }
    

    // Methods 
    /**
     * Challenge Method
     * Gets assigned color
     */
    public Color getDepartmentColor(){
        return col;
    }
    
    /**
     * Challenge Method
     * Increases the total time waited in a department
     */
    public void increaseDepartmentWaitTotal(){
        waitInDepartment++;
    }
    
    /**
     * Challenge Method
     * Increase the number of patients treated count 
     */
    public void increasePatientSeen(){
        patientsSeen++;
    }
    
    /**
     * Challenge method
     * Get the total wait time
     */
    public int getTotalWait(){
        if(waitInDepartment < 0) return 0;
        return waitInDepartment;
    }
    
    /**
     * Get the total patients seen
     */
    public int getTotalPatientsSeen(){
        return patientsSeen;
    }
    
    /**
     * Clears both the rooms of the department
     */
    public void clear(Boolean usePriorityQueues){
        if(usePriorityQueues) {
            this.waitingRoom = new PriorityQueue<>();
        }else{
            this.waitingRoom = new ArrayDeque<>();
        }
    }
    
    /**
     * Returns how many patients can be treated at a single instance
     */
    public int getCapacity(){return maxPatients; }
    
    /**
     * Gets all patients in the waiting room
     */
    public Queue<Patient> getWaitingRoom(){
        return waitingRoom;
    }
    
    /**
     * Gets the name of the department
     */
    public String getDepartmentName(){
        return name;
    }
    
    /**
     * Gets all the patients in the treatment room
     */
    public Set<Patient> getTreatmentRoom(){
        return this.treatmentRoom;
    }
    
    /**
     * removes a patient from the department
     */
    public Patient remove(Patient patient){
         this.treatmentRoom.remove(patient);
         return patient;
    }
    
    /**
     * Returns whether or not there is space for another patient
     */
    public boolean hasSpace(){return (this.treatmentRoom.size() < maxPatients);}
    
    public String toString(){
        return this.name + " Capacity " + this.maxPatients; 
    }
    
    
    /**
     * gets a patient from the waiting room
     */
    public Patient getWaitingPatient(Patient pat){
        for(Patient p : waitingRoom){
            if(p.equals(pat)){
                return p;
            }
        }
        return null;
    }
    
    /**
     * gets a patient from treatment room 
     */
    public Patient getPatient(Patient pat){
        for(Patient p : treatmentRoom){
            if(p.equals(pat)){
                return p;
            }
        }
        return null;
    }
    
    /**
     * Moves a patient between departments
     */
    public void moveFrom(Department other, Patient p){
        Patient move = other.remove(p);
        patientsSeen++;
        this.add(p);
    }
    
    /**
     * Returns if department is currently treating any patients
     */
    public boolean isTreating(){
        if(treatmentRoom.size() == 0) return false;
        return true;
    }
    
    /**
     * Add a patient to a new department
     */
    public void add(Patient patient){
        this.waitingRoom.offer(patient);
    }
    
    /**
     * shifts from waiting room to treatement
     */
    public void move(){treatmentRoom.add(waitingRoom.poll()); }
    
    /**
     * Draw the department: the patients being treated and the patients waiting
     * You may need to change the names if your fields had different names
     */
    public void redraw(double y){
        UI.setFontSize(14);
        UI.drawString(name, 0, y-35);
        double x = 10;
        UI.drawRect(x-5, y-30, maxPatients*10, 30);  // box to show max number of patients
        for(Patient p : treatmentRoom){
            p.redraw(x, y);
            x += 10;
        }
        x = 200;
        for(Patient p : waitingRoom){
            p.redraw(x, y);
            x += 10;
        }
    }

}
