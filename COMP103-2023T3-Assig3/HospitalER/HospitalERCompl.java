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
import java.awt.Color;
/**
 * Simulation of a Hospital ER
 * 
 * The hospital has a collection of Departments, including the ER department, each of which has
 *  and a treatment room.
 * 
 * When patients arrive at the hospital, they are immediately assessed by the
 *  triage team who determine the priority of the patient and (unrealistically) a sequence of treatments 
 *  that the patient will need.
 *
 * The simulation should move patients through the departments for each of the required treatments,
 * finally discharging patients when they have completed their final treatment.
 *
 *  READ THE ASSIGNMENT PAGE!
 */

public class HospitalERCompl{

    /**
     * The map of the departments.
     * The names of the departments should be "ER", "Surgery", "X-ray", "MRI", and "Ultrasound"
     */

    private Map<String, Department> departments = new HashMap<String, Department>();

    // Copy the code from HospitalERCore and then modify/extend to handle multiple departments

    // fields for the statistics
    private List<Patient> dischargedPatients = new ArrayList<>(); //list of patients who have been discharged

    // Fields for the simulation
    private boolean running = false;
    private int time = 0; // The simulated time - the current "tick"
    private int delay = 300;  // milliseconds of real time for each tick

    private double oldPri1 = 0.1;
    private double oldPri2 = 0.2;
    
    /**
     * stop any running simulation
     * Define the departments available and put them in the map of departments.
     * Each department needs to have a name and a maximum number of patients that
     * it can be treating at the same time.
     * reset the statistics
     */
    public void reset(boolean usePriorityQueues){
        running = false;
        UI.sleep(2*delay);  // to make sure that any running simulation has stopped
        time = 0;
        loadDepartments(usePriorityQueues); //adds all the departments

        //clear all departments
        for(Department d : departments.values()){
            d.clear(usePriorityQueues);
        }
        dischargedPatients.clear(); // clear list of discharged patients

        UI.clearGraphics();
        UI.clearText();
        UI.sleep(2*delay);
        UI.println("Ready for next simulation");
    }

    /**
     * Loads all the departments into the field for proper acces
     */
    public void loadDepartments(boolean usePriorityQueues){
        departments.put("ER", new Department("ER", 5, usePriorityQueues, Color.red));
        departments.put("Surgery", new Department("Surgery", 8, usePriorityQueues, Color.green));
        departments.put("X-ray", new Department("X-ray", 2, usePriorityQueues, Color.blue));
        departments.put("MRI", new Department("MRI", 4, usePriorityQueues, Color.orange));
        departments.put("Ultrasound", new Department("Ultrasound", 1, usePriorityQueues, Color.magenta));
    }

    /**
     * Main loop of the simulation
     */
    public void run(){

        if (running) { return; } // don't start simulation if already running one!
        running = true;
        while (running){         // each time step, check whether the simulation should pause.
            time++;
            // Hint: if you are stepping through a set, you can't remove
            //   items from the set inside the loop!
            //   If you need to remove items, you can add the items to a
            //   temporary list, and after the loop is done, remove all 
            //   the items on the temporary list from the set.

            //all patients in all departments + treatment room, 
            //if completed, add to map of all completed to remove 
            //Map<String, Patient> completedTreatment = new HashMap<>();
            
            //map for all patients that have completed treatment to remove and alter later
            Map<Patient, String> completedTreatment = new HashMap<>();
            for(String currentDep : departments.keySet()){
                //checks if there are patients in the treating room
                if(departments.get(currentDep).isTreating()){
                    for(Patient patient : departments.get(currentDep).getTreatmentRoom()){
                        if(patient.currentTreatmentFinished()){
                            patient.removeCurrentTreatment();
                            completedTreatment.put(patient, currentDep);
                        }
                    }
                }
            }

            //then iterate through extracted list of completed treatments
            if(!completedTreatment.isEmpty()){
                for(Map.Entry<Patient, String> patient : completedTreatment.entrySet()){
                    //if the patient has finished all patients 
                    if(departments.get(patient.getValue()).getPatient(patient.getKey()).allTreatmentsCompleted()){
                        departments.get(patient.getValue()).remove(patient.getKey());//remove from old department
                        dischargedPatients.add(patient.getKey());//add to list of discharged patients
                        UI.printf("%d: Discharged) Patient %s has been discharged\n", time, patient.getKey());
                        UI.println();
                    }else{
                        departments.get(patient.getKey().getCurrentDepartment()).moveFrom(departments.get(patient.getValue()), patient.getKey());
                        UI.printf("%d: Moved)  Patient %s had been moved to %s from %s\n",time,  patient.getKey(), departments.get(patient.getKey().getCurrentDepartment()).getDepartmentName(), patient.getValue());
                        UI.println();
                    }
                }
            }

            //advance wait time by one for all departments
            for(Department d : departments.values()){
                for(Patient p : d.getTreatmentRoom()){
                    p.advanceCurrentTreatmentByTick();
                }
                for(Patient p : d.getWaitingRoom()){
                    p.waitForATick();
                    d.increaseDepartmentWaitTotal();// challenge, increases the total count of patient wait
                }
            }

            //check all treatment rooms, check if waiting room is empty and add to treatment room
            for(Department d : departments.values()){
                while(d.hasSpace() && !d.getWaitingRoom().isEmpty()){
                    d.move();
                }
            }
            
            //Lets user know when an influx starts
            double prob1 = PatientGenerator.getProbPri1();
            double prob2 = PatientGenerator.getProbPri2();
            if(prob1 > oldPri1 && prob1 > 0.4){
                UI.println("Scenario start: Priority One surge");
            }
            if(prob2 > oldPri2 && prob2 > 0.4){
                UI.println("Scenario start: Priority Two surge");
            }
            this.oldPri1 = prob1;
            this.oldPri2 = prob2;
            // Gets any new patient that has arrived and adds them to the waiting room
            Patient newPatient = PatientGenerator.getNextPatient(time);
            if (newPatient != null){
                UI.println(time + ": Arrived: " + newPatient);
                UI.println();
                departments.get("ER").add(newPatient); // should always start in ER so put in that department
            }
            redraw();
            UI.sleep(delay);
        }
        //paused, so report current statistics
        reportStatistics();
    }

    

