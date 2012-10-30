package com.salathegroup.socialcontagion;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SimulationManager {
    private PrintWriter output;
    public static void main(String[] args) throws IOException {
        SimulationManager sm = new SimulationManager();

        // sm.localHeatmaps();
         sm.testHerdImmunity();
        // sm.edgeTypes();
        // sm.degreeDIST();

        // sm.onlyGeneral();
        // sm.generalR0();

//        double rewire = Double.parseDouble(args[0]);
//        int threshold = Integer.parseInt(args[1]);
//        sm.theClusterHeatmaps(rewire,threshold);
    }

    public void edgeTypes() {
        int simCount = 50;
        SimulationSettings.getInstance().setT(2);
        double[] rewires = new double[5] ;
        rewires[0] = 0.90;
        rewires[1] = 0.50;
        rewires[2] = 0.25;
        rewires[3] = 0.10;
        rewires[4] = 0;
        double[] omegas = new double[3];
        omegas[0] = 0.01;
        omegas[1] = 0.001;
        omegas[2] = 0.0001;
        System.out.println("type," + "freq," + "rewire" + "," + "rge" + "," + "omega");
        for (int rewire=0; rewire<5; rewire++) {
            SimulationSettings.getInstance().setRewiringProbability(rewires[rewire]);
            SimulationSettings.getInstance().setRge(0.00001);
            for (int omegaCounter = 0; omegaCounter<3; omegaCounter++) {
                SimulationSettings.getInstance().setOmega(omegas[omegaCounter]);
                for (int simID=0; simID<simCount;simID++) {
                    Simulations sim = new Simulations();
                    sim.run();
                    System.out.println("basic" + "," + sim.getFractionEdgeType(Connection.BASIC) + "," + SimulationSettings.getInstance().getRewiringProbability() + "," + SimulationSettings.getInstance().getRge() + "," + SimulationSettings.getInstance().getOmega());
                    System.out.println("social" + "," + sim.getFractionEdgeType(Connection.SOCIAL) + "," + SimulationSettings.getInstance().getRewiringProbability() + "," + SimulationSettings.getInstance().getRge() + "," + SimulationSettings.getInstance().getOmega());
                }
            }
        }
    }

    public void degreeDIST() {
        int threshold = 2;
        SimulationSettings.getInstance().setT(threshold);
        double rewire = 0.01;
        SimulationSettings.getInstance().setRewiringProbability(rewire);
        int simCount = 10;
        int numberOfSusceptibles = 500;
        int steps = 20;
        int[][][] degreeDist = new int[steps][simCount][numberOfSusceptibles];
        double omegaMax = 0.01;
        double omegaStart = 0.0001;
        double omegaSteps = Math.pow(omegaMax/omegaStart, (1.0/(steps-1)));
        for (int omegaCounter = 0; omegaCounter < steps; omegaCounter++) {
            double omega = omegaStart * Math.pow(omegaSteps,omegaCounter);
            SimulationSettings.getInstance().setOmega(omega);
            System.out.println(omegaCounter);
            for (int i = 0; i < simCount; i++) {
                System.out.println("sim:" + i);
                Simulations sim = new Simulations();
                sim.run();
                degreeDist[omegaCounter][i] = sim.recordDegreeDistribution();
            }
        }
        //file print, input for data analysis
        String filename = "degDIST " + "thresh" + String.format("%d", threshold) + "," + System.currentTimeMillis();;
        PrintWriter out = null;
        try {
            out = new PrintWriter(new java.io.FileWriter(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println("threshold" + "," + "omegaCounter" + "," + "simID" + "," + "personID" + "," + "degree" + "," + "rewire");
        for (int omegaC=0; omegaC<steps; omegaC++) {
            for (int sim=0; sim<simCount; sim++) {
                for (int person=0; person<numberOfSusceptibles; person++) {
                    out.println(threshold + "," + omegaC + "," + sim + "," + person + "," + degreeDist[omegaC][sim][person] + "," + rewire);
                }
            }
        }
        out.close();
    }

    public void testHerdImmunity() {
        //100 networks and social contagion (per p, rge, omega) -- THEN -- 10,000 biological simulations for each network
        SimulationSettings.getInstance().setRewiringProbability(.1);
        SimulationSettings.getInstance().setRge(0.001);
        SimulationSettings.getInstance().setOmega(0.01);
        int coverages = 10;
        double[] epidemicFrequency = new double[coverages];
        System.out.println("p@" + SimulationSettings.getInstance().getRewiringProbability() + " // " + "RGE@" + SimulationSettings.getInstance().getRge() + " // " + "Omega@" + SimulationSettings.getInstance().getOmega());
        System.out.println("epiFreq, threshold, negSent, coverage, simID");

        for (int simCount = 0; simCount<100;simCount++) {
            for (int thresh = 1; thresh<3; thresh++) {
                SimulationSettings.getInstance().setT(thresh);
                for (int i = 1; i < coverages; i++) {
                    double negSent = 0.05 * i;
                    SimulationSettings.getInstance().getMaxLevelofNegativeSentiment(negSent);
                    double vaccinationCoverage = 1.0 - negSent;
                    Simulations sim = new Simulations();
                    sim.run();
                    epidemicFrequency[i] = sim.getNumberOfEpidemics();
                    System.out.println(epidemicFrequency[i] + "," + thresh + "," + negSent + "," + vaccinationCoverage + "," + simCount);
                }
            }
        }
    }

    private void theClusterHeatmaps(Double rewire, int threshold) {
        int steps = 20;
        double omegaMax = 0.01;
        double omegaStart = 0.0001;
        double omegaSteps = Math.pow(omegaMax/omegaStart, (1.0/(steps-1)));
        double rgeMax = 0.01;
        double rgeStart = 0.00001;
        double rgeSteps = Math.pow(rgeMax/rgeStart, (1.0/(steps-1)));
        double outbreaksHeatmap[][] = new double[steps][steps];
        int clustersHeatmap[][] = new int[steps][steps];
        double simOutbreaksHeatmap[][] = new double[steps][steps];
        double edgeRemovalOUTBREAKS[][] = new double[steps][steps];
        int edgeRemovalCLUSTERS[][] = new int[steps][steps];
        double social[][] = new double[steps][steps];
        double general[][] = new double[steps][steps];
        double mixed[][] = new double[steps][steps];

        for (int omegaCounter = 0; omegaCounter < steps; omegaCounter++) {
            double omega = omegaStart * Math.pow(omegaSteps,omegaCounter);
            SimulationSettings.getInstance().setOmega(omega);
            for (int rgeCounter = 0; rgeCounter < steps; rgeCounter++) {
                double rge = rgeStart * Math.pow(rgeSteps,rgeCounter);
                SimulationSettings.getInstance().setRge(rge);
                System.out.println(omega + "," + rge);
                SimulationSettings.getInstance().setT(threshold);
                SimulationSettings.getInstance().setRewiringProbability(rewire);
                Simulations sim = new Simulations();
                sim.run();

                outbreaksHeatmap[omegaCounter][rgeCounter] = sim.getPredictedOutbreakSize();
                clustersHeatmap[omegaCounter][rgeCounter] = sim.getNumberOfClusters();
                simOutbreaksHeatmap[omegaCounter][rgeCounter] = sim.getSimulatedAverageOutbreak();

                social[omegaCounter][rgeCounter] = sim.getFractionAdoptStatus(Person.onlySOCIAL);
                general[omegaCounter][rgeCounter] = sim.getFractionAdoptStatus(Person.onlyGENERAL);
                mixed[omegaCounter][rgeCounter] = sim.getFractionAdoptStatus(Person.MIXED);

            }
        }

        String socialFilename = "social" + "," + String.format("%02d", threshold) + "," + String.format("%.2f", SimulationSettings.getInstance().getRewiringProbability()) + "," + System.currentTimeMillis();
        PrintWriter outSocial = null;
        try {
            outSocial = new PrintWriter(new java.io.FileWriter(socialFilename));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        for (int i = 0; i < steps; i++) {
            for (int ii = 0; ii < steps; ii++) {
                if (ii == steps-1) {
                    outSocial.println(social[i][ii]);
                }
                else outSocial.print(social[i][ii] + ",");
            }
        }
        outSocial.close();

        String generalFilename = "general" + "," + String.format("%02d", threshold) + "," + String.format("%.2f", SimulationSettings.getInstance().getRewiringProbability()) + "," + System.currentTimeMillis();
        PrintWriter outGeneral = null;
        try {
            outGeneral = new PrintWriter(new java.io.FileWriter(generalFilename));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        for (int i = 0; i < steps; i++) {
            for (int ii = 0; ii < steps; ii++) {
                if (ii == steps-1) {
                    outGeneral.println(general[i][ii]);
                }
                else outGeneral.print(general[i][ii] + ",");
            }
        }
        outGeneral.close();

        String mixedFilename = "mixed" + "," + String.format("%02d", threshold) + "," + String.format("%.2f", SimulationSettings.getInstance().getRewiringProbability()) + "," + System.currentTimeMillis();
        PrintWriter outMixed = null;
        try {
            outMixed = new PrintWriter(new java.io.FileWriter(mixedFilename));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        for (int i = 0; i < steps; i++) {
            for (int ii = 0; ii < steps; ii++) {
                if (ii == steps-1) {
                    outMixed.println(mixed[i][ii]);
                }
                else outMixed.print(mixed[i][ii] + ",");
            }
        }
        outMixed.close();

        String outbreaksFilename = "predictedOutbreaks" + "," + String.format("%02d", threshold) + "," + String.format("%.2f", SimulationSettings.getInstance().getRewiringProbability()) + "," + System.currentTimeMillis();
        PrintWriter outPredict = null;
        try {
            outPredict = new PrintWriter(new java.io.FileWriter(outbreaksFilename));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        for (int i = 0; i < steps; i++) {
            for (int ii = 0; ii < steps; ii++) {
                if (ii == steps-1) {
                    outPredict.println(outbreaksHeatmap[i][ii]);
                }
                else outPredict.print(outbreaksHeatmap[i][ii] + ",");
            }
        }
        outPredict.close();

        String clustersFilename = "clusters" + "," + String.format("%02d", threshold) + "," + String.format("%.2f", SimulationSettings.getInstance().getRewiringProbability()) + "," + System.currentTimeMillis();
        PrintWriter outClusters = null;
        try {
            outClusters = new PrintWriter(new java.io.FileWriter(clustersFilename));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        for (int i = 0; i < steps; i++) {
            for (int ii = 0; ii < steps; ii++) {
                if (ii == steps-1) {
                    outClusters.println(clustersHeatmap[i][ii]);
                }
                else outClusters.print(clustersHeatmap[i][ii] + ",");
            }
        }
        outClusters.close();

        String simulatedFilename = "simulatedOutbreaks" + "," + String.format("%02d", threshold) + "," + String.format("%.2f", SimulationSettings.getInstance().getRewiringProbability()) + "," + System.currentTimeMillis();
        PrintWriter outSimulate = null;
        try {
            outSimulate = new PrintWriter(new java.io.FileWriter(simulatedFilename));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        for (int i = 0; i < steps; i++) {
            for (int ii = 0; ii < steps; ii++) {
                if (ii == steps-1) {
                    outSimulate.println(simOutbreaksHeatmap[i][ii]);
                }
                else outSimulate.print(simOutbreaksHeatmap[i][ii] + ",");
            }
        }
        outSimulate.close();

    }

    private void onlyGeneral() {
        int simCount = 100;
        SimulationSettings.getInstance().setOmega(0);
        SimulationSettings.getInstance().setRge(0.00001);
        SimulationSettings.getInstance().setT(2);
        SimulationSettings.getInstance().setRewiringProbability(.5);
        System.out.println("threshold," + "rewire," + "predOut," + "clusters");
        for (int i =0; i<simCount;i++) {
            Simulations sim = new Simulations();
            sim.run();
            double predOut = sim.getPredictedOutbreakSize();
            int clust = sim.getNumberOfClusters();
            double simOut = sim.getSimulatedAverageOutbreak();
            System.out.println(SimulationSettings.getInstance().getT() + "," + SimulationSettings.getInstance().getRewiringProbability() + "," + predOut + "," + clust + "," + simOut);
        }
    }

    public void generalOnlyR0() {
        int threshold = 2;
        double rewire = 0.01;
        SimulationSettings.getInstance().setT(threshold);
        SimulationSettings.getInstance().setRewiringProbability(rewire);
        SimulationSettings.getInstance().setOmega(0);
        int simCount = 100;
        int numberOfSusceptibles = 500;
        int[][] degreeDist = new int[simCount][numberOfSusceptibles];
        System.out.println("threshold" + "," + "omegaCounter" + "," + "simID" + "," + "personID" + "," + "degree" + "," + "rewire");
        for (int i = 0; i < simCount; i++) {
            Simulations sim = new Simulations();
            sim.run();
            degreeDist[i] = sim.recordDegreeDistribution();
            for (int person=0; person<numberOfSusceptibles; person++) {
                System.out.println(threshold + "," + 0 + "," + i + "," + person + "," + degreeDist[i][person] + "," + rewire);
            }
        }
    }
    private void localHeatmaps() {
        //for pre-cluster submission testing
        int numberOfSimulations = 1;
        int steps = 20;
        ArrayList<double[][]> outbreaks = new ArrayList<double[][]>();
        ArrayList<int[][]> clusters = new ArrayList<int[][]>();
        for (int simCount = 0; simCount < numberOfSimulations; simCount++){
            double outbreaks_heatmap[][] = new double[20][20];
            int clusters_heatmap[][] = new int[20][20];
            System.out.println(simCount);
            double omegaMax = 0.01;
            double omegaStart = 0.0001;
            double omegaSteps = Math.pow(omegaMax/omegaStart, (1.0/(steps-1))) ;
            double rgeMax = 0.01;
            double rgeStart = 0.00001;
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
                    clusters_heatmap[omegaCounter][rgeCounter] = sim.getNumberOfClusters();
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
            for (int i = 0; i < 0; i++) {
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
            for (int i = 0; i < 20; i++) {
                out.println(clusters.get(simulation)[i][0] + "," + clusters.get(simulation)[i][1] + "," + clusters.get(simulation)[i][2] + "," + clusters.get(simulation)[i][3] + "," + clusters.get(simulation)[i][4] + "," + clusters.get(simulation)[i][5] + "," + clusters.get(simulation)[i][6] + "," + clusters.get(simulation)[i][7] + "," + clusters.get(simulation)[i][8] + "," + clusters.get(simulation)[i][9]) ;
            }
            out.close();
        }
    }
}
