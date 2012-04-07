package com.salathegroup.socialcontagion;

import java.io.*;
import java.util.ArrayList;



public class SimulationManager {


    public static void main(String[] args) throws IOException {
        SimulationManager sm = new SimulationManager();
        // sm.fileReading("/Users/ellscampbell/Documents/SocialContagion/rgeXomega,10sims,p0/clusters");
         sm.adoptionANDedgeType();
        // sm.heatmaps();
        // sm.rewire_clusters_outbreaks();
        // sm.threshold_clusters_outbreaks();
        // sm.rge_clusters_outbreaks();
        // sm.runOutbreaks_onlySusceptible();
        // sm.runOutbreaks_vs_exposureRate();
        // sm.runOutbreaks_vs_VaccineCoverage_vs_Threshold();
        // sm.runOutbreaksVsVaccinationCoverage();
        // sm.runOutbreaksLikelihoodVsVaccinationCoverage();
    }

    public void playground() {

    }

    public void adoptionANDedgeType() {
        int numberOfSimulations = 100;
        SimulationSettings.getInstance().setOmega(0.01);
        SimulationSettings.getInstance().setRge(0.001);
        
        System.out.println("p(rewire)="+SimulationSettings.getInstance().getRewiringProbability() + "," + "OMEGA=.01,RGE=.001") ;
        System.out.println("GEN"+ "," + "PEER"+ "," + "MIXED" + "," + "SocialEdges");

        for (int simCount = 0; simCount < numberOfSimulations; simCount++){
            Simulations sim = new Simulations();
            sim.run();
        }
    }


    private void heatmaps() {
        int numberOfSimulations = 10;
        int steps = 10;
        ArrayList<double[][]> Outbreaks = new ArrayList<double[][]>();
        ArrayList<int[][]> Clusters = new ArrayList<int[][]>();

        for (int simCount = 0; simCount < numberOfSimulations; simCount++){
            double outbreaks_heatmap[][] = new double[10][10];
            int clusters_heatmap[][] = new int[10][10];
            System.out.println(simCount);

            double omegaMax = 0.02;
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

                    Simulations sim = new Simulations();
                    sim.run();

                    outbreaks_heatmap[omegaCounter][rgeCounter] = sim.predictOutbreakSize();
                    clusters_heatmap[omegaCounter][rgeCounter] = sim.getClusterCount();

                }
            }
            Outbreaks.add(outbreaks_heatmap);
            Clusters.add(clusters_heatmap);
        }

        for (int simulation = 0; simulation < numberOfSimulations; simulation++) {
            String outbreaksFilename = "Outbreaks" + String.format("%3d",simulation) + "," + String.format("%.2f", SimulationSettings.getInstance().getRewiringProbability());
            PrintWriter out = null;
            try {
                out = new PrintWriter(new java.io.FileWriter(outbreaksFilename));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            for (int i = 0; i < 10; i++) {
                out.println(Outbreaks.get(simulation)[i][0] + "," + Outbreaks.get(simulation)[i][1] + "," + Outbreaks.get(simulation)[i][2] + "," + Outbreaks.get(simulation)[i][3] + "," + Outbreaks.get(simulation)[i][4]+ "," + Outbreaks.get(simulation)[i][5]+ "," + Outbreaks.get(simulation)[i][6] + "," + Outbreaks.get(simulation)[i][7] + "," + Outbreaks.get(simulation)[i][8] + "," + Outbreaks.get(simulation)[i][9]) ;
            }
            out.close();
        }

        for (int simulation = 0; simulation < numberOfSimulations; simulation++) {
            String clustersFilename = "Clusters" + String.format("%3d",simulation) + "," + String.format("%.2f", SimulationSettings.getInstance().getRewiringProbability());
            PrintWriter out = null;
            try {
                out = new PrintWriter(new java.io.FileWriter(clustersFilename));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            for (int i = 0; i < 10; i++) {
                out.println(Clusters.get(simulation)[i][0] + "," + Clusters.get(simulation)[i][1] + "," + Clusters.get(simulation)[i][2] + "," + Clusters.get(simulation)[i][3] + "," + Clusters.get(simulation)[i][4] + "," + Clusters.get(simulation)[i][5] + "," + Clusters.get(simulation)[i][6] + "," + Clusters.get(simulation)[i][7] + "," + Clusters.get(simulation)[i][8] + "," + Clusters.get(simulation)[i][9]) ;
            }
            out.close();
        }
    }

    private void rewire_clusters_outbreaks() {

        ArrayList<Integer> ClusterCount;
        ArrayList<Double> OutbreakSize;
        int numberOfSimulations = 500;
        int rewireRange = 400;
        SimulationSettings.getInstance().setMinimumLevelOfNegativeVaccinationOpinion(0.10);


        for (int rewireCounter = 0; rewireCounter < rewireRange; rewireCounter++) {
            double rewiringProbability = 0.101 + (0.001 * rewireCounter);
            SimulationSettings.getInstance().setRewiringProbability(rewiringProbability);
            ClusterCount = new ArrayList<Integer>();
            OutbreakSize = new ArrayList<Double>();

            for (int simCount = 0; simCount < numberOfSimulations; simCount++){
                Simulations sim = new Simulations();
                sim.run();

                ClusterCount.add(sim.getClusterCount());
                OutbreakSize.add(sim.predictOutbreakSize());
            }
            int cSum = 0;
            double oSum = 0;
            for (int i = 0; i < numberOfSimulations; i++) {
                cSum = cSum + ClusterCount.get(i);
                oSum = oSum + OutbreakSize.get(i);
            }

            double cAvg = (double)cSum/numberOfSimulations;
            double oAvg = oSum/numberOfSimulations;

            System.out.println(rewiringProbability + "," + cAvg + "," + oAvg);

        }
    }

    private void threshold_clusters_outbreaks() {

        ArrayList<Integer> ClusterCount;
        ArrayList<Double> OutbreakSize;
        int numberOfSimulations = 500;
        int thresholdRange = 11;
        SimulationSettings.getInstance().setMinimumLevelOfNegativeVaccinationOpinion(0.10);


        for (int threshold = 1; threshold < thresholdRange; threshold++) {
            SimulationSettings.getInstance().setT(threshold);
            ClusterCount = new ArrayList<Integer>();
            OutbreakSize = new ArrayList<Double>();

            for (int simCount = 0; simCount < numberOfSimulations; simCount++){
                Simulations sim = new Simulations();
                sim.run();

                ClusterCount.add(sim.getClusterCount());
                OutbreakSize.add(sim.predictOutbreakSize());
            }
            int cSum = 0;
            double oSum = 0;
            for (int i = 0; i < numberOfSimulations; i++) {
                cSum = cSum + ClusterCount.get(i);
                oSum = oSum + OutbreakSize.get(i);
            }

            double cAvg = (double)cSum/numberOfSimulations;
            double oAvg = oSum/numberOfSimulations;

            System.out.println(threshold + "," + cAvg + "," + oAvg);

        }
    }

    private void rge_clusters_outbreaks() {

        ArrayList<Integer> ClusterCount;
        ArrayList<Double> OutbreakSize;
        int numberOfSimulations = 500;
        int rgeRange = 100;
        SimulationSettings.getInstance().setMinimumLevelOfNegativeVaccinationOpinion(0.10);


        for (int rgeCounter = 0; rgeCounter < rgeRange; rgeCounter++) {
            double rge = 0.01 + (0.01*rgeCounter);
            SimulationSettings.getInstance().setRge(rge);
            ClusterCount = new ArrayList<Integer>();
            OutbreakSize = new ArrayList<Double>();

            for (int simCount = 0; simCount < numberOfSimulations; simCount++){
                Simulations sim = new Simulations();
                sim.run();

                ClusterCount.add(sim.getClusterCount());
                OutbreakSize.add(sim.predictOutbreakSize());

            }

            int cSum = 0;
            double oSum = 0;
            for (int i = 0; i < numberOfSimulations; i++) {
                cSum = cSum + ClusterCount.get(i);
                oSum = oSum + OutbreakSize.get(i);
            }

            double cAvg = (double)cSum/numberOfSimulations;
            double oAvg = oSum/numberOfSimulations;

            System.out.println(cAvg + "," + oAvg);



        }
    }

    private void runOutbreaks_vs_Omega() {
        int numberOfSimulations = 500;
        int minOutbreakSize = 10;

        for (int i = 1; i < 25; i++) {
            double omega = 0.01 * i;
            SimulationSettings.getInstance().setOmega(omega);

            int numberOfOutbreaks = 0;
            for (int ii = 0; ii < 10; ii++){
                double vaccinationCoverage = 0.5 + (0.05 * ii);
                SimulationSettings.getInstance().setMinimumLevelOfNegativeVaccinationOpinion(1.0 - vaccinationCoverage);

                for (int iii = 0; iii < numberOfSimulations; iii++) {
                    Simulations sim = new Simulations();
                    sim.run();
                    if (sim.getOutbreakSize() >= minOutbreakSize) numberOfOutbreaks++;
                }
                System.out.println(vaccinationCoverage + "\t" + (double)numberOfOutbreaks);
            }
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