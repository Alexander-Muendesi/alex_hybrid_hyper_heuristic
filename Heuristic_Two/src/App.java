import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import GE.Chromosome;
import GE.GrammaticalEvolution;
import constructor_classes.Solutions;
import constructor_classes.Timetable;
import data_classes.DataReader;

public class App {

    public static long seed = 0;
    public static int numThreads = 8;
    public static void main(String[] args) throws Exception {
        // Scanner scanner = new Scanner(System.in);
        // String message = "If this first time running program, just type \'y\' \n";
        // message += "if program fails to run for some reason enter a number of threads in the range [4,8] with no spaces after number. The more the better\n";
        // message += "Program is set to 8 threads initially which could potentially cause memory issues for less powerful machines";

        // System.out.println(message);

        // String input = scanner.nextLine();
        // if(input.equals("y") == false){
        //     numThreads = Integer.parseInt(input);
        // }

        // executeRun();
        // scanner.close();

        // Random random = new Random(seed);
        // DataReader dataReader = new DataReader(1);
        // int tournamentSize = 5;
        // int minCodons = 8;
        // int maxCodons = 24;
        // int populationSize = 141;
        // double crossoverRate = 0.070625;
        // double mutationRate = 1.0 - crossoverRate;
        // double thresholdValue = 0.5585859375, thresholdAdaptationFactor = 0.8367421875;
        // int pTournamentSize = 3;

        // GrammaticalEvolution ge = new GrammaticalEvolution(random, maxCodons, minCodons, tournamentSize, populationSize, mutationRate, crossoverRate, 2000, dataReader);
        // ge.execute();

        // List<Chromosome> heuristics = ge.getHeuristics();

        // Perturbator perturbator = new Perturbator(random, dataReader, pTournamentSize, thresholdValue, thresholdAdaptationFactor, pTournamentSize, heuristics);
        // perturbator.execute();
        while(true)
            executeRun();

    }

