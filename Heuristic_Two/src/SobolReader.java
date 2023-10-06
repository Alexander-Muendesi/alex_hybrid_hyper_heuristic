import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * tournament size: [2,7]
 * min codons : [2, 10]
 * max codons : [12, 40]
 * population size : [10,300]
 * crossover rate : [0,1.0]
 */
public class SobolReader {
    private List<List<Double>> sobolNumbers;
    private String filename = "master_Sobol_numbers.txt";
    private double []minVals = {2,2,12,10,0.01};
    private double []maxVals = {7,10,40,150,0.98};
    private int index = 0;

    public SobolReader(){
        sobolNumbers = new ArrayList<List<Double>>();
        readFile();
    }

    private void readFile(){
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line = "";
            while((line = reader.readLine()) != null){
                if(line.equals(""))
                    break;

                int startIndex = 0;
                int endIndex = line.indexOf('\t');
                List<Double> data = new ArrayList<Double>();

                int count = 0;
                while(endIndex >= 0){
                    String elem = line.substring(startIndex,endIndex);
                    double val = Double.valueOf(elem);
                    
                    double scaledVal = minVals[count] + (maxVals[count]-minVals[count]) * val;//min-max normalization
                    
                    data.add(scaledVal);
                    count++;

                    startIndex = endIndex+1;
                    endIndex = line.indexOf('\t', startIndex);

                    if(count >=5)
                        break;
                }
                sobolNumbers.add(data);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public Double[] getParams(){
        if(index >= sobolNumbers.size())
            return null;

        List<Double> params = sobolNumbers.get(index++);
        return params.toArray(new Double[params.size()]);
    }
}
