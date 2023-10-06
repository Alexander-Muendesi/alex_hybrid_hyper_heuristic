package constructor_classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import constraints.Constraints;
import data_classes.Course;
import data_classes.DataReader;

public class Solutions {
    private final DataReader reader;
    public String[] timetable;//numDays x PeriodsPerDay x numRooms
    private Random random;
    private final Constraints constraints;

    /**
     * 
     * @param random
     * @param reader
     */
    public Solutions(DataReader reader, Random random){
        this.reader = reader;
        this.random = random;
        this.constraints = new Constraints(reader,random);
        timetable = new String[reader.numDays*reader.periodsPerDay*reader.rooms.size()];
        initializeTimetable();
    }

    public String[] generateSolution(){
        List<Course> courses = fisherYatesShuffle(reader.courses);

        int coursesAssigned = courses.size();//keep track of how many courses are left to be fully assigned.
        int[] lecturesAssigned = new int[courses.size()];
        boolean[] finishedCourses = new boolean[courses.size()];

        for(int i=0;i>courses.size();i++){
            finishedCourses[i] = false;
            lecturesAssigned[i] = 0;
        }


        while(coursesAssigned > 0){
            int day=0,period=0,roomIndex=0, numPlaces=0, index=0, lowestPlaces = Integer.MAX_VALUE, worst = 0;
    
            //find the course that can be placed in the least amount of places
            for(Course course: reader.courses){
                day=0;period=0;roomIndex=0;numPlaces=0;
    
                for(int i=0;i<timetable.length && lowestPlaces != 0;i++){
                    if(timetable[i] == null){
                        boolean allConstraintsSatisfied = true;
                        
                        allConstraintsSatisfied = constraints.roomOccupancyConstraint(timetable, day, period, roomIndex);
                        allConstraintsSatisfied = constraints.teacherConstraint(timetable,day,period,course.teacherId);
                        allConstraintsSatisfied = constraints.conflictsConstraint(timetable, day, period, constraints.searchFoCurriculumByCourseId(course.courseId));
                        allConstraintsSatisfied = constraints.unavailabilityConstraint(course.courseId, day, period);
                        
                        if(allConstraintsSatisfied)
                            numPlaces++;
                    }
                    
                    // System.out.println("numPlaces: " + numPlaces + " lowestPlaces: " + lowestPlaces);
                    if(numPlaces < lowestPlaces && !finishedCourses[index]){
                        lowestPlaces = numPlaces;
                        worst = index;
                    }
    
                    roomIndex++;
                    if(roomIndex == reader.rooms.size()){
                        roomIndex = 0;
                        period++;
                    }
                    if(period == reader.periodsPerDay){
                        period = 0;
                        day++;
                    }
                }
                index++;
                if(lowestPlaces == 0)
                    break;
            }
    
            index = 0; day=0; period=0; roomIndex=0;
            Course course = courses.get(worst);
            int bestPlace = 0, currCost = 0, bestCost = Integer.MAX_VALUE;
            //find a place to assign the course where it violates the least amount of constraints
            for(int i=0; i < timetable.length; i++){
                if(timetable[i] == null){
                    timetable[i] = course.courseId;
                    currCost += constraints.teacherConstraintCost(timetable, day, period, course.teacherId);
                    currCost += constraints.conflictsConstraintCost(timetable, day, period, constraints.searchFoCurriculumByCourseId(course.courseId));
                    currCost += constraints.roomCapacityConstraintCost(course.courseId, roomIndex);
                    currCost += constraints.minimumWorkingDaysConstraintCost(timetable);
                    currCost += constraints.curriculumCompactnessCost(timetable);
                    currCost += constraints.roomStabilityConstraintCost(timetable);

                    // System.out.println("currCost: " + currCost + " bestCost: " + bestCost);
                    if(currCost < bestCost && constraints.unavailabilityConstraint(course.courseId, day, period)){
                        bestCost = currCost;
                        bestPlace = index;
                    }
                    timetable[i] = null;
                }


                roomIndex++;
                if(roomIndex == reader.rooms.size()){
                    roomIndex = 0;
                    period++;
                }
                if(period == reader.periodsPerDay){
                    period = 0;
                    day++;
                }
                index++;
                currCost = 0;
            }

            timetable[bestPlace] = course.courseId;
            lecturesAssigned[worst]++;
            if(lecturesAssigned[worst] == course.numLectures){
                finishedCourses[worst] = true;
                coursesAssigned--;
            }

            // System.out.println("Worst: " + worst);
            // for(int val: lecturesAssigned)
            //     System.out.print(val + " ");
            // System.out.println();
        }

        return timetable;
    }

    private void initializeTimetable(){
        for(int i=0; i<timetable.length;i++)
            timetable[i] = null;
    }

    /**
     * This methods performs a shuffle on the courses list so that the initial solution is diverse
     * @param courses
     * @return shuffled list of courses
     */
    private List<Course> fisherYatesShuffle(List<Course> courses){
        for(int i=courses.size() - 1;i > 0; i--){
            int j= random.nextInt(i+1);

            //swap the elements at the i an j indices
            Course temp = courses.get(i);
            courses.set(i, courses.get(j));
            courses.set(j,temp);
        }

        return courses;
    }

    // public void printTimetable(){
    //     for(int day=0; day < reader.numDays; day++){
    //         for(int period = 0; period < reader.periodsPerDay; period++){
    //             for(int roomIndex = 0; roomIndex < reader.rooms.size(); roomIndex++){
    //                 String courseId = timetable[day][period][roomIndex];
    //                 System.out.print(courseId != null ? courseId : " - ");
    //                 System.out.print("\t");
    //             }
    //             System.out.println();
    //         }
    //         System.out.println();
    //     }
    // }
}
