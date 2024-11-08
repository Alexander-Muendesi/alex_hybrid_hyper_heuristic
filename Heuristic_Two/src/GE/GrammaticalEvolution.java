package GE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import constructor_classes.Solutions;
import constructor_classes.Timetable;
import data_classes.DataReader;

public class GrammaticalEvolution {
    private Map<Integer, Chromosome> population;
    private Random random;
    private int tournamentSize, populationSize, maxGenerations;
    private int maxCodons, minCodons;
    private double mutationRate, crossoverRate;
    private Timetable timetable;
    private final DataReader dataReader;
    public static int numIterations = 0;

    public GrammaticalEvolution(Random random, int maxCodons, int minCodons, int tournamentSize, int populationSize,
                                    double mutationRate, double crossoverRate, int maxGenerations, DataReader dataReader){
        this.population = new HashMap<Integer,Chromosome>();
        this.random = random;
        this.tournamentSize = tournamentSize;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;

        this.maxCodons = maxCodons;
        this.minCodons = minCodons;
        this.maxGenerations = maxGenerations;
        this.dataReader = dataReader;

        Solutions solution = new Solutions(dataReader, random);
        this.timetable = new Timetable(solution.generateSolution(), dataReader, random);
        this.timetable.calculateFitness();
    }

    public void execute(){
        numIterations = 0;
        int stopCounter = 0;
        int prevBest = -1;

        generateInitialPopulation();
        // int numIterations = 0;
        Chromosome bestIndividual = null;

        while(numIterations < maxGenerations && stopCounter < 50){
            // System.out.println("Num iterations: " + numIterations);

            if(bestIndividual != null)
                prevBest = bestIndividual.getFitness();

            bestIndividual = evaluateFitness();//evaluate the population
            
            numIterations++;
            generateNewPopulation();
            // bestIndividual.printFitness();

            if(bestIndividual.getFitness() == prevBest)
                stopCounter++;
            else
                stopCounter = 0;
        }
            
        // System.out.print("Instance Number: " + dataReader.filenumber + " ");
        // bestIndividual.printFitness();
        // System.out.println(bestIndividual.programRepresantation);
        // System.out.println(bestIndividual.timetable.deletedCourses.size());
    }

    /**
     * Finds the chromosome that resulted in the best fitness
     * @return Chromosome representing the best individual
     */
    public Chromosome evaluateFitness(){
        Chromosome best = this.population.get(0);
        best.evaluateIndividual();

        for(int i=0; i<this.population.size(); i++){
            // System.out.println("evaluating fitness function");
            Chromosome temp = this.population.get(i);
            temp.evaluateIndividual();

            // System.out.println("temp Fitness: " + temp.getFitness());
            if(temp.getFitness() < best.getFitness())
                best = temp;
        }
        return best;
    }

    /**
     * This method returns the 10 best heuristics from the population
     * @return List containing the 10 best heuristics
     */
    public List<Chromosome> getHeuristics(){
        List<Chromosome> best = new ArrayList<Chromosome>();

        List<Chromosome> pop = new ArrayList<Chromosome>();
        for(int i=0; i<population.size();i++){
            pop.add(population.get(i));
        }
        Collections.sort(pop, new Comparator<Chromosome>(){
            @Override
            public int compare(Chromosome one, Chromosome two){
                int fitnessOne = one.getFitness();
                int fitnessTwo = two.getFitness();
                
                return Integer.compare(fitnessOne, fitnessTwo);
            }
        });

        for(int i=0;i<10;i++)
            best.add(pop.get(i));

        return best;
    }