    /**
     * Report summary statistics about the simulation
     */
    public void reportStatistics(){
        //if no patients discharged no point continuing
        if(dischargedPatients.isEmpty()){
            UI.println("No discharged patients to report on");
            return;
        }
        UI.printf("Processed %d patients with an average wait time of %d minutes\n", dischargedPatients.size(), avgWaitTime());
        // reports on priority one patients
        if(totalPriorityOne() > 0) UI.printf("Processed %d priority 1 patients with an average waiting time of %d minutes\n", totalPriorityOne(), avgTimeOfOnes());
        else UI.println("No priority 1 patients to report on");
        
        if(totalPriorityTwo() > 0) UI.printf("Processed %d priority 2 patients with an average waiting time of %d minutes\n", totalPriorityTwo(), avgTimeOfTwos());
        else UI.println("No priority 2 patients to report on");
        
        if(totalPriorityThree() > 0) UI.printf("Processed %d priority 3 patients with an average waiting time of %d minutes\n", totalPriorityThree(), avgTimeOfThrees());
        else UI.println("No priority 3 patients to report on");
        
        UI.printf("The average patient spent %d minutes in the treatment rooms\n", avgTreatmentTime());
        UI.println("--------DEPARTMENTS--------");
        for(Department d : departments.values()){
            UI.printf("Department %s: \nTotal patients seen: %d \nTotal Wait: %d minutes\n", d.getDepartmentName(), d.getTotalPatientsSeen(), d.getTotalWait());
        }
    }

    /**
     * Returns the avergae treatment time of all discharged patients 
     */
    public int avgTreatmentTime(){
        if(dischargedPatients.size() <= 0) return 0;
        int totalTime = 0;
        for(Patient p : dischargedPatients){
            totalTime += p.getTotalTreatmentTime();
        }
        return totalTime / dischargedPatients.size();
    }

    /**
     * Returns the average wait time for all patients
     */
    public int avgWaitTime(){
        if(dischargedPatients.size() <= 0) return 0;
        int totalTime = 0;
        for(Patient p : dischargedPatients){
            totalTime += p.getTotalWaitingTime();
        }
        return totalTime / dischargedPatients.size();
    }

    /**
     * return the average priority seen throughout
     */
    public int avgPriority(){
        if(dischargedPatients.size() <= 0) return 0;
        int allPrior = 0;
        for(Patient p : dischargedPatients){
            allPrior += p.getPriority();
        }
        return allPrior / dischargedPatients.size();
    }

    /**
     * Returns the total amount of priority one patients seen
     */
    public int totalPriorityOne(){
        if(dischargedPatients.size() <= 0) return 0;
        int allOne = 0;
        for(Patient p : dischargedPatients){
            if(p.getPriority() == 1){
                allOne++;
            }
        }
        return allOne;
    }

    /**
     * Returns the total average amount of time priority one patients had to wait
     */
    public int avgTimeOfOnes(){
        int allOne = 0;
        int numOfOnes = 0;
        for(Patient p : dischargedPatients){
            if(p.getPriority() == 1){
                allOne += p.getTotalWaitingTime();
                numOfOnes++;
            }
        }
        if(numOfOnes != 0) return allOne / numOfOnes;
        return 0;
    }
    
