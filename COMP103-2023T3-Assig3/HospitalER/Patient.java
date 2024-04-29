// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 3
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

/**
 * Represents an ER Patient at a Hospital
 * Each Patient has
 *     a name and
 *     initials,
 *     an arrival time,
 *     total waiting time
 *     total treatment time
 *  and has been examined by the triage team at reception who has assigned
 *     a priority (1 - 3)  and
 *     a queue of treatments that Patient must have
 *       (scans, examinations, operations, etc)
 *       each treatment will have a department name and the amount of time the treatment will take
 *     The treatment at the head of the queue is the "current treatment" which the patient is about to start
 *       or is currently undergoing.
 *   NOTE: For the core, you can ignore all but the first treatment.
 * 
 *  Possible departments:  ER, MRI, Surgery, X-ray Ultrasound

 * Methods:
 *   compareTo(other)        -> int       [You need to complete]
 *   redraw(x, y)
 *   toString()              -> String
 *   getPriority()           -> int
 *   getTotalWaitingTime()   -> int
 *   getTotalTreatmentTime() -> int
 *
 *   waitForATick()
 *   advanceCurrentTreatmentByTick()
 *   currentTreatmentFinished() -> boolean
 *
 *   [THE FOLLOWING ARE NOT NEEDED FOR THE CORE]
 *   allTreatmentsCompleted()    -> boolean  
 *   removeCurrentTreatment
 *   getCurrentDepartment()       -> String (name of department for the treatment needed now)
 */

public class Patient implements Comparable<Patient>{

    private String name;
    private String initials;
    private int arrival;
    private int priority;  // 1 is highest priority, 3 is lowest priority
    private Queue<Treatment> treatments = new ArrayDeque<Treatment>();
    private int totalWaitTime;
    private int totalTreatmentTime;

    /**
     * Construct a new Patient object
     * parameters are the arrival time.
     * construct random priority level
     * construct random name and initials.
     * construct random sequence of treatments for the Patient
     */
    public Patient(int time, int priority, String firstName, String lastName, Queue<Treatment> treatments){
        arrival = time;
        this.priority = priority;
        name = firstName + " " + lastName;
        initials = firstName.substring(0,1)+lastName.substring(0,1);
        this.treatments = treatments;
    }
    
    /**
     * Returns the treatments required by patient
     */
    public Queue<Treatment> getTreatments(){return treatments; }
    
    /**
     * Prints name for User information
     */
    public String getName(){return name; }
    
    
    /**
     * Return the Patient's priority
     */
    public int getPriority(){ return priority; }

    /**
     * Return the Patient's total time spent waiting
     */
    public int getTotalWaitingTime(){ return totalWaitTime; }

    /**
     * Return the Patient's total time in treatment
     */
    public int getTotalTreatmentTime(){ return totalTreatmentTime; }
    
    /**
     * Compare this Patient with another Patient to determine who should
     *  be treated first.
     * A patient should be earlier in the ordering if they should be treated first.
     * The ordering depends on the triage priority and the arrival time.
     */
    public int compareTo(Patient other){
        int byPriority = Integer.compare(this.getPriority(),other.getPriority());
        if(byPriority == 0){
            return other.getTotalWaitingTime() - this.getTotalWaitingTime();
        }else{
            return byPriority;
        }
    }

    // Methods for simulating the progress of the Patient through their waiting and their treatments
    
    /**
     * Make Patient wait for one tick.
     * added to patient's totalWaitTime
     */
    public void waitForATick(){
        totalWaitTime++;
    }

    /**
     * Reduce the remaining time of the current treatment by 1 tick. 
     * Throws an exception if the patient has already completed this treatment.
     *  or if the treatment at the head of the queue is finished
     *  (Ie, always ensure that the patient is not yet completed before calling) 
     */
    public void advanceCurrentTreatmentByTick(){
        if (treatments.isEmpty()){
            throw new RuntimeException("patient doesn't have any treatments to do: "+this);
        }
        if (treatments.peek().getTimeRemaining()==0){
            throw new RuntimeException("patient has finished this treatment: "+this);
        }
        totalTreatmentTime++;
        treatments.peek().advanceTime();
    }

    /**
     * Return true if patient has completed their current treatment
     */
    public boolean currentTreatmentFinished(){
        if (treatments.isEmpty()){
            throw new RuntimeException("patient doesn't have any treatments to do: "+this);
        }
        return (treatments.peek().getTimeRemaining()==0);
    }

    /**
     * Return true if the patient has completed all their treatments.
     */
    public boolean allTreatmentsCompleted(){
        return (treatments.isEmpty());

    }

    /**
     * Return the name of the department for the current treatment
     *  (the one about to start or in progress)
     * Throws an exception if the patient is all done.
     */
    public String getCurrentDepartment(){
        if (treatments.isEmpty()){
            throw new RuntimeException("patient already completed all treatments: "+this);
        }
        return treatments.peek().getDepartment();
    }

    /**
     * Remove the current treatment from the queue of treatments
     * Note:  Doesn't actually move them to the next department!!
     * Throws an exception if the patient has no more treatments to visit
     */
    public void removeCurrentTreatment(){
        if (treatments.isEmpty()){
            throw new RuntimeException("patient already completed all treatments: "+this);
        }
        treatments.poll();
    }

    //========== Methods for printing and drawing Patients ======================================

    /**
     * toString: Descriptive string of most of the information in the patient
     */
    public String toString(){
        StringBuilder ans = new StringBuilder(name);
        ans.append(", pri:").append(priority).append(", Ar:").append(arrival).append(", ").
        append("treat: ").append(totalTreatmentTime).append(" wait: ").append(totalWaitTime).append("\n    ");
        ans.append(treatments.size()).append(" treatments:");
        ans.append(treatments.toString());
        return ans.toString();
    }

    /**
     * Draw the patient:
     * 6 units wide, 28 units high
     * x,y specifies center of the base
     */
    public void redraw(double x, double y){
        if (priority == 1) UI.setColor(Color.red);
        else if (priority == 2) UI.setColor(Color.orange);
        else UI.setColor(Color.green);
        UI.fillOval(x-3, y-28, 6, 8);
        UI.fillRect(x-3, y-20, 6, 20);
        UI.setColor(Color.black);
        UI.drawOval(x-3, y-28, 6, 8);
        UI.drawRect(x-3, y-20, 6, 20);
        UI.setFontSize(10);
        UI.drawString(""+initials.charAt(0), x-3,y-10);
        UI.drawString(""+initials.charAt(1), x-3,y-1);
    }

}
