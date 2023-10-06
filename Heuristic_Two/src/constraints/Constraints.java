package constraints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import GE.GrammaticalEvolution;
import data_classes.Constraint;
import data_classes.Course;
import data_classes.Curriculum;
import data_classes.DataReader;
import data_classes.Room;

public class Constraints {
    private final DataReader reader;
    private final Random random;

    public Constraints(DataReader reader, Random random){
        this.reader = reader;
        this.random = random;
    }

    //hard constraints start here

    /**
     * The required number of lectures must be scheduled in the timetable
     * @param timetable
     * @param course
     * @return true=satisfied. false=not satisfied
     */
    public boolean lecturesConstraint(String[] timetable, Course course){
        boolean stop = false;
        int numAssignedLectures = 0;

        for(int i=0; i< timetable.length && !stop;i++){
            if(numAssignedLectures < course.numLectures){
                if(course.courseId.equals(timetable[i]))
                    numAssignedLectures++;
            }
            else
                stop = true;
        }

        return stop;
    }

    /**
     * This method calculates the lecture constraint cost. Each lecture not scheduled for a course is worth one point
     * @param timetable
     * @return cost
     */
    public int lectureConstraintCost(String[] timetable){
        Map<String,Integer> costs = new HashMap<String,Integer>();

        for(int i=0;i<timetable.length;i++){
            if(timetable[i] != null){
                if(!costs.containsKey(timetable[i])){
                    costs.put(timetable[i], 1);
                }
                else{
                    costs.put(timetable[i],costs.get(timetable[i])+1);
                }
            }
        }

        int cost = 0;
        for(Map.Entry<String,Integer> entry : costs.entrySet()){
            Course c = reader.coursesMap.get(entry.getKey());
            int numLectures = entry.getValue();

            cost += Math.abs(c.numLectures-numLectures);
        }

        return cost;
    }

    /**
     * Each room must only be scheduled once in a period.
     * @param timetable
     * @param day
     * @param period
     * @param roomIndex
     * @return true=available, false = not available
     */
    public  boolean roomOccupancyConstraint(String[] timetable,int day, int period, int roomIndex){
        int targetIndex = day * (reader.periodsPerDay * reader.rooms.size()) + period * reader.rooms.size() + roomIndex;
        return timetable[targetIndex] == null;//check if the room is available at the current period in the current day
    }

    /**
     * Each teacher must not be scheduled more than once in a period
     * @param timetable
     * @param day
     * @param period
     * @param teacherId
     * @return true=available, false= not available
     */
    public boolean teacherConstraint(String[] timetable, int day, int period, String teacherId){
        for(int roomIndex=0;roomIndex < reader.rooms.size(); roomIndex++){
            int targetIndex = day * (reader.periodsPerDay * reader.rooms.size()) + period * reader.rooms.size() + roomIndex;
            String courseId = timetable[targetIndex];

            if(courseId != null){
                Course course = searchForCourse(courseId);
                if(course == null){
                    System.out.println("There is null even though there shouldn't be in teacher Constraint method");
                    return false;
                }
                else{
                    if(teacherId.equals(course.teacherId))
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * This method calculates the cost of teacher constraint in the timetable
     * @param timetable
     * @param day
     * @param period
     * @param teacherId
     * @return
     */
    public int teacherConstraintCost(String[] timetable, int day, int period, String teacherId){
        int cost = -1;
        for(int roomIndex=0;roomIndex < reader.rooms.size();roomIndex++){
            int targetIndex = day * (reader.periodsPerDay * reader.rooms.size()) + period * reader.rooms.size() + roomIndex;
            String courseId =  timetable[targetIndex];

            if(courseId != null){
                Course course = searchForCourse(courseId);
                if(course == null){
                    System.out.println("Null even though there should not be in teacherConstraintCost");
                    return cost;
                }
                else{
                    if(teacherId.equals(course.teacherId)){
                        if(cost == -1)//ignore first instance of the teacherId
                            cost++;
                        else//one violation for every other occurrence of the teacher id
                            cost++;
                    }
                }
            }
        }

        return (cost == -1) ? 0 : cost;
    }

    /**
     * Conflicts hard constraint: Lectures for courses in a curriculum must be scheduled in different periods
     * @param timetable
     * @param day
     * @param period
     * @param curriculum
     * @return true=available, false = not available
     */
    public boolean conflictsConstraint(String[] timetable, int day, int period, Curriculum curriculum){
        for(int roomIndex=0;roomIndex < reader.rooms.size(); roomIndex++){
            int targetIndex = day * (reader.periodsPerDay * reader.rooms.size()) + period * reader.rooms.size() + roomIndex;
            String courseId = timetable[targetIndex];
            
            if(courseId != null){
                if(curriculum.courses.contains(courseId))
                    return false;
            }
        }

        return true;//curriculum does not have a course assigned in this period
    }

    

    /**
     * This method calculates the cost of this hard constraint being violated.
     * @param timetable
     * @param day
     * @param period
     * @param curriculum
     * @return
     */
    public int conflictsConstraintCost(String[] timetable, int day, int period, Curriculum curriculum){
        int cost = -1;

        for(int roomIndex = 0; roomIndex < reader.rooms.size();roomIndex++){
            int targetIndex = day * (reader.periodsPerDay * reader.rooms.size()) + period * reader.rooms.size() + roomIndex;
            String courseId = timetable[targetIndex];

            if(courseId != null){
                if(curriculum.courses.contains(courseId)){
                    cost++;
                }
            }
        }

        return (cost == -1) ? 0 : cost;
    }

    /**
     * This method returns the number of lectures currently assigned to a course
     * @param timetable
     * @param course
     * @return the number of assigned lectures currently for the course
     */
    public int getAssignedLecturesCount(String[] timetable,Course course){
        int lectureCount = 0;

        for(int i = 0; i < timetable.length; i++){
            if(timetable[i] != null  && course.courseId.equals(timetable[i]))
                lectureCount++;
            if(lectureCount == course.numLectures)
                break;
        }


        return lectureCount;
    }

    /**
     * This method checks if the unavailability constraints in the file are satisfied.
     * @param courseId
     * @param day
     * @param dayPeriod
     * @return true=available. false = not available
     */
    public boolean unavailabilityConstraint(String courseId, int day, int dayPeriod){
        for(Constraint constraint: reader.constraints){
            if(constraint.courseId.equals(courseId) && constraint.day == day && constraint.dayPeriod == dayPeriod)
                return false;//course is not available for this period
        }

        return true;//course is available for the period
    }

    public int unavailabilityConstraintCost(String courseId, int day, int dayPeriod){
        int cost = 0;
        for(Constraint constraint : reader.constraints)
            if(constraint.courseId.equals(courseId) && constraint.day == day && constraint.dayPeriod == dayPeriod)
                cost++;
        return cost;
    }

    /**
     * Each student above the room capacity is 1 penalty;
     * @param timetable
     * @return 
     */
    public int roomCapacityConstraintCost(String[] timetable){
        int roomCost = 0;

        int roomIndex = 0;
        for(int i=0; i<timetable.length;i++){
            if(timetable[i] != null){
                int numStudentsEnrolled = reader.coursesMap.get(timetable[i]).numStudentsEnrolled;
                int roomSize = reader.rooms.get(roomIndex).capacity;
                int difference = roomSize - numStudentsEnrolled;

                if(difference < 0){
                    roomCost += -1 * difference;
                }
            }

            roomIndex++;
            if(roomIndex == reader.rooms.size())
                roomIndex = 0;
        }
        return roomCost;
    }

    /**
     * 
     * @param timetable
     * @param courseId
     * @param roomIndex
     * @return
     */
    public int roomCapacityConstraintCost(String courseId, int roomIndex){
        int numStudentsEnrolled = reader.coursesMap.get(courseId).numStudentsEnrolled;
        int roomSize = reader.rooms.get(roomIndex).capacity;
        int difference = roomSize - numStudentsEnrolled;

        return (difference < 0) ? (-1*difference) : 0;
    }

    /**
     * The lectures of each course must be spread into the given minimum number of days.
     * Each day below the minimum counts as 5 points of penalty
     * @param timetable
     * @return
     */
    public int minimumWorkingDaysConstraintCost(String[] timetable){
        int finalCost = 0;
        for(Course course: reader.courses){
            int minWorkingDays = course.minWorkingDays;
            int currWorkingDays = 0;
            int numLecturesFound = 0;//to stop the looping process once all lectures found
            boolean stop = false;
            boolean foundLectureInDay = false;

            int day=0,period=0,roomIndex=0;
            for(int i=0;i<timetable.length && !stop;i++){
                if(timetable[i] != null && timetable[i].equals(course.courseId)){
                    numLecturesFound++;
                    foundLectureInDay = true;
                    stop = (numLecturesFound >= course.numLectures)? true : false;
                }

                roomIndex++;
                if(roomIndex == reader.rooms.size()){
                    roomIndex=0;
                    period++;
                }

                if(period == reader.periodsPerDay){
                    period = 0;
                    day++;
                    if(foundLectureInDay)
                        currWorkingDays = (!stop) ? currWorkingDays+1 : currWorkingDays;
                    foundLectureInDay = false;
                }
            }

            int result = currWorkingDays - minWorkingDays;
            if(result < 0){
                result = -1 * result * 5;
            }

            finalCost += (result < 0) ? -1*result*5 : 0;

        }
        return finalCost;
    }

    /**
     * Each isolated lecture in a curriculum counts as 2 points
     * @param timetable
     * @return
     */
    public int curriculumCompactnessCost(String[] timetable){
        int finalCost = 0;
        int isolatedLectures = 0;
        for(Curriculum c: reader.curricula){
            for(String course: c.courses){
                int lastPeriod = -1;
                int lastDay = -1;
                boolean foundCourse = false;
                //iterate through the timetable to find adjacent lectures
                int day=0,period=0,roomIndex=0;

                for(int i=0;i<timetable.length;i++){

                    if(timetable[i] != null && course.equals(timetable[i])){
                        if(!foundCourse){//found unpaired course
                            lastDay = day;
                            lastPeriod = period;
                            foundCourse = true;
                        }
                        else if(foundCourse && lastDay == day && lastPeriod+1 == period){
                            foundCourse = false;
                        }
                        else if(foundCourse){
                            isolatedLectures++;
                            lastDay = day;
                            lastPeriod = period;
                        }
                    }
                    roomIndex++;
                    if(roomIndex == reader.rooms.size()){
                        roomIndex = 0;
                        period++;
                    }
                    if(period == reader.periodsPerDay){
                        period = 0;
                        day++;

                        if(foundCourse){
                            isolatedLectures++;
                            foundCourse = false;
                        }
                    }
                }
            }
        }

        finalCost = isolatedLectures * 2;
        return finalCost;
        
    }

    /**
     * Each distinct room used for lectures of a course but the first counts as 1 point of penalty
     * @param timetable
     * @return
     */
    public int roomStabilityConstraintCost(String[] timetable){
        Map<String,List<Integer>> rooms = new HashMap<String, List<Integer>>();
        int finalCost = 0;

        for(Course course: reader.courses){
            rooms.put(course.courseId, new ArrayList<Integer>());
        }

        int roomIndex = 0;
        for(int i=0;i<timetable.length;i++){
            if(timetable[i] != null){
                rooms.get(timetable[i]).add(roomIndex);
            }
            roomIndex++;
            if(roomIndex == reader.rooms.size())
                roomIndex = 0;
        }
        
        for(Map.Entry<String,List<Integer>> val : rooms.entrySet()){
            List<Integer> value = val.getValue().stream().distinct().collect(Collectors.toList());
            String key = val.getKey();
            rooms.put(key, value);

            int cost = value.size()-1;
            if(cost > 0)
                finalCost += cost;
        }
        return finalCost;
    }
    //helper methods

    public Course searchForCourse(String courseId){
        for(Course course: reader.courses){
            if(course.courseId.equals(courseId))
                return course;
        }

        return null;
    }

    public Curriculum searchFoCurriculumByCourseId(String courseId){
        for(Curriculum c: reader.curricula){
            for(String val: c.courses)
                if(val.equals(courseId))
                    return c;
        }

        return null;
    }

    /**
     * This method finds the "numComponentsInvolved" lowest cost lectures. Cost defined in terms of room capacity constraint cost
     * @param numComponentsInvolved 
     * @param timetable
     * @return an array containing the indexes of the lowest cost lectures
     */
    public List<Integer> findLowestCostLecture(int numComponentsInvolved, String []timetable){
        //so basically if you encounter a lecture with the same id which has smaller cost replace it
        Map<String, Integer> costs = new HashMap<String, Integer>();
        Map<String, List<Integer>> costsLocations = new HashMap<String, List<Integer>>();

        int day = 0,period = 0, roomIndex = 0;

        for(int i=0;i<timetable.length;i++){
            if(timetable[i] != null){
                int cost = 0;
                //calculating costs 
                //can potentially only add the following hard constraint costs as well 1. lecture allocations 2. Conflicts
                cost += roomCapacityConstraintCost(timetable[i], roomIndex);


                if(!costs.containsKey(timetable[i])){
                    List<Integer> temp = new ArrayList<Integer>();
                    costs.put(timetable[i],cost);

                    temp = new ArrayList<Integer>();
                    temp.add(i);
                    costsLocations.put(timetable[i], temp);
                }
                else{
                    int tempCost = costs.get(timetable[i]);
                    if(cost < tempCost){//replace current data with new lowest for lecture
                        List<Integer> t = new ArrayList<Integer>();
                        costs.put(timetable[i],cost);

                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                    else if(cost == tempCost){//store location of equal lecture
                        List<Integer> t = costsLocations.get(timetable[i]);
                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                }

            }

            roomIndex++;
            if(roomIndex >= reader.numRooms){
                roomIndex = 0;
                period++;
            }
            if(period >= reader.periodsPerDay){
                period = 0;
                day++;
            }
        }

        //sort the map in ascending order
        List<Map.Entry<String,Integer>> list = new ArrayList<>(costs.entrySet());
        list.sort(Map.Entry.comparingByValue());//sort the list based on values in ascending order

        List<Integer> result = new ArrayList<Integer>();
        int counter = 0;

        for(Map.Entry<String, Integer> entry : list){
            List<Integer> temp = costsLocations.get(entry.getKey());

            for(int val : temp){
                result.add(val);
                counter++;

                if(counter >= numComponentsInvolved)
                    break;
            }

            if(counter >= numComponentsInvolved)
                break;
            
        }

        return result;
    }

    /**
     * @param numComponentsInvolved
     * @param timetable
     */
    public List<Integer> findHighestCostLecture(int numComponentsInvolved, String []timetable){
        Map<String, Integer> costs = new HashMap<String, Integer>();
        Map<String, List<Integer>> costsLocations = new HashMap<String, List<Integer>>();

        int day = 0,period = 0, roomIndex = 0;

        for(int i=0;i<timetable.length;i++){
            if(timetable[i] != null){
                int cost = 0;
                //calculating costs 
                //can potentially only add the following hard constraint costs as well 1. lecture allocations 2. Conflicts
                cost += roomCapacityConstraintCost(timetable[i], roomIndex);


                if(!costs.containsKey(timetable[i])){
                    List<Integer> temp = new ArrayList<Integer>();
                    costs.put(timetable[i],cost);

                    temp = new ArrayList<Integer>();
                    temp.add(i);
                    costsLocations.put(timetable[i], temp);
                }
                else{
                    int tempCost = costs.get(timetable[i]);
                    if(cost > tempCost){//replace current data with new highest for lecture
                        List<Integer> t = new ArrayList<Integer>();
                        costs.put(timetable[i],cost);

                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                    else if(cost == tempCost){//store location of equal lecture
                        List<Integer> t = costsLocations.get(timetable[i]);
                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                }

            }

            roomIndex++;
            if(roomIndex >= reader.numRooms){
                roomIndex = 0;
                period++;
            }
            if(period >= reader.periodsPerDay){
                period = 0;
                day++;
            }
        }

        //sort the map in descending order
        List<Map.Entry<String,Integer>> list = new ArrayList<>(costs.entrySet());
        list.sort(Map.Entry.<String, Integer>comparingByValue().reversed());


        List<Integer> result = new ArrayList<Integer>();
        int counter = 0;

        for(Map.Entry<String, Integer> entry : list){
            List<Integer> temp = costsLocations.get(entry.getKey());

            for(int val : temp){
                result.add(val);
                counter++;

                if(counter >= numComponentsInvolved)
                    break;
            }

            if(counter >= numComponentsInvolved)
                break;
            
        }

        return result;
    }

    /**
     * Cost defined in terms of room capacity constraint
     * @param numComponentsInvolved
     * @param timetable
     * @return
     */
    public List<Integer> findLowestCostRoom(int numComponentsInvolved, String[] timetable){
        Map<String, Integer> costs = new HashMap<String, Integer>();
        Map<String, List<Integer>> costsLocations = new HashMap<String, List<Integer>>();

        int day = 0,period = 0, roomIndex = 0;

        for(int i=0; i<timetable.length; i++){
            if(timetable[i] != null){
                int cost = roomCapacityConstraintCost(timetable[i], roomIndex);
                if(!costs.containsKey(timetable[i])){
                    List<Integer> temp = new ArrayList<Integer>();
                    costs.put(timetable[i],cost);

                    temp = new ArrayList<Integer>();
                    temp.add(i);
                    costsLocations.put(timetable[i], temp);
                }
                else{
                    int tempCost = costs.get(timetable[i]);
                    if(cost < tempCost){//replace current data with new lowest for room
                        List<Integer> t = new ArrayList<Integer>();
                        costs.put(timetable[i],cost);

                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                    else if(cost == tempCost){//store location of equal lecture
                        List<Integer> t = costsLocations.get(timetable[i]);
                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                }
            }

            roomIndex++;
            if(roomIndex >= reader.numRooms){
                roomIndex = 0;
                period++;
            }
            if(period >= reader.periodsPerDay){
                period = 0;
                day++;
            }
        }

        //sort the map in ascending order
        List<Map.Entry<String,Integer>> list = new ArrayList<>(costs.entrySet());
        list.sort(Map.Entry.comparingByValue());//sort the list based on values in ascending order

        List<Integer> result = new ArrayList<Integer>();
        int counter = 0;

        for(Map.Entry<String, Integer> entry : list){
            List<Integer> temp = costsLocations.get(entry.getKey());

            for(int val : temp){
                result.add(val);
                counter++;

                if(counter >= numComponentsInvolved)
                    break;
            }

            if(counter >= numComponentsInvolved)
                break;
            
        }

        return result;
    }

    public List<Integer> findHighestCostRoom(int numComponentsInvolved, String[] timetable){
        Map<String, Integer> costs = new HashMap<String, Integer>();
        Map<String, List<Integer>> costsLocations = new HashMap<String, List<Integer>>();

        int day = 0,period = 0, roomIndex = 0;

        for(int i=0; i<timetable.length; i++){
            if(timetable[i] != null){
                int cost = roomCapacityConstraintCost(timetable[i], roomIndex);
                if(!costs.containsKey(timetable[i])){
                    List<Integer> temp = new ArrayList<Integer>();
                    costs.put(timetable[i],cost);

                    temp = new ArrayList<Integer>();
                    temp.add(i);
                    costsLocations.put(timetable[i], temp);
                }
                else{
                    int tempCost = costs.get(timetable[i]);
                    if(cost > tempCost){//replace current data with new highest for room
                        List<Integer> t = new ArrayList<Integer>();
                        costs.put(timetable[i],cost);

                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                    else if(cost == tempCost){//store location of equal lecture
                        List<Integer> t = costsLocations.get(timetable[i]);
                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                }
            }

            roomIndex++;
            if(roomIndex >= reader.numRooms){
                roomIndex = 0;
                period++;
            }
            if(period >= reader.periodsPerDay){
                period = 0;
                day++;
            }
        }

        //sort the map in descending order
        List<Map.Entry<String,Integer>> list = new ArrayList<>(costs.entrySet());
        list.sort(Map.Entry.<String, Integer>comparingByValue().reversed());


        List<Integer> result = new ArrayList<Integer>();
        int counter = 0;

        for(Map.Entry<String, Integer> entry : list){
            List<Integer> temp = costsLocations.get(entry.getKey());

            for(int val : temp){
                result.add(val);
                counter++;

                if(counter >= numComponentsInvolved)
                    break;
            }

            if(counter >= numComponentsInvolved)
                break;
            
        }

        return result;
    }

    /**
     * cost defined in terms of room capacity costraint cost
     * @param numComponentsInvolved
     * @param timetable
     * @return
     */
    public List<Integer> findLowestCostPeriod(int numComponentsInvolved, String[] timetable){
        Map<String, Integer> costs = new HashMap<String, Integer>();
        Map<String, List<Integer>> costsLocations = new HashMap<String, List<Integer>>();

        int day = 0,period = 0, roomIndex = 0;

        for(int i=0; i<timetable.length; i++){
            if(timetable[i] != null){
                int cost = roomCapacityConstraintCost(timetable[i], roomIndex);
                if(!costs.containsKey(timetable[i])){
                    List<Integer> temp = new ArrayList<Integer>();
                    costs.put(timetable[i],cost);

                    temp = new ArrayList<Integer>();
                    temp.add(i);
                    costsLocations.put(timetable[i], temp);
                }
                else{
                    int tempCost = costs.get(timetable[i]);
                    if(cost < tempCost){//replace current data with new lowest for lecture
                        List<Integer> t = new ArrayList<Integer>();
                        costs.put(timetable[i],cost);

                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                    else if(cost == tempCost){//store location of equal lecture
                        List<Integer> t = costsLocations.get(timetable[i]);
                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                }
            }

            roomIndex++;
            if(roomIndex >= reader.numRooms){
                roomIndex = 0;
                period++;
            }
            if(period >= reader.periodsPerDay){
                period = 0;
                day++;
            }
        }

        //sort the map in ascending order
        List<Map.Entry<String,Integer>> list = new ArrayList<>(costs.entrySet());
        list.sort(Map.Entry.comparingByValue());//sort the list based on values in ascending order

        List<Integer> result = new ArrayList<Integer>();
        int counter = 0;

        for(Map.Entry<String, Integer> entry : list){
            List<Integer> temp = costsLocations.get(entry.getKey());

            for(int val : temp){
                result.add(val);
                counter++;

                if(counter >= numComponentsInvolved)
                    break;
            }

            if(counter >= numComponentsInvolved)
                break;
            
        }

        return result;
    }

    public List<Integer> findHighestCostPeriod(int numComponentsInvolved, String[] timetable){
        Map<String, Integer> costs = new HashMap<String, Integer>();
        Map<String, List<Integer>> costsLocations = new HashMap<String, List<Integer>>();

        int day = 0,period = 0, roomIndex = 0;

        for(int i=0; i<timetable.length; i++){
            if(timetable[i] != null){
                int cost = roomCapacityConstraintCost(timetable[i], roomIndex);
                if(!costs.containsKey(timetable[i])){
                    List<Integer> temp = new ArrayList<Integer>();
                    costs.put(timetable[i],cost);

                    temp = new ArrayList<Integer>();
                    temp.add(i);
                    costsLocations.put(timetable[i], temp);
                }
                else{
                    int tempCost = costs.get(timetable[i]);
                    if(cost > tempCost){//replace current data with new lowest for lecture
                        List<Integer> t = new ArrayList<Integer>();
                        costs.put(timetable[i],cost);

                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                    else if(cost == tempCost){//store location of equal lecture
                        List<Integer> t = costsLocations.get(timetable[i]);
                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                }
            }

            roomIndex++;
            if(roomIndex >= reader.numRooms){
                roomIndex = 0;
                period++;
            }
            if(period >= reader.periodsPerDay){
                period = 0;
                day++;
            }
        }

        //sort the map in descending order
        List<Map.Entry<String,Integer>> list = new ArrayList<>(costs.entrySet());
        list.sort(Map.Entry.<String, Integer>comparingByValue().reversed());


        List<Integer> result = new ArrayList<Integer>();
        int counter = 0;

        for(Map.Entry<String, Integer> entry : list){
            List<Integer> temp = costsLocations.get(entry.getKey());

            for(int val : temp){
                result.add(val);
                counter++;

                if(counter >= numComponentsInvolved)
                    break;
            }

            if(counter >= numComponentsInvolved)
                break;
            
        }

        return result;
    }

    /**
     * Cost defined as the number of students taking the lecture
     */
    public List<Integer> findLowestSizeLecture(int numComponentsInvolved, String[] timetable){
        Map<String, Integer> costs = new HashMap<String, Integer>();
        Map<String, List<Integer>> costsLocations = new HashMap<String, List<Integer>>();
        int day = 0,period = 0, roomIndex = 0;

        for(int i=0; i<timetable.length;i++){
            if(timetable[i] != null){
                Course c = reader.coursesMap.get(timetable[i]);
                int cost = c.numStudentsEnrolled;

                if(!costs.containsKey(timetable[i])){
                    List<Integer> temp = new ArrayList<Integer>();
                    costs.put(timetable[i],cost);

                    temp = new ArrayList<Integer>();
                    temp.add(i);
                    costsLocations.put(timetable[i], temp);
                }
                else{
                    int tempCost = costs.get(timetable[i]);
                    if(cost < tempCost){//replace current data with new lowest for lecture
                        List<Integer> t = new ArrayList<Integer>();
                        costs.put(timetable[i],cost);

                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                    else if(cost == tempCost){//store location of equal lecture
                        List<Integer> t = costsLocations.get(timetable[i]);
                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                }
            }

            roomIndex++;
            if(roomIndex >= reader.numRooms){
                roomIndex = 0;
                period++;
            }
            if(period >= reader.periodsPerDay){
                period = 0;
                day++;
            }
        }

        //sort the map in ascending order
        List<Map.Entry<String,Integer>> list = new ArrayList<>(costs.entrySet());
        list.sort(Map.Entry.comparingByValue());//sort the list based on values in ascending order

        List<Integer> result = new ArrayList<Integer>();
        int counter = 0;

        for(Map.Entry<String, Integer> entry : list){
            List<Integer> temp = costsLocations.get(entry.getKey());

            for(int val : temp){
                result.add(val);
                counter++;

                if(counter >= numComponentsInvolved)
                    break;
            }

            if(counter >= numComponentsInvolved)
                break;
            
        }

        return result;
    }

    public List<Integer> findHighestSizeLecture(int numComponentsInvolved, String[] timetable){
        Map<String, Integer> costs = new HashMap<String, Integer>();
        Map<String, List<Integer>> costsLocations = new HashMap<String, List<Integer>>();
        int day = 0,period = 0, roomIndex = 0;

        for(int i=0; i<timetable.length;i++){
            if(timetable[i] != null){
                Course c = reader.coursesMap.get(timetable[i]);
                int cost = c.numStudentsEnrolled;

                if(!costs.containsKey(timetable[i])){
                    List<Integer> temp = new ArrayList<Integer>();
                    costs.put(timetable[i],cost);

                    temp = new ArrayList<Integer>();
                    temp.add(i);
                    costsLocations.put(timetable[i], temp);
                }
                else{
                    int tempCost = costs.get(timetable[i]);
                    if(cost > tempCost){//replace current data with new lowest for lecture
                        List<Integer> t = new ArrayList<Integer>();
                        costs.put(timetable[i],cost);

                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                    else if(cost == tempCost){//store location of equal lecture
                        List<Integer> t = costsLocations.get(timetable[i]);
                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                }
            }

            roomIndex++;
            if(roomIndex >= reader.numRooms){
                roomIndex = 0;
                period++;
            }
            if(period >= reader.periodsPerDay){
                period = 0;
                day++;
            }
        }

        //sort the map in descending order
        List<Map.Entry<String,Integer>> list = new ArrayList<>(costs.entrySet());
        list.sort(Map.Entry.<String, Integer>comparingByValue().reversed());


        List<Integer> result = new ArrayList<Integer>();
        int counter = 0;

        for(Map.Entry<String, Integer> entry : list){
            List<Integer> temp = costsLocations.get(entry.getKey());

            for(int val : temp){
                result.add(val);
                counter++;

                if(counter >= numComponentsInvolved)
                    break;
            }

            if(counter >= numComponentsInvolved)
                break;
            
        }

        return result;
    }

    /**
     * Cost defined in terms of the room capacity
     * @param numComponentsInvolved
     * @param timetable
     * @return
     */
    public List<Integer> findLowestSizeRoom(int numComponentsInvolved, String[] timetable){
        Map<String, Integer> costs = new HashMap<String, Integer>();
        Map<String, List<Integer>> costsLocations = new HashMap<String, List<Integer>>();
        int day = 0,period = 0, roomIndex = 0;

        for(int i=0; i< timetable.length; i++){
            if(timetable[i] != null){
                Room r = reader.rooms.get(roomIndex);
                int cost = r.capacity;

                if(!costs.containsKey(timetable[i])){
                    List<Integer> temp = new ArrayList<Integer>();
                    costs.put(timetable[i],cost);

                    temp = new ArrayList<Integer>();
                    temp.add(i);
                    costsLocations.put(timetable[i], temp);
                }
                else{
                    int tempCost = costs.get(timetable[i]);
                    if(cost < tempCost){//replace current data with new lowest for lecture
                        List<Integer> t = new ArrayList<Integer>();
                        costs.put(timetable[i],cost);

                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                    else if(cost == tempCost){//store location of equal lecture
                        List<Integer> t = costsLocations.get(timetable[i]);
                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                }
            }
            roomIndex++;
            if(roomIndex >= reader.numRooms){
                roomIndex = 0;
                period++;
            }
            if(period >= reader.periodsPerDay){
                period = 0;
                day++;
            }
        }

        //sort the map in ascending order
        List<Map.Entry<String,Integer>> list = new ArrayList<>(costs.entrySet());
        list.sort(Map.Entry.comparingByValue());//sort the list based on values in ascending order

        List<Integer> result = new ArrayList<Integer>();
        int counter = 0;

        for(Map.Entry<String, Integer> entry : list){
            List<Integer> temp = costsLocations.get(entry.getKey());

            for(int val : temp){
                result.add(val);
                counter++;

                if(counter >= numComponentsInvolved)
                    break;
            }

            if(counter >= numComponentsInvolved)
                break;
            
        }

        return result;
    }

    public List<Integer> findHighestSizeRoom(int numComponentsInvolved, String[] timetable){
        Map<String, Integer> costs = new HashMap<String, Integer>();
        Map<String, List<Integer>> costsLocations = new HashMap<String, List<Integer>>();
        int day = 0,period = 0, roomIndex = 0;

        for(int i=0; i< timetable.length; i++){
            if(timetable[i] != null){
                Room r = reader.rooms.get(roomIndex);
                int cost = r.capacity;

                if(!costs.containsKey(timetable[i])){
                    List<Integer> temp = new ArrayList<Integer>();
                    costs.put(timetable[i],cost);

                    temp = new ArrayList<Integer>();
                    temp.add(i);
                    costsLocations.put(timetable[i], temp);
                }
                else{
                    int tempCost = costs.get(timetable[i]);
                    if(cost > tempCost){//replace current data with new lowest for lecture
                        List<Integer> t = new ArrayList<Integer>();
                        costs.put(timetable[i],cost);

                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                    else if(cost == tempCost){//store location of equal lecture
                        List<Integer> t = costsLocations.get(timetable[i]);
                        t.add(i);
                        costsLocations.put(timetable[i], t);
                    }
                }
            }
            roomIndex++;
            if(roomIndex >= reader.numRooms){
                roomIndex = 0;
                period++;
            }
            if(period >= reader.periodsPerDay){
                period = 0;
                day++;
            }
        }

        //sort the map in descending order
        List<Map.Entry<String,Integer>> list = new ArrayList<>(costs.entrySet());
        list.sort(Map.Entry.<String, Integer>comparingByValue().reversed());


        List<Integer> result = new ArrayList<Integer>();
        int counter = 0;

        for(Map.Entry<String, Integer> entry : list){
            List<Integer> temp = costsLocations.get(entry.getKey());

            for(int val : temp){
                result.add(val);
                counter++;

                if(counter >= numComponentsInvolved)
                    break;
            }

            if(counter >= numComponentsInvolved)
                break;
            
        }

        return result;
    }

    /**
     * Defined as the number of lectures scheduled in the period
     * @param numComponentsInvolved
     * @param timetable
     * @return
     */
    public List<Integer> findLowestSizePeriod(int numComponentsInvolved, String[] timetable){
        Map<Integer, Integer> periodCosts = new HashMap<Integer, Integer>();
        Map<Integer, List<Integer>> periodLocations = new HashMap<Integer, List<Integer>>();

        int day = 0,period = 0, roomIndex = 0;

        for(int i=0; i<timetable.length;i++){
            if(timetable[i] != null){
                if(!periodCosts.containsKey(period)){
                    periodCosts.put(period,1);
                    List<Integer> temp = new ArrayList<Integer>();
                    temp.add(i);
                    periodLocations.put(period,temp);
                }
                else{
                    periodCosts.put(period, periodCosts.get(period) +1);
                    List<Integer> temp = periodLocations.get(period);
                    temp.add(i);
                    periodLocations.put(period,temp);
                }
            }

            roomIndex++;
            if(roomIndex >= reader.numRooms){
                roomIndex = 0;
                period++;
            }
            if(period >= reader.periodsPerDay){
                period = 0;
                day++;
            }
        }

        //sort the map in ascending order
        List<Map.Entry<Integer,Integer>> list = new ArrayList<>(periodCosts.entrySet());
        list.sort(Map.Entry.comparingByValue());

        List<Integer> result = new ArrayList<Integer>();
        int counter = 0;

        for(Map.Entry<Integer, Integer> entry : list){
            List<Integer> temp = periodLocations.get(entry.getKey());
            while(temp.isEmpty() == false && counter < numComponentsInvolved){
                int index = random.nextInt(temp.size());
                result.add(index);
                temp.remove(index);
                counter++;
            }

            if(counter >= numComponentsInvolved)
                break;
        }

        return result;

    }

    public List<Integer> findHighestSizePeriod(int numComponentsInvolved, String[] timetable){
        Map<Integer, Integer> periodCosts = new HashMap<Integer, Integer>();
        Map<Integer, List<Integer>> periodLocations = new HashMap<Integer, List<Integer>>();
        int day = 0,period = 0, roomIndex = 0;

        for(int i=0; i<timetable.length;i++){
            if(timetable[i] != null){
                if(!periodCosts.containsKey(period)){
                    periodCosts.put(period,1);
                    List<Integer> temp = new ArrayList<Integer>();
                    temp.add(i);
                    periodLocations.put(period,temp);
                }
                else{
                    periodCosts.put(period, periodCosts.get(period) +1);
                    List<Integer> temp = periodLocations.get(period);
                    temp.add(i);
                    periodLocations.put(period,temp);
                }
            }

            roomIndex++;
            if(roomIndex >= reader.numRooms){
                roomIndex = 0;
                period++;
            }
            if(period >= reader.periodsPerDay){
                period = 0;
                day++;
            }
        }

        //sort in descending order
        List<Map.Entry<Integer,Integer>> list = new ArrayList<>(periodCosts.entrySet());
        list.sort(Map.Entry.<Integer, Integer>comparingByValue().reversed());

        List<Integer> result = new ArrayList<Integer>();
        int counter = 0;

        for(Map.Entry<Integer, Integer> entry : list){
            List<Integer> temp = periodLocations.get(entry.getKey());
            while(temp.isEmpty() == false && counter < numComponentsInvolved){
                int index = random.nextInt(temp.size());
                result.add(index);
                temp.remove(index);
                counter++;
            }

            if(counter >= numComponentsInvolved)
                break;
        }

        return result;
    }

    /**
     * Randomly select a solution component
     */
    public List<Integer> findRandom(int nummComponentsInvolved, String[] timetable){
        List<Integer> result = new ArrayList<Integer>();

        while(result.size() < nummComponentsInvolved){
            int index = random.nextInt(timetable.length);
            if(timetable[index] != null)
                result.add(index);
        }

        return result;
    }
}