    public static void executeRun(){
        seed = System.currentTimeMillis();
        System.out.println("Seed: " + seed);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        List<Future<?>> fs = new ArrayList<>();
        
        fs.add(executorService.submit(()->execute1()));
        fs.add(executorService.submit(()->execute3()));
        fs.add(executorService.submit(()->execute4()));
        fs.add(executorService.submit(()->execute11()));
        fs.add(executorService.submit(()->execute13()));
        fs.add(executorService.submit(()->execute14()));
        fs.add(executorService.submit(()->execute15()));
        fs.add(executorService.submit(()->execute18()));

        try{
            for(Future<?> f: fs)
                f.get();
            fs.clear();
            executorService.shutdown();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //best values  = count8: 5.4375 8.5 24.25 141.25 0.070625
    public static void parameterTuning(){
        SobolReader s = new SobolReader();
        Double []params = null;
        s.getParams();//skip the line with 0's
        int count = 0;
        while((params = s.getParams()) != null){
            if(count <= 246){
                System.out.println("Count: " + count);
    
                for(double d: params)
                    System.out.print(d + " ");
                System.out.println();
    
                Random random = new Random(2154645);
                DataReader dataReader = new DataReader(1);
                int tournamentSize = params[0].intValue();
                int minCodons = params[1].intValue();
                int maxCodons = params[2].intValue();
                int populationSize = params[3].intValue();
                double crossoverRate = params[4];
                double mutationRate = 1.0 - crossoverRate;
    
                GrammaticalEvolution ge = new GrammaticalEvolution(random, maxCodons, minCodons, tournamentSize, populationSize, mutationRate, crossoverRate, 2000, dataReader);
                ge.execute();
    
                System.gc();
            }
            count++;
        }
    }

    public static void execute1(){
        Random random = new Random(seed);
        DataReader dataReader = new DataReader(1);
        int tournamentSize = 5;
        int minCodons = 8;
        int maxCodons = 24;
        int populationSize = 141;
        double crossoverRate = 0.070625;
        double mutationRate = 1.0 - crossoverRate;
        double thresholdValue = 0.5585859375, thresholdAdaptationFactor = 0.8367421875;
        int pTournamentSize = 3;

        GrammaticalEvolution ge = new GrammaticalEvolution(random, maxCodons, minCodons, tournamentSize, populationSize, mutationRate, crossoverRate, 2000, dataReader);
        ge.execute();

        List<Chromosome> heuristics = ge.getHeuristics();

        Perturbator perturbator = new Perturbator(random, dataReader, pTournamentSize, thresholdValue, thresholdAdaptationFactor, pTournamentSize, heuristics);
        perturbator.execute();
    }

    public static void execute3(){
        Random random = new Random(seed);
        DataReader dataReader = new DataReader(3);
        int tournamentSize = 5;
        int minCodons = 8;
        int maxCodons = 24;
        int populationSize = 141;
        double crossoverRate = 0.070625;
        double mutationRate = 1.0 - crossoverRate;
        double thresholdValue = 0.5585859375, thresholdAdaptationFactor = 0.8367421875;
        int pTournamentSize = 3;

        GrammaticalEvolution ge = new GrammaticalEvolution(random, maxCodons, minCodons, tournamentSize, populationSize, mutationRate, crossoverRate, 2000, dataReader);
        ge.execute();

        List<Chromosome> heuristics = ge.getHeuristics();

        Perturbator perturbator = new Perturbator(random, dataReader, pTournamentSize, thresholdValue, thresholdAdaptationFactor, pTournamentSize, heuristics);
        perturbator.execute();
    }

    public static void execute4(){
        Random random = new Random(seed);
        DataReader dataReader = new DataReader(4);
        int tournamentSize = 5;
        int minCodons = 8;
        int maxCodons = 24;
        int populationSize = 141;
        double crossoverRate = 0.070625;
        double mutationRate = 1.0 - crossoverRate;
        double thresholdValue = 0.5585859375, thresholdAdaptationFactor = 0.8367421875;
        int pTournamentSize = 3;

        GrammaticalEvolution ge = new GrammaticalEvolution(random, maxCodons, minCodons, tournamentSize, populationSize, mutationRate, crossoverRate, 2000, dataReader);
        ge.execute();

        List<Chromosome> heuristics = ge.getHeuristics();

        Perturbator perturbator = new Perturbator(random, dataReader, pTournamentSize, thresholdValue, thresholdAdaptationFactor, pTournamentSize, heuristics);
        perturbator.execute();
    }

    public static void execute11(){
        Random random = new Random(seed);
        DataReader dataReader = new DataReader(11);
        int tournamentSize = 5;
        int minCodons = 8;
        int maxCodons = 24;
        int populationSize = 141;
        double crossoverRate = 0.070625;
        double mutationRate = 1.0 - crossoverRate;
        double thresholdValue = 0.5585859375, thresholdAdaptationFactor = 0.8367421875;
        int pTournamentSize = 3;

        GrammaticalEvolution ge = new GrammaticalEvolution(random, maxCodons, minCodons, tournamentSize, populationSize, mutationRate, crossoverRate, 2000, dataReader);
        ge.execute();

        List<Chromosome> heuristics = ge.getHeuristics();

        Perturbator perturbator = new Perturbator(random, dataReader, pTournamentSize, thresholdValue, thresholdAdaptationFactor, pTournamentSize, heuristics);
        perturbator.execute();
    }

    public static void execute13(){
        Random random = new Random(seed);
        DataReader dataReader = new DataReader(13);
        int tournamentSize = 5;
        int minCodons = 8;
        int maxCodons = 24;
        int populationSize = 141;
        double crossoverRate = 0.070625;
        double mutationRate = 1.0 - crossoverRate;
        double thresholdValue = 0.5585859375, thresholdAdaptationFactor = 0.8367421875;
        int pTournamentSize = 3;

        GrammaticalEvolution ge = new GrammaticalEvolution(random, maxCodons, minCodons, tournamentSize, populationSize, mutationRate, crossoverRate, 2000, dataReader);
        ge.execute();

        List<Chromosome> heuristics = ge.getHeuristics();

        Perturbator perturbator = new Perturbator(random, dataReader, pTournamentSize, thresholdValue, thresholdAdaptationFactor, pTournamentSize, heuristics);
        perturbator.execute();
    }

    public static void execute14(){
        Random random = new Random(seed);
        DataReader dataReader = new DataReader(14);
        int tournamentSize = 5;
        int minCodons = 8;
        int maxCodons = 24;
        int populationSize = 141;
        double crossoverRate = 0.070625;
        double mutationRate = 1.0 - crossoverRate;
        double thresholdValue = 0.5585859375, thresholdAdaptationFactor = 0.8367421875;
        int pTournamentSize = 3;

        GrammaticalEvolution ge = new GrammaticalEvolution(random, maxCodons, minCodons, tournamentSize, populationSize, mutationRate, crossoverRate, 2000, dataReader);
        ge.execute();

        List<Chromosome> heuristics = ge.getHeuristics();

        Perturbator perturbator = new Perturbator(random, dataReader, pTournamentSize, thresholdValue, thresholdAdaptationFactor, pTournamentSize, heuristics);
        perturbator.execute();
    }

    public static void execute15(){
        Random random = new Random(seed);
        DataReader dataReader = new DataReader(15);
        int tournamentSize = 5;
        int minCodons = 8;
        int maxCodons = 24;
        int populationSize = 141;
        double crossoverRate = 0.070625;
        double mutationRate = 1.0 - crossoverRate;
        double thresholdValue = 0.5585859375, thresholdAdaptationFactor = 0.8367421875;
        int pTournamentSize = 3;

        GrammaticalEvolution ge = new GrammaticalEvolution(random, maxCodons, minCodons, tournamentSize, populationSize, mutationRate, crossoverRate, 2000, dataReader);
        ge.execute();

        List<Chromosome> heuristics = ge.getHeuristics();

        Perturbator perturbator = new Perturbator(random, dataReader, pTournamentSize, thresholdValue, thresholdAdaptationFactor, pTournamentSize, heuristics);
        perturbator.execute();
    }

    public static void execute18(){
        Random random = new Random(seed);
        DataReader dataReader = new DataReader(18);
        int tournamentSize = 5;
        int minCodons = 8;
        int maxCodons = 24;
        int populationSize = 141;
        double crossoverRate = 0.070625;
        double mutationRate = 1.0 - crossoverRate;
        double thresholdValue = 0.5585859375, thresholdAdaptationFactor = 0.8367421875;
        int pTournamentSize = 3;

        GrammaticalEvolution ge = new GrammaticalEvolution(random, maxCodons, minCodons, tournamentSize, populationSize, mutationRate, crossoverRate, 2000, dataReader);
        ge.execute();

        List<Chromosome> heuristics = ge.getHeuristics();

        Perturbator perturbator = new Perturbator(random, dataReader, pTournamentSize, thresholdValue, thresholdAdaptationFactor, pTournamentSize, heuristics);
        perturbator.execute();
    }

    public static void test(){
        int maxCodons = 20, minCodons = 2 , numCodons = 15;
        Random random = new Random(0);
        boolean flag = true;

        DataReader datareader = new DataReader(1);
        datareader.readFile();

        Solutions solution = new Solutions(datareader, random);

        Timetable timetable = new Timetable(solution.generateSolution(), datareader, random);
        Chromosome c = new Chromosome(maxCodons, minCodons, random, flag, numCodons, timetable);
        c = new Chromosome(maxCodons, minCodons, random, flag, numCodons, timetable);
        c = new Chromosome(maxCodons, minCodons, random, flag, numCodons, timetable);
        c = new Chromosome(maxCodons, minCodons, random, flag, numCodons, timetable);
        c = new Chromosome(maxCodons, minCodons, random, flag, numCodons, timetable);
        c = new Chromosome(maxCodons, minCodons, random, flag, numCodons, timetable);
        c = new Chromosome(maxCodons, minCodons, random, flag, numCodons, timetable);
        c = new Chromosome(maxCodons, minCodons, random, flag, numCodons, timetable);
        c = new Chromosome(maxCodons, minCodons, random, flag, numCodons, timetable);
        c = new Chromosome(maxCodons, minCodons, random, flag, numCodons, timetable);
        c = new Chromosome(maxCodons, minCodons, random, flag, numCodons, timetable);
        c = new Chromosome(maxCodons, minCodons, random, flag, numCodons, timetable);
        c = new Chromosome(maxCodons, minCodons, random, flag, numCodons, timetable);

        if(c.partiallyEvaluateIndividual()){
            c.evaluateIndividual();
            c.printFitness();
        }
    }
}
