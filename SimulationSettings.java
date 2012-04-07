package com.salathegroup.socialcontagion;

public class SimulationSettings {

    private static SimulationSettings ourInstance = new SimulationSettings();


    private int numberOfPeople = 5000;
    private int k = 10;
    private double rewiringProbability = 0.90;
    private double rge = 0.001;
    private int T = 2;
    private double omega = 0.01;
    private double minimumLevelOfNegativeVaccinationOpinion = 0.1;
    private double infectionRate = 0.1;
    private double recoveryRate = 0.1;
    private int outbreakSizeToStopSimulation = Integer.MAX_VALUE; // if this is max, the simulation will run its entire course


    public static SimulationSettings getInstance() {
        return ourInstance;
    }

    private SimulationSettings() {
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public double getRewiringProbability() {
        return rewiringProbability;
    }

    public void setRewiringProbability(double rewiringProbability) {
        this.rewiringProbability = rewiringProbability;
    }

    public double getRge() {
        return rge;
    }

    public void setRge(double rge) {
        this.rge = rge;
    }

    public int getT() {
        return T;
    }

    public void setT(int t) {
        T = t;
    }

    public double getOmega() {
        return omega;
    }

    public void setOmega(double omega) {
        this.omega = omega;
    }

    public double getMinimumLevelOfNegativeVaccinationOpinion() {
        return minimumLevelOfNegativeVaccinationOpinion;
    }

    public void setMinimumLevelOfNegativeVaccinationOpinion(double minimumLevelOfNegativeVaccinationOpinion) {
        this.minimumLevelOfNegativeVaccinationOpinion = minimumLevelOfNegativeVaccinationOpinion;
    }

    public double getInfectionRate() {
        return infectionRate;
    }

    public void setInfectionRate(double infectionRate) {
        this.infectionRate = infectionRate;
    }

    public double getRecoveryRate() {
        return recoveryRate;
    }

    public void setRecoveryRate(double recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public int getOutbreakSizeToStopSimulation() {
        return outbreakSizeToStopSimulation;
    }

    public void setOutbreakSizeToStopSimulation(int outbreakSizeToStopSimulation) {
        this.outbreakSizeToStopSimulation = outbreakSizeToStopSimulation;
    }
}