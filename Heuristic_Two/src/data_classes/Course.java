package data_classes;

public class Course{
    public String courseId;
    public String teacherId;
    public int numLectures;
    public int minWorkingDays;
    public int numStudentsEnrolled;
    
    /**
     * 
     * @param courseId
     * @param teacherId
     * @param numLectures
     * @param minWorkingDays
     * @param numStudentsEnrolled
     */
    public Course(String courseId, String teacherId, int numLectures, int minWorkingDays, int numStudentsEnrolled){
        this.courseId  = courseId; 
        this.teacherId = teacherId; 
        this.numLectures = numLectures; 
        this.minWorkingDays = minWorkingDays;
        this.numStudentsEnrolled = numStudentsEnrolled;
    }

    @Override
    public String toString(){
        return courseId + " " + teacherId + " " + numLectures + " " + minWorkingDays + " " + numStudentsEnrolled;
    }
}
