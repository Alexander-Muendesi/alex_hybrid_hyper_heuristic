package constructor_classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import constraints.Constraints;
import data_classes.Course;
import data_classes.Curriculum;
import data_classes.DataReader;

public class Timetable {
    private String[] timetable;
    public int fitness;
    private int hardConstraintCost;
    private int softConstraintCost;
    private final DataReader reader;
    private final Constraints constraints;
    private final Random random;
    public List<String> deletedCourses; //keeps track of courses that were deleted
    public int []fitnessArray;

    //TOOD: might have to add a copy method for the timetable class so that we keep track of the deletedCourses!!

    public Timetable(String[] timetable, DataReader reader, Random random){
        this.timetable = getTimetableCopy(timetable);
        this.random = random;
        fitness = Integer.MAX_VALUE;
        hardConstraintCost = Integer.MAX_VALUE;
        softConstraintCost = Integer.MAX_VALUE;
        this.reader = reader;
        this.constraints = new Constraints(reader, random);
        this.deletedCourses = new ArrayList<String>();
    }

    public Timetable copy(){
        String[] tempTimetable = getTimetableCopy(this.timetable);
        Timetable t = new Timetable(tempTimetable, reader, random);
        List<String> dCourses = new ArrayList<String>(this.deletedCourses);

        t.deletedCourses = dCourses;
        t.fitness = this.fitness;
        t.hardConstraintCost = this.hardConstraintCost;
        t.softConstraintCost = this.softConstraintCost;

        return t;
    }

    /**
     * 0 is for hard constraints, 1 for softconstraints
     * @return
     */
    public int[] calculateFitness(){
        hardConstraintCost = calculateHardConstraintCost();
        softConstraintCost = calculateSoftConstraintCost();
        // fitness = hardConstraintCost + softConstraintCost;
        int []arr = {hardConstraintCost,softConstraintCost}; 

        this.fitnessArray = arr;
        this.fitness = hardConstraintCost + softConstraintCost;
        return arr;
    }

    public int calculateHardConstraintCost(){
        //lecture constraint is never violated as of current setup with low level heuristics
        //room occupancy is never violated as of current setup with low level heuristics
        
        //check the teacherConstraint
        int teacherConstraintCost = 0;

        for(Course c: reader.courses){
            for(int day=0; day < reader.numDays; day++)
                for(int period=0; period < reader.periodsPerDay; period++)
                    teacherConstraintCost += constraints.teacherConstraintCost(timetable, day, period, c.teacherId);
        }

        //check the conflicts hard constraint
        int conflictsConstraintCost = 0;

        for(Curriculum c: reader.curricula){
            for(int day=0; day < reader.numDays; day++)
                for(int period=0; period < reader.periodsPerDay; period++)
                conflictsConstraintCost += constraints.conflictsConstraintCost(timetable, day, period, c);
        }

        return conflictsConstraintCost + teacherConstraintCost + deletedCourses.size();
    }

    /**
     * This methd calculates the soft constraint costs of the current timetable
     * @return Soft constraint cost
     */
    public int calculateSoftConstraintCost(){
        int cost = 0;
        int roomCapacityConstraintCost = constraints.roomCapacityConstraintCost(timetable);
        int minimumWorkingDaysConstraintCost = constraints.minimumWorkingDaysConstraintCost(timetable);
        int curriculumCompactnessCost = constraints.curriculumCompactnessCost(timetable);
        int roomStabilityConstraintCost = constraints.roomStabilityConstraintCost(timetable);

        cost = roomCapacityConstraintCost + minimumWorkingDaysConstraintCost + curriculumCompactnessCost + roomStabilityConstraintCost;
        return cost;
    }


    /**
     * Updates timetable with a new timetable
     * @param timetable
     */
    public void setTimetable(String[] timetable){
        this.timetable = getTimetableCopy(timetable);
    }

