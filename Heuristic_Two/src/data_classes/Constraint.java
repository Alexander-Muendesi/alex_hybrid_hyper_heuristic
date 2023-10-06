package data_classes;

public class Constraint{
    public String courseId;
    public int day;
    public int dayPeriod;

    /**
     * 
     * @param courseId
     * @param day
     * @param dayPeriod
     */
    public Constraint(String courseId, int day, int dayPeriod){
        this.courseId = courseId;
        this.day = day;
        this.dayPeriod = dayPeriod;
    }

    @Override
    public String toString(){
        return courseId + " " + day + " " + dayPeriod;
    }
}