    /**
     * Returns the average amount of time priority two patients had to wait
     */
    public int avgTimeOfTwos(){
        int allTwo=0;
        int numOfTwo = 0;
        for(Patient p : dischargedPatients){
            if(p.getPriority() == 2){
                allTwo += p.getTotalWaitingTime();
                numOfTwo++;
            }
        }
        if(numOfTwo != 0) return allTwo / numOfTwo;
        return 0;
    }
    
    /**
     * Returns the total amount of priority two patients
     */
    public int totalPriorityTwo(){
        if(dischargedPatients.size() <= 0) return 0;
        int allTwo = 0;
        for(Patient p : dischargedPatients){
            if(p.getPriority() == 2){
                allTwo++;
            }
        }
        return allTwo;
    }
    
    /**
     * Returns the total amount of priority three patients seen
     */
    public int totalPriorityThree(){
        if(dischargedPatients.size() <= 0) return 0;
        int allThree = 0;
        for(Patient p : dischargedPatients){
            if(p.getPriority() == 3){
                allThree++;
            }
        }
        return allThree;
    }

    /**
     * Returns the total average amount of time priority three patients had to wait
     */
    public int avgTimeOfThrees(){
        int allThree = 0;
        int numOfThree = 0;
        for(Patient p : dischargedPatients){
            if(p.getPriority() == 3){
                allThree += p.getTotalWaitingTime();
                numOfThree++;
            }
        }
        if(numOfThree != 0) return allThree / numOfThree;
        return 0;
    }
    
    // METHODS FOR THE GUI AND VISUALISATION
    /**
     * Set up the GUI: buttons to control simulation and sliders for setting parameters
     */
    public void setupGUI(){
        UI.addButton("Reset (Queue)", () -> {this.reset(false); });
        UI.addButton("Reset (Pri Queue)", () -> {this.reset(true);});
        UI.addButton("Start", ()->{if (!running){ run(); }});   //don't start if already running!
        UI.addButton("Pause & Report", ()->{running=false;});
        UI.addSlider("Speed", 1, 400, (401-delay),
            (val)-> {delay = (int)(401-val);});
        UI.addSlider("Av arrival interval", 1, 50, PatientGenerator.getArrivalInterval(),
            PatientGenerator::setArrivalInterval);
        UI.addSlider("Prob of Pri 1", 1, 100, PatientGenerator.getProbPri1(),
            PatientGenerator::setProbPri1);
        UI.addSlider("Prob of Pri 2", 1, 100, PatientGenerator.getProbPri2(),
            PatientGenerator::setProbPri2);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1000,600);
        UI.setDivider(0.5);
    }

    /**
     * Redraws all the departments
     * Also draws a 'live' graph displaying the patient wait times by department
     */
    public void redraw(){
        UI.setColor(Color.black);
        UI.clearGraphics();
        UI.setFontSize(14);
        UI.drawString("Treating Patients", 5, 15);
        UI.drawString("Waiting Queues", 200, 15);
        UI.drawLine(0,32,400, 32);
        //graph title
        UI.drawString("Average Wait Time For a Patient per Department (live)",50, 295);
        
        double y = 80;
        for (Department dept : departments.values()){
            dept.redraw(y);
            UI.drawLine(0,y+2,400, y+2);
            y += 50;
        }
        UI.setColor(Color.gray);
        UI.drawLine(50, 300, 50, 550);
        UI.setFontSize(5);
        //x axis
        for(int i = 0; i < 400; i+= 20){
            String j = String.valueOf(i);
            UI.drawString(j, (50 + i), 555); 
        }
        UI.drawString("400+", 450, 555);
        UI.setFontSize(12);
        //axis title
        UI.drawString("Average Wait Time (mins)", 200, 570);
        
        UI.setFontSize(8);
        double g = 300;
        //draws the department on the graph
        for(Department dept : departments.values()){
            int seen = dept.getTotalPatientsSeen();
            int wait = dept.getTotalWait();
            UI.setColor(dept.getDepartmentColor());
            UI.drawString(dept.getDepartmentName(), 5, g + 25);
            if(seen != 0 && wait >= 0){
                UI.setColor(dept.getDepartmentColor());
                if(wait/seen > 400) UI.fillRect(100, g, 400, 40);
                else UI.fillRect(50, g, wait/seen, 40);
            }
            g += 50;
        }
    }

    /**
     * Construct a new HospitalER object, setting up the GUI, and resetting
     */
    public static void main(String[] arguments){
        HospitalERCompl er = new HospitalERCompl();
        er.setupGUI();
        er.reset(false);   // initialise with an ordinary queue.
        er.loadDepartments(false); //load all departments with ordinary queues
    }        

}