    public String[] getTimetable(){
        return this.timetable;
    }
    /**
     * Creates a copy of the current timetable
     * @param timetable
     * @return copy of timetable
     */
    public String[] getTimetableCopy(String[] timetable){
        String []result = new String[timetable.length];

        for(int k=0; k < timetable.length; k++)
            if(timetable[k] != null)
                result[k] = new String(timetable[k]);
            else
                result[k] = null;

        return result;
    }

    /**
     * @param n number of solution components involved
     * @param compSel selection Method
     * @param comp type of solution component
     */
    public void applySwapOperator(String n, String compSel, String comp){
        int numComponentsInvolved = Integer.parseInt(n);
        List<Integer> result = new ArrayList<Integer>();

        if(compSel.equals("lowestCost")){
            if(comp.equals("lecture")){
                result = constraints.findLowestCostLecture(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findLowestCostPeriod(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findHighestCostPeriod(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("highestCost")){
            if(comp.equals("lecture")){
                result = constraints.findHighestCostLecture(numComponentsInvolved,timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findHighestCostPeriod(numComponentsInvolved, timetable); 
            }
            else if(comp.equals("room")){
                result = constraints.findHighestCostRoom(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("smallestSize")){
            if(comp.equals("lecture")){
                result = constraints.findLowestSizeLecture(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findLowestCostPeriod(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findLowestCostRoom(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("largestSize")){
            if(comp.equals("lecture")){
                result = constraints.findHighestSizeLecture(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result  = constraints.findHighestSizePeriod(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findHighestSizeRoom(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("random")){
            if(comp.equals("lecture")){
                result = constraints.findRandom(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findRandom(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findRandom(numComponentsInvolved, timetable);
            }
        }
        else{
            System.out.println("Error in applySwapOperator in Timetable.java");
            System.exit(-1);
        }

        for(int i=0; i<result.size(); i += 2){
            if(i+1 >= result.size()){//only one index to swap
                int index = result.get(i);
                int newIndex = random.nextInt(timetable.length);
                String temp = timetable[index];

                timetable[index] = timetable[newIndex];
                timetable[newIndex] = temp;
            }
            else{
                int index1 = result.get(i);
                int index2 = result.get(i+1);

                if(index1 >=0  && index1 < timetable.length && index2 >= 0 && index2 < timetable.length){
                    String temp =  timetable[index1];
                    timetable[index1] = timetable[index2];
                    timetable[index2] = timetable[index1];
                }
            }
        }
    }

    /**
     * 
     * @param n
     * @param compSel
     * @param comp
     */
    public void applyMoveOperator(String n, String compSel, String comp){
        int numComponentsInvolved = Integer.parseInt(n);
        List<Integer> result = new ArrayList<Integer>();

        if(compSel.equals("lowestCost")){
            if(comp.equals("lecture")){
                result = constraints.findLowestCostLecture(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findLowestCostPeriod(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findHighestCostPeriod(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("highestCost")){
            if(comp.equals("lecture")){
                result = constraints.findHighestCostLecture(numComponentsInvolved,timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findHighestCostPeriod(numComponentsInvolved, timetable); 
            }
            else if(comp.equals("room")){
                result = constraints.findHighestCostRoom(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("smallestSize")){
            if(comp.equals("lecture")){
                result = constraints.findLowestSizeLecture(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findLowestCostPeriod(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findLowestCostRoom(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("largestSize")){
            if(comp.equals("lecture")){
                result = constraints.findHighestSizeLecture(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result  = constraints.findHighestSizePeriod(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findHighestSizeRoom(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("random")){
            if(comp.equals("lecture")){
                result = constraints.findRandom(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findRandom(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findRandom(numComponentsInvolved, timetable);
            }
        }
        else{
            System.out.println("Error in applyMove operator in Timetable.java");
            System.exit(-1);
        }

        for(int i=0; i<result.size(); i++){
            int index = result.get(i);

            int randomIndex = random.nextInt(timetable.length);

            String temp = timetable[index];
            timetable[index] = timetable[randomIndex];
            timetable[randomIndex] = temp;
        }
        
    }

    /**
     * 
     * @param n
     * @param compSel
     * @param comp
     */
    public void applyAddOperator(String n, String compSel, String comp){
        if(deletedCourses.isEmpty())
            return;

        int numComponentsInvolved = Integer.parseInt(n);
        
        for(int i=0; i < numComponentsInvolved && !deletedCourses.isEmpty(); i++){
            int randomCourseIndex = random.nextInt(deletedCourses.size());
            String course = deletedCourses.get(randomCourseIndex);

            int timetableIndex = random.nextInt(timetable.length);
            while(timetable[timetableIndex] != null)
                timetableIndex = random.nextInt(timetable.length);

            timetable[timetableIndex] = course;
        }


    }

    /**
     * 
     * @param n
     * @param compSel
     * @param comp
     */
    public void applyDeleteOperator(String n, String compSel, String comp){
        int numComponentsInvolved = Integer.parseInt(n);

        List<Integer> result = new ArrayList<Integer>();

        if(compSel.equals("lowestCost")){
            if(comp.equals("lecture")){
                result = constraints.findLowestCostLecture(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findLowestCostPeriod(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findHighestCostPeriod(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("highestCost")){
            if(comp.equals("lecture")){
                result = constraints.findHighestCostLecture(numComponentsInvolved,timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findHighestCostPeriod(numComponentsInvolved, timetable); 
            }
            else if(comp.equals("room")){
                result = constraints.findHighestCostRoom(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("smallestSize")){
            if(comp.equals("lecture")){
                result = constraints.findLowestSizeLecture(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findLowestCostPeriod(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findLowestCostRoom(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("largestSize")){
            if(comp.equals("lecture")){
                result = constraints.findHighestSizeLecture(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result  = constraints.findHighestSizePeriod(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findHighestSizeRoom(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("random")){
            if(comp.equals("lecture")){
                result = constraints.findRandom(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findRandom(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findRandom(numComponentsInvolved, timetable);
            }
        }
        else{
            System.out.println("Error in applyDelete operator in Timetable.java");
            System.exit(-1);
        }

        for(int i=0; i<result.size(); i++){
            int index = result.get(i);

            if(timetable[index] != null){
                deletedCourses.add(timetable[index]);
                timetable[index] = null;
            }
        }
    }

    public void applyShuffleOperator(String n, String compSel, String comp){
        int numComponentsInvolved = Integer.parseInt(n);
        List<Integer> result = new ArrayList<Integer>();

        if(compSel.equals("lowestCost")){
            if(comp.equals("lecture")){
                result = constraints.findLowestCostLecture(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findLowestCostPeriod(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findHighestCostPeriod(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("highestCost")){
            if(comp.equals("lecture")){
                result = constraints.findHighestCostLecture(numComponentsInvolved,timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findHighestCostPeriod(numComponentsInvolved, timetable); 
            }
            else if(comp.equals("room")){
                result = constraints.findHighestCostRoom(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("smallestSize")){
            if(comp.equals("lecture")){
                result = constraints.findLowestSizeLecture(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findLowestCostPeriod(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findLowestCostRoom(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("largestSize")){
            if(comp.equals("lecture")){
                result = constraints.findHighestSizeLecture(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result  = constraints.findHighestSizePeriod(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findHighestSizeRoom(numComponentsInvolved, timetable);
            }
        }
        else if(compSel.equals("random")){
            if(comp.equals("lecture")){
                result = constraints.findRandom(numComponentsInvolved, timetable);
            }
            else if(comp.equals("period")){
                result = constraints.findRandom(numComponentsInvolved, timetable);
            }
            else if(comp.equals("room")){
                result = constraints.findRandom(numComponentsInvolved, timetable);
            }
        }
        else{
            System.out.println("Error in applyShuffle operator in Timetable.java");
            System.exit(-1);
        }

        for(int i=result.size() -1; i>0;i--){
            int j = random.nextInt(i+1);
            String temp = timetable[result.get(i)];
            timetable[result.get(i)] = timetable[result.get(j)];
            timetable[result.get(j)] = temp;
        }
    }
}
