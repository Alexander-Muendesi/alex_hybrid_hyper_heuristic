package data_classes;

import java.util.List;

public class Curriculum{
    public String curriculumId;
    public int numCourses;
    public List<String> courses;

    /**
     * 
     * @param curriculumId
     * @param numCourses
     * @param courses
     */
    public Curriculum(String curriculumId, int numCourses, List<String> courses){
        this.curriculumId = curriculumId;
        this.numCourses = numCourses;
        this.courses = courses;
    }

    @Override
    public String toString(){
        String result = curriculumId + " "+ numCourses + " ";
        
        for(String val: courses)
            result += val + " ";

        return result;
    }
}
