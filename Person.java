package com.salathegroup.socialcontagion;

import java.util.HashSet;


public class Person {

    private String id;
    private String vaccinationOpinion;
    private boolean tempValue = false;
    private int healthStatus;
    private int adoptStatus;
    private HashSet<String> exposures;

    public static final int SUSCEPTIBLE = 1;
    public static final int INFECTED = 2;
    public static final int RESISTANT = 3;
    public static final int VACCINATED = 4;
    
    public static final int NONE = 1 ;
    public static final int onlySOCIAL = 2;
    public static final int onlyGENERAL = 3;
    public static final int mixedSOCIAL = 4 ;
    public static final int mixedGENERAL = 5;


    public Person(String id, String vaccinationOpinion) {
        this.id = id;
        this.vaccinationOpinion = vaccinationOpinion;
        this.healthStatus = Person.SUSCEPTIBLE;
        this.adoptStatus = Person.NONE;
        this.exposures = new HashSet<String>();
    }

    public String toString() {
        return this.id;
    }

    public String getVaccinationOpinion() {
        return this.vaccinationOpinion;
    }

    public void setVaccinationOpinion(String vaccinationOpinion) {
        this.vaccinationOpinion = vaccinationOpinion;
    }

    public void increaseGeneralExposures(String exposureSource) {
        this.exposures.add(exposureSource);
    }

    public int getNumberOfExposures() {
        return this.exposures.size();
    }

    public HashSet<String> getExposureHashSet() {
        return this.exposures;
    }

    public void setTempValue(boolean b) {
        this.tempValue = b;
    }

    public boolean getTempValue() {
        return this.tempValue;
    }

    public void setHealthStatus(int healthStatus) {
        this.healthStatus = healthStatus;
    }

    public void setAdoptStatus(int adoptStatus) {
        this.adoptStatus = adoptStatus;
    }
    
    public int getAdoptStatus() {
        return this.adoptStatus;
    }

    public boolean isSusceptible() {
        return this.healthStatus==Person.SUSCEPTIBLE;
    }

    public boolean isInfected() {
        return this.healthStatus==Person.INFECTED;
    }

    public boolean isResistant() {
        return this.healthStatus==Person.RESISTANT;
    }
    
    public boolean isVaccinated() {
        return this.healthStatus==Person.VACCINATED;
    }

    public boolean isNONE() {
        return this.adoptStatus==Person.NONE;
    }
    
    public boolean isSOCIAL() {
        return this.adoptStatus==Person.onlySOCIAL;
    }
    
    public boolean isGENERAL() {
        return this.adoptStatus==Person.onlyGENERAL;
    }
    
    public boolean ismixedSOCIAL() {
        return this.adoptStatus==Person.mixedSOCIAL;
    }

    public boolean ismixedGENERAL() {
        return this.adoptStatus==Person.mixedGENERAL;
    }

}