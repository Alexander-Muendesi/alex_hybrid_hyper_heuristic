import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import GE.Chromosome;
import constructor_classes.Solutions;
import constructor_classes.Timetable;
import data_classes.DataReader;

public class Perturbator {
    private final Random random;
    private final DataReader reader;
    private List<Chromosome> heuristics;
    private int pTournamentSize = 4;
    public double thresholdValue, thresholdAdaptationFactor;
    private int iterationLimit;

    /**
     * 
     * @param random
     * @param reader
     * @param pTournamentSize Tournament size used for single point selection
     * @param thresholdValue
     * @param thresholdAdaptationFactor
     * @param iterationLimit
     * @param heuristics
     */
    public Perturbator(Random random, DataReader reader, int pTournamentSize, double thresholdValue, double thresholdAdaptationFactor, int iterationLimit,
                                List<Chromosome> heuristics){
        this.random = random;
        this.reader =  reader;
        this.pTournamentSize = pTournamentSize;
        this.thresholdValue = thresholdValue;
        this.thresholdAdaptationFactor = thresholdAdaptationFactor;
        this.iterationLimit = iterationLimit;
    }

    public void execute(){
        //create an initial solution
        Solutions solution = new Solutions(reader, random);
        Timetable timetable = new Timetable(solution.generateSolution(), reader, random);
        Timetable copyTimetable =  new Timetable(timetable.getTimetable(), reader, random);

        int numIterations = 0;
        int[] bestFitness = timetable.calculateFitness();
        int[] result = {};

        while(numIterations <= 150000){
            copyTimetable = tournamentSelection(timetable);//heuristic selection and execution
            int[] currentFitness = copyTimetable.calculateFitness();

            if(currentFitness[0] <= bestFitness[0] && currentFitness[1] <= bestFitness[1]){
                timetable.setTimetable(copyTimetable.getTimetable());
                bestFitness = currentFitness;
            }
            //new code is below
            else if(numIterations > iterationLimit && currentFitness[0]+currentFitness[1] < thresholdValue * bestFitness[0]+bestFitness[1]){//accept move based on threshold criteria
                timetable.setTimetable(copyTimetable.getTimetable());
                bestFitness = currentFitness;
            }

            if(currentFitness[0]+currentFitness[1] > bestFitness[0]+bestFitness[1]){//adapt the threshold if there is no improvement in fitness
                thresholdValue *= thresholdAdaptationFactor;
            }

            numIterations++;
            result = bestFitness;
        }
        
        System.out.println("fileNumber: "+reader.filenumber + " result: " + result[0] + " " + result[1]);
    }

    public Timetable tournamentSelection(Timetable t){
        String[] timetable = t.getTimetable();
        List<Chromosome> population =  new ArrayList<Chromosome>();

        while(population.size() < pTournamentSize){
            Chromosome temp = heuristics.get(random.nextInt(heuristics.size()));
            if(!population.contains(temp)){
                population.add(temp);
                temp.timetable = t;
            }
        }

        Timetable[] timetables = new Timetable[population.size()];
        int counter = 0;
        int bestFitness = Integer.MAX_VALUE;
        int[] tempFitness = {};
        int bestIndex = 0;

        for(Timetable t: timetables){
            t = new Timetable(timetable, reader,random);
            timetables[counter] = t;
            population.get(counter).evaluateIndividual();
            t.setTimetable(population.get(counter).timetable.getTimetable());

            tempFitness = t.calculateFitness();
            if(tempFitness[0] + tempFitness[1] < bestFitness){
                bestIndex = counter;
                bestFitness = tempFitness[0]+tempFitness[1];
            }
            counter++;

        }

        return timetables[bestIndex];
    }
}