    /**
     * This method creates a new population using the generational approach.
     * @param currIteration Represents the current generation. Used for currIteration variable in Chromosome
     */
    public void generateNewPopulation(){
        Map<Integer, Chromosome> newPopulation = new HashMap<Integer,Chromosome>();
        int numCrossoverIndividuals = (int) (this.crossoverRate * this.populationSize);
        int numMutationIndividuals = this.populationSize - numCrossoverIndividuals;
        int counter = 0;

        while(newPopulation.size() != this.populationSize){
            if(numCrossoverIndividuals > 0 && numMutationIndividuals > 0){
                //randomly create an individual from mutation or crossover

                if(this.random.nextInt(2) == 0){//perform crossover
                    Chromosome parentOne = tournamentSelection();
                    Chromosome parentTwo = tournamentSelection();

                    // parentOne.printChromosome();
                    // parentTwo.printChromosome();

                    parentTwo = parentOne.singlePointCrossover(parentTwo);

                    // parentOne.printChromosome();
                    // parentTwo.printChromosome();

                    if(parentOne.partiallyEvaluateIndividual()){//make sure the derivation tree can be created without infinite recursion happening
                        newPopulation.put(counter++, parentOne);
                        numCrossoverIndividuals--;
                    }
                    
                    if(parentTwo.partiallyEvaluateIndividual() && newPopulation.size() + 1 < this.populationSize){//make sure the derivation tree can be created without infinite recursion happening
                        newPopulation.put(counter++, parentTwo);
                        numCrossoverIndividuals--;
                    }

                }
                else{//perform mutation
                    Chromosome parentOne = tournamentSelection();
                    parentOne.mutate();

                    if(parentOne.partiallyEvaluateIndividual()){//make sure the derivation tree can be created without infinite recursion happening
                        newPopulation.put(counter++,parentOne);
                        numMutationIndividuals--;
                    }

                }
            }
            else if(numCrossoverIndividuals > 0){
                Chromosome parentOne = tournamentSelection();
                Chromosome parentTwo = tournamentSelection();

                parentTwo = parentOne.singlePointCrossover(parentTwo);
                if(parentOne.partiallyEvaluateIndividual()){
                    newPopulation.put(counter++, parentOne);
                    numCrossoverIndividuals--;
                }

                if(parentTwo.partiallyEvaluateIndividual() && newPopulation.size() + 1 < this.populationSize){
                    newPopulation.put(counter++, parentTwo);
                    numCrossoverIndividuals--;
                }
            }
            else if(numMutationIndividuals > 0){
                Chromosome parentOne = tournamentSelection();
                parentOne.mutate();

                if(parentOne.partiallyEvaluateIndividual()){
                    newPopulation.put(counter++,parentOne);
                    numMutationIndividuals--;
                }
            }
        }

        this.population = newPopulation;
    }

    /**
     * Generates random size codons
     */
    private void generateInitialPopulation(){
        int codonRange = this.maxCodons - minCodons + 1;
        // System.out.println(maxCodons + " " + minCodons + " " + codonRange);
        int codonsPerIndividual = random.nextInt(codonRange) + this.minCodons;
        

        for(int i = 0; i < populationSize; i++ ){
            // System.out.println("population size: " + i + " " + "numCodons: " + codonsPerIndividual);
            Chromosome c = new Chromosome(this.maxCodons, this.minCodons, this.random, true, codonsPerIndividual, timetable.copy());
            c.totalIterations = this.maxGenerations;
            c.prevFitness = -1;
            c.currFitness = this.timetable.fitness;

            boolean r = c.partiallyEvaluateIndividual();
            if(r){
                this.population.put(i, c);
            }
            else
                --i;
            codonsPerIndividual = random.nextInt(codonRange) + this.minCodons;
        }
    }

    /**
     * Creates a tournament of size tournament_size and returns the individual with the best fitness
     * @return Copy of the fittest individual
     */
    public Chromosome tournamentSelection(){
        List<Chromosome> tournament = new ArrayList<Chromosome>();
        Chromosome bestIndividual = null;

        while(tournament.size() < this.tournamentSize){
            Chromosome temp = population.get(random.nextInt(population.size()));
            
            if(tournament.isEmpty()){
                tournament.add(temp);
                bestIndividual = temp;
            }
            else if(tournament.contains(temp) == false){
                tournament.add(temp);
                // bestIndividual = (temp.getFitness() < bestIndividual.getFitness()) ? temp : bestIndividual;

                if(random.nextInt(2) == 0){//try and balance between favouring lower hard constraint and lower soft constraints
                    if(temp.fitness[0] < bestIndividual.fitness[0] && temp.fitness[1] < bestIndividual.fitness[1])
                        bestIndividual = temp;
                    else if(temp.fitness[0] < bestIndividual.fitness[0])
                        bestIndividual = temp;
                }
                else{
                    if(temp.fitness[0] + temp.fitness[1] < bestIndividual.fitness[0] + bestIndividual.fitness[1])
                        bestIndividual = temp;
                }

                
                
            }
        }

        return bestIndividual.copy();
    }
}
