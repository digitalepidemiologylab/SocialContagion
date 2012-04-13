package com.salathegroup.socialcontagion;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SimulationManager {

    public static void main(String[] args) throws IOException {
        SimulationManager sm = new SimulationManager();
        double rewire = Double.parseDouble(args[0]);

        sm.theClusterHeatmaps(rewire);
        // sm.predictedOutbreaks();
        // sm.predictionRatio();
        // sm.playground();
        // sm.fileReading("/Users/ellscampbell/Documents/SocialContagion/rgeXomega,10sims,p0/clusters");
        // sm.localHeatmaps();
        // sm.edgeRemoval();
        // sm.adoptionStatus();
        // sm.edgeRemoval();
        // sm.runOutbreaks_vs_VaccineCoverage_vs_Threshold();
        // sm.runOutbreaksVsVaccinationCoverage();
        // sm.runOutbreaksLikelihoodVsVaccinationCoverage();
    }

    public void playground() {
        for (int i = 0; i < 10; i++) {
            System.out.println(System.currentTimeMillis());
            Simulations sim = new Simulations();
            sim.run();
            System.out.println(System.currentTimeMillis());


        }

    }
    
    public void predictionRatio() {
        int numberOfSimulations = 100;
        SimulationSettings.getInstance().setOmega(0.01);
        SimulationSettings.getInstance().setRge(0.001);
        SimulationSettings.getInstance().setInfectionRate(0.20);

        for (int rewireCounter = 0; rewireCounter < 50; rewireCounter++) {
            double rewire = 0.0 + (0.02 * rewireCounter);
            SimulationSettings.getInstance().setRewiringProbability(rewire);

            for (int simCount = 0; simCount < numberOfSimulations; simCount++) {
                Simulations sim = new Simulations();
                sim.predictVsimulate();
            }
        }
    }

    public void predictedOutbreaks() {
        int numberOfSimulations = 100;
        SimulationSettings.getInstance().setOmega(0.01);
        SimulationSettings.getInstance().setRge(0.001);
        SimulationSettings.getInstance().setInfectionRate(0.30);

        for (int rewireCounter = 0; rewireCounter < 50; rewireCounter++) {
            double rewire = 0 + (0.02 * rewireCounter);
            SimulationSettings.getInstance().setRewiringProbability(rewire);

            for (int simCount = 0; simCount < numberOfSimulations; simCount++) {
                Simulations sim = new Simulations();
                sim.run();
            }
        }
    }

    public void adoptionStatus() {
        System.out.println("p(rewire) =" + SimulationSettings.getInstance().getRewiringProbability() + " // " + "OMEGA = 0.01  //  RGE = 0.001 ");
        System.out.println("Social Adopter" + "," + "mixedSocial Adopter" + "," + "General Adopter" + "mixedGeneral Adopter");

        int numberOfSimulations = 100;
        SimulationSettings.getInstance().setOmega(0.01);
        SimulationSettings.getInstance().setRge(0.001);

        for (int simCount = 0; simCount < numberOfSimulations; simCount++) {
            Simulations sim = new Simulations();
            sim.run();
        }
    }

    public void edgeRemoval() {
        int numberOfSimulations = 100;
        SimulationSettings.getInstance().setOmega(0.01);
        SimulationSettings.getInstance().setRge(0.001);
        System.out.println("p(rewire) =" + SimulationSettings.getInstance().getRewiringProbability() + " // " + "OMEGA = 0.01 // RGE = 0.001 ");
        System.out.println("Osize" + ", " + "Osize-BASIC" + ", " + "CC" + ", " + "CC-BASIC" );

        ArrayList<Double[]> outbreakSizes = new ArrayList<Double[]>();
        ArrayList<Integer[]> clusterCounts = new ArrayList<Integer[]>();
        for (int simCount = 0; simCount < numberOfSimulations; simCount++) {
            Double[] outbreakSize;
            Integer[] clusterCount;
            outbreakSize = new Double[2];
            clusterCount = new Integer[2];
            Simulations sim = new Simulations();
            sim.run();
            sim.removeVaccinated();
            sim.clusters();
            outbreakSize[0] = sim.getPredictedOutbreakSize();
            clusterCount[0] = sim.getClusterCount();

            sim.removeSocialEdges(Connection.BASIC);
            sim.clusters();
            outbreakSize[1] = sim.getPredictedOutbreakSize();
            clusterCount[1] = sim.getClusterCount();


            System.out.println(outbreakSize[0] + "," + outbreakSize[1]  + "," + clusterCount[0] + "," + clusterCount[1]);
            
            
            outbreakSizes.add(outbreakSize);
            clusterCounts.add(clusterCount);
        }
    }

    private void theClusterHeatmaps(double rewire) {

        SimulationSettings.getInstance().setRewiringProbability(rewire);

        int steps = 20;
        double omegaMax = 0.01;
        double omegaStart = 0.0001;
        double omegaSteps = Math.pow(omegaMax/omegaStart, (1.0/(steps-1))) ;
        double rgeMax = 0.01;
        double rgeStart = 0.0001;
        double rgeSteps = Math.pow(rgeMax/rgeStart, (1.0/(steps-1))) ;

        double outbreaks_heatmap[][] = new double[steps][steps];
        int clusters_heatmap[][] = new int[steps][steps];


        for (int omegaCounter = 0; omegaCounter < steps; omegaCounter++) {
            double omega = omegaStart * Math.pow(omegaSteps,omegaCounter);
            SimulationSettings.getInstance().setOmega(omega);
            for (int rgeCounter = 0; rgeCounter < steps; rgeCounter++) {
                double rge = rgeStart * Math.pow(rgeSteps,rgeCounter);
                SimulationSettings.getInstance().setRge(rge);
                System.out.println(omega + "," + rge);
                Simulations sim = new Simulations();
                sim.run();


                outbreaks_heatmap[omegaCounter][rgeCounter] = sim.getPredictedOutbreakSize();
                clusters_heatmap[omegaCounter][rgeCounter] = sim.getClusterCount();

            }
        }

        String outbreaksFilename = "outbreaks" + "," + String.format("%.2f", SimulationSettings.getInstance().getRewiringProbability()) + "," + System.currentTimeMillis();
        PrintWriter outOutbreaks = null;
        try {
            outOutbreaks = new PrintWriter(new java.io.FileWriter(outbreaksFilename));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        for (int i = 0; i < steps; i++) {
            for (int ii = 0; ii < steps; ii++) {
                if (ii == steps-1) {
                    outOutbreaks.println(outbreaks_heatmap[i][ii]);
                }
                else outOutbreaks.print(outbreaks_heatmap[i][ii] + ",");
            }
        }
        outOutbreaks.close();

        String clustersFilename = "clusters" + "," + String.format("%.2f", SimulationSettings.getInstance().getRewiringProbability()) + "," + System.currentTimeMillis();
        PrintWriter outClusters = null;
        try {
            outClusters = new PrintWriter(new java.io.FileWriter(clustersFilename));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        for (int i = 0; i < steps; i++) {
            for (int ii = 0; ii < steps; ii++) {
                if (ii == steps-1) {
                    outClusters.println(clusters_heatmap[i][ii]);
                }
                else outClusters.print(clusters_heatmap[i][ii] + ",");
            }
        }
        outClusters.close();

    }

    private void heatmaps() {
        int numberOfSimulations = 1;
        int steps = 20;
        ArrayList<double[][]> outbreaks = new ArrayList<double[][]>();
        ArrayList<int[][]> clusters = new ArrayList<int[][]>();

        for (int simCount = 0; simCount < numberOfSimulations; simCount++){
            double outbreaks_heatmap[][] = new double[10][10];
            int clusters_heatmap[][] = new int[10][10];
            System.out.println(simCount);

            double omegaMax = 0.01;
            double omegaStart = 0.0001;
            double omegaSteps = Math.pow(omegaMax/omegaStart, (1.0/(steps-1))) ;
            double rgeMax = 0.01;
            double rgeStart = 0.0001;
            double rgeSteps = Math.pow(rgeMax/rgeStart, (1.0/(steps-1))) ;

            for (int omegaCounter = 0; omegaCounter < steps; omegaCounter++) {
                double omega = omegaStart * Math.pow(omegaSteps,omegaCounter);
                SimulationSettings.getInstance().setOmega(omega);
                for (int rgeCounter = 0; rgeCounter < steps; rgeCounter++) {
                    double rge = rgeStart * Math.pow(rgeSteps,rgeCounter);
                    SimulationSettings.getInstance().setRge(rge);
                    System.out.println(System.currentTimeMillis());
                    Simulations sim = new Simulations();
                    sim.run();
                    System.out.println(System.currentTimeMillis());


                    outbreaks_heatmap[omegaCounter][rgeCounter] = sim.getPredictedOutbreakSize();
                    clusters_heatmap[omegaCounter][rgeCounter] = sim.getClusterCount();

                }
            }
            outbreaks.add(outbreaks_heatmap);
            clusters.add(clusters_heatmap);
        }

        for (int simulation = 0; simulation < numberOfSimulations; simulation++) {
            String outbreaksFilename = "outbreaks" + String.format("%3d",simulation) + "," + String.format("%.2f", SimulationSettings.getInstance().getRewiringProbability());
            PrintWriter out = null;
            try {
                out = new PrintWriter(new java.io.FileWriter(outbreaksFilename));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            for (int i = 0; i < 10; i++) {
                out.println(outbreaks.get(simulation)[i][0] + "," + outbreaks.get(simulation)[i][1] + "," + outbreaks.get(simulation)[i][2] + "," + outbreaks.get(simulation)[i][3] + "," + outbreaks.get(simulation)[i][4]+ "," + outbreaks.get(simulation)[i][5]+ "," + outbreaks.get(simulation)[i][6] + "," + outbreaks.get(simulation)[i][7] + "," + outbreaks.get(simulation)[i][8] + "," + outbreaks.get(simulation)[i][9]) ;
            }
            out.close();
        }

        for (int simulation = 0; simulation < numberOfSimulations; simulation++) {
            String clustersFilename = "clusters" + String.format("%3d",simulation) + "," + String.format("%.2f", SimulationSettings.getInstance().getRewiringProbability());
            PrintWriter out = null;
            try {
                out = new PrintWriter(new java.io.FileWriter(clustersFilename));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            for (int i = 0; i < 10; i++) {
                out.println(clusters.get(simulation)[i][0] + "," + clusters.get(simulation)[i][1] + "," + clusters.get(simulation)[i][2] + "," + clusters.get(simulation)[i][3] + "," + clusters.get(simulation)[i][4] + "," + clusters.get(simulation)[i][5] + "," + clusters.get(simulation)[i][6] + "," + clusters.get(simulation)[i][7] + "," + clusters.get(simulation)[i][8] + "," + clusters.get(simulation)[i][9]) ;
            }
            out.close();
        }
    }



    private void runOutbreaks_vs_VaccineCoverage_vs_Threshold() {
        int numberOfSimulations = 500;
        SimulationSettings.getInstance().setRge(0.01);
        SimulationSettings.getInstance().setOmega(0.15);
        int minOutbreakSize = 10;

        for (int i = 1; i < 6; i++) {
            SimulationSettings.getInstance().setT(i);
            System.out.println(SimulationSettings.getInstance().getT());

            for (int ii = 0; ii < 10; ii++) {
                double vaccinationCoverage = 0.5 + (0.05*ii);
                SimulationSettings.getInstance().setMinimumLevelOfNegativeVaccinationOpinion(1.0 - vaccinationCoverage);
                int numberOfOutbreaks = 0;

                for (int iii = 0; iii < numberOfSimulations; iii++) {
                    Simulations sim = new Simulations();
                    sim.run();
                    if (sim.getOutbreakSize() >= minOutbreakSize) numberOfOutbreaks++;
                }
                System.out.println(vaccinationCoverage + "\t" + (double)numberOfOutbreaks/numberOfSimulations);
            }
        }
    }

    private void runOutbreaksVsVaccinationCoverage() {
        int numberOfSimulations = 500;
        SimulationSettings.getInstance().setRge(0.01);
        SimulationSettings.getInstance().setOmega(1.0);
        int minOutbreakSize = 10;
        for (int i = 0; i < 10; i++) {
            double vaccinationCoverage = 0.5 + (0.05*i);
            SimulationSettings.getInstance().setMinimumLevelOfNegativeVaccinationOpinion(1.0-vaccinationCoverage);
            int numberOfOutbreaks = 0;
            for (int ii = 0; ii < numberOfSimulations; ii++) {
                Simulations sim = new Simulations();
                sim.run();
                if (sim.getOutbreakSize() >= minOutbreakSize) numberOfOutbreaks++;
            }
            System.out.println(vaccinationCoverage + "\t" + (double)numberOfOutbreaks/numberOfSimulations);
        }
    }

    private void runOutbreaksLikelihoodVsVaccinationCoverage() {
        int numberOfSimulations = 2000;
        SimulationSettings.getInstance().setRge(0.01);
        SimulationSettings.getInstance().setOmega(0);
        int minOutbreakSize = 10;
        SimulationSettings.getInstance().setOutbreakSizeToStopSimulation(minOutbreakSize);
        int numberOfVaccinationCoverages = 10;
        double[] omega0 = new double[numberOfVaccinationCoverages];
        for (int i = 0; i < numberOfVaccinationCoverages; i++) {
            double vaccinationCoverage = 0.5 + (0.05*i);
            SimulationSettings.getInstance().setMinimumLevelOfNegativeVaccinationOpinion(1.0-vaccinationCoverage);
            int numberOfOutbreaks = 0;
            for (int ii = 0; ii < numberOfSimulations; ii++) {
                Simulations sim = new Simulations();
                sim.run();
                if (sim.getOutbreakSize() >= minOutbreakSize) numberOfOutbreaks++;
            }
            System.out.println(vaccinationCoverage + "\t" + (double)numberOfOutbreaks/numberOfSimulations);
            omega0[i] = (double)numberOfOutbreaks/numberOfSimulations;
        }
        // now that we have the baseline, run the sims
        for (int j = 0; j < 10; j++) {
            double omega = (j * 0.1) + 0.1;
            SimulationSettings.getInstance().setOmega(omega);
            System.out.println("omega = " + omega);
            for (int i = 0; i < numberOfVaccinationCoverages; i++) {
                double vaccinationCoverage = 0.5 + (0.05*i);
                SimulationSettings.getInstance().setMinimumLevelOfNegativeVaccinationOpinion(1.0-vaccinationCoverage);
                int numberOfOutbreaks = 0;
                for (int ii = 0; ii < numberOfSimulations; ii++) {
                    Simulations sim = new Simulations();
                    sim.run();
                    if (sim.getOutbreakSize() >= minOutbreakSize) numberOfOutbreaks++;
                }
                double outbreakProbability =  (double)numberOfOutbreaks/numberOfSimulations;
                System.out.println(vaccinationCoverage + "\t" + outbreakProbability / omega0[i]);
            }
        }
    }

    private void fileReading(String pathToDir) throws IOException {
        File dir = new File(pathToDir);
        File[] files = dir.listFiles();
        double[][] finalValues = new double[10][10];
        int fileCounter = 0;

        for (File file:files) {
            if (file.getName().startsWith(".")) continue;
            if (file.isDirectory()) continue;

            BufferedReader br = new FileReader(file.getAbsolutePath()).getBufferedReader();
            String str;
            int lineCounter = 0;

            while ((str = br.readLine()) !=null) {
                String[] values = str.split(",");

                for (int i = 0; i < values.length; i++) {
                    double value = Double.parseDouble(values[i]);
                    finalValues[lineCounter][i] += value;
                }
                lineCounter++;
            }
            fileCounter++;
        }


        for (int i = 0; i < finalValues.length; i++) {
            for (int ii = 0; ii < finalValues[i].length; ii++) {
                finalValues[i][ii] /= fileCounter;
            }
        }

        FileWriter fw = new FileWriter(pathToDir+"/results/averages.txt");
        for (int i = 0; i < finalValues.length; i++) {
            for (int ii = 0; ii < finalValues[i].length; ii++) {
                if (ii > 0) fw.write(",");
                fw.write(finalValues[i][ii]+"");
            }
            fw.writeln("");
        }
        fw.close();
    }
}