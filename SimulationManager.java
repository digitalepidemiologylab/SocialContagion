package com.salathegroup.socialcontagion;

public class SimulationManager {



    public static void main(String[] args) {
        SimulationManager sm = new SimulationManager();
        sm.runOutbreaks_onlySusceptible();
        // sm.runOutbreaks_vs_exposureRate();
        // sm.runOutbreaks_vs_VaccineCoverage_vs_Threshold();
        // sm.runOutbreaksVsVaccinationCoverage();
        // sm.runOutbreaksLikelihoodVsVaccinationCoverage();
    }

    private void runOutbreaks_onlySusceptible() {
        int numberOfSimulations = 500;
        int[] predictedOutbreakSize = new int[numberOfSimulations];


        for (int simCount = 0; simCount < numberOfSimulations; simCount++) {
            Simulations sim = new Simulations();
            sim.run();
        }
    }

    private void thresholds() {
        int numberOfSimulations = 500;
        int[] predictedOutbreakSize = new int[numberOfSimulations];
        for (int i = 1; i < 5; i++){

        }

        for (int simCount = 0; simCount < numberOfSimulations; simCount++) {
            Simulations sim = new Simulations();
            sim.run();
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