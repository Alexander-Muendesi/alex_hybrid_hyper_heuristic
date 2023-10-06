package data_classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataReader {
    public int numCourses,  numRooms,  numDays,  periodsPerDay,  numCurricula,  numConstraints;
    public List<Course> courses;
    public Map<String,Course> coursesMap;
    public List<Room> rooms;
    public List<Curriculum> curricula;
    public List<Constraint> constraints;
    public String filename = "Heuristic_Two/data/comp";
    public int filenumber = 0;

    /**
     * 
     * @param fileNumber
     */
    public DataReader(int fileNumber){
        this.filenumber = fileNumber;
        if(fileNumber < 10)
            filename += "0"+ String.valueOf(fileNumber) + ".ctt.txt";
        else
            filename += String.valueOf(fileNumber) + ".ctt.txt";

        this.courses = new ArrayList<Course>();
        this.rooms = new ArrayList<Room>();
        this.curricula = new ArrayList<Curriculum>();
        this.constraints = new ArrayList<Constraint>();
        this.coursesMap = new HashMap<String, Course>();

        readFile();
    }

    public void readFile(){
        Boolean headerData = false;
        Boolean courses = false;
        Boolean rooms = false;
        Boolean curricula = false;
        Boolean constraints = false;

        try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line = "";

            while((line = reader.readLine()) != null){
                ArrayList<String> data = new ArrayList<String>();

                if(line.equals(""))//skip blank lines
                    continue;

                int startIndex = 0;
                int endIndex = line.indexOf(' ');

                while(endIndex >= 0){
                    data.add(line.substring(startIndex, endIndex));
                    startIndex = endIndex + 1;
                    endIndex = line.indexOf(' ', startIndex);
                }
                data.add(line.substring(startIndex));

                if(data.get(0).equals("Courses:"))
                    this.numCourses = Integer.parseInt(data.get(1));
                else if(data.get(0).equals("Rooms:"))
                    this.numRooms = Integer.parseInt(data.get(1));
                else if(data.get(0).equals("Days:"))
                    this.numDays = Integer.parseInt(data.get(1));
                else if(data.get(0).equals("Periods_per_day:"))
                    this.periodsPerDay = Integer.parseInt(data.get(1));
                else if(data.get(0).equals("Curricula:"))
                    this.numCurricula = Integer.parseInt(data.get(1));
                else if(data.get(0).equals("Constraints:")){
                    this.numConstraints = Integer.parseInt(data.get(1));
                    headerData = true;
                }
                else if(headerData){
                    if(data.get(0).equals("COURSES:")){
                        courses = true;
                    }
                    else if(data.get(0).equals("ROOMS:")){
                        rooms = true;
                        courses = false;
                    }
                    else if(data.get(0).equals("CURRICULA:")){
                        curricula = true;
                        rooms = false;
                    }
                    else if(data.get(0).equals("UNAVAILABILITY_CONSTRAINTS:")){
                        constraints = true;
                        curricula = false;
                    }
                    else if(data.get(0).equals("END.")){
                        constraints = false;
                    }
                    else if(courses){
                        this.courses.add(new Course(data.get(0), data.get(1), Integer.parseInt(data.get(2)), Integer.parseInt(data.get(3)), Integer.parseInt(data.get(4))));
                        this.coursesMap.put(data.get(0), this.courses.get(this.courses.size()-1));
                    }
                    else if(rooms)
                        this.rooms.add(new Room(data.get(0), Integer.parseInt(data.get(1))));
                    else if(curricula){
                            List<String> temp = new ArrayList<String>();
                            int counter = 0;
                            int index = 0;

                            for(String val: data){
                                if(counter > 1)
                                    temp.add(data.get(counter));
                                counter++;
                            }
                            this.curricula.add(new Curriculum(data.get(0), Integer.parseInt(data.get(1)), temp));
                    }
                    else if(constraints)
                        this.constraints.add(new Constraint(data.get(0), Integer.parseInt(data.get(1)), Integer.parseInt(data.get(2))));

                }
            }
            //free I/O
            reader.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @return numCourses,  numRooms,  numDays,  periodsPerDay,  numCurricula,  numConstraints
     */
    public int[] getHeaderData(){
        int result[] = {numCourses,  numRooms,  numDays,  periodsPerDay,  numCurricula,  numConstraints};
        return result;
    }   

}
