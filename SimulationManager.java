package com.salathegroup.socialcontagion;

import java.util.ArrayList;


public class SimulationManager {



    public static void main(String[] args) {
        SimulationManager sm = new SimulationManager();
        sm.heatmaps();
        // sm.rewire_clusters_outbreaks();
        // sm.threshold_clusters_outbreaks();
        // sm.rge_clusters_outbreaks();
        // sm.runOutbreaks_onlySusceptible();
        // sm.runOutbreaks_vs_exposureRate();
        // sm.runOutbreaks_vs_VaccineCoverage_vs_Threshold();
        // sm.runOutbreaksVsVaccinationCoverage();
        // sm.runOutbreaksLikelihoodVsVaccinationCoverage();
    }

    private void heatmaps() {

        ArrayList<Integer> ClusterCount;
        ArrayList<Double> OutbreakSize;
        int numberOfSimulations = 100;

        for (int omegaCounter = 0; omegaCounter < 10; omegaCounter++) {
            double omega = 0.05 + (0.05 * omegaCounter);
            SimulationSettings.getInstance().setOmega(omega);

            for (int rgeCounter = 0; rgeCounter < 10; rgeCounter++) {
                double rge = 0.05 + (0.05*rgeCounter);
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

                System.out.println(omega + "," + rge + "," + cAvg + "," + oAvg);
            }
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
}