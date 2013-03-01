package SocialContagion;
import java.util.ArrayList;

public class Person {
    private String id;
    private String vaccinationOpinion;
    private boolean tempValue = false;
    private int healthStatus;
    private int adoptStatus;
    private ArrayList<Integer> exposures;
    private ArrayList<Integer> exposureTimestamps;
    public static final int SUSCEPTIBLE = 1;
    public static final int INFECTED = 2;
    public static final int RESISTANT = 3;
    public static final int VACCINATED = 4;
    public static final int NONE = 1 ;
    public static final int onlySOCIAL = 2;
    public static final int MIXED = 3;
    public static final int onlyGENERAL = 4 ;
    private String infectedBy;

    public Person(String id, String vaccinationOpinion) {
        this.id = id;
        this.vaccinationOpinion = vaccinationOpinion;
        this.healthStatus = Person.SUSCEPTIBLE;
        this.adoptStatus = Person.NONE;
        this.exposures = new ArrayList<Integer>();
        this.exposureTimestamps = new ArrayList<Integer>();
        this.infectedBy = "nobody";
    }

    public String getInfector() {
        return this.infectedBy;
    }

    public void setInfector(String infector) {
        this.infectedBy = infector;
    }

    public String toString() {
        return this.id;
    }

    public String getVaccinationOpinion() {
        return this.vaccinationOpinion;
    }

    public String getID() {
        return this.id;
    }

    public Integer getIntID() {
        return Integer.parseInt(this.id);
    }

    public void setVaccinationOpinion(String vaccinationOpinion) {
        this.vaccinationOpinion = vaccinationOpinion;
    }

    public void increaseGeneralExposures(Integer exposureSource, Integer exposureTimestamp) {
        this.exposures.add(exposureSource);
        this.exposureTimestamps.add(exposureTimestamp);
    }

    public int getNumberOfExposures() {
        return this.exposures.size();
    }

    public ArrayList<Integer> getExposureList() {
        return this.exposures;
    }

    public ArrayList<Integer> getExposureTimestamps() {
        return this.exposureTimestamps;
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

    public boolean isMIXED() {
        return this.adoptStatus==Person.MIXED;
    }
}


