package com.salathegroup.socialcontagion;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.algorithms.layout.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Simulations {

    Graph<Person, Connection> g;
    Graph<Person, Connection> SusceptibleGraph;
    Vector<Graph<Person, Connection>> MultiGraphs;
    int socialTimestep = 0;
    int biologicalTimestep = 0;
    Person[] people;
    Random random = new Random();
    boolean opinionIsSpreading = false;
    boolean diseaseIsSpreading = false;
    int outbreakSize = 0;
    int numberOfAntiVaccineOpinions = 0;
    double predictedOutbreakSize = 0;
    int[] clusterSizeSquared;
    int[] clusterSize;
    ArrayList<Double> avgClusterDistances;

    public static void main(String[] args) {
        Simulations simulation = new Simulations();
        simulation.run();
    }

    public void run(){
        this.initGraph();
        this.runSocialTimesteps();
        this.removeVaccinated();
        this.clusters();
    }


    private void runSocialTimesteps() {
        while(true) {
            if (this.socialTimestep==0) this.opinionIsSpreading = true;
            if (this.opinionIsSpreading) {
                this.generalExposure();
                this.socialContagion();
                if (!this.opinionIsSpreading) {
                    if (this.getFractionOfNegativeVaccinationOpinion() == 0) break;
                    this.vaccinate();
                }
            }
            this.socialTimestep++;
            if (!this.opinionIsSpreading) break;
        }
    }

    private void runBiologicalTimesteps() {
        diseaseIsSpreading = true;
        infectRandomIndexCase();
        while(true) {
            biologicalContagion();
            this.biologicalTimestep++;
            if (!diseaseIsSpreading) break;
        }
    }

    private double getFractionHealthStatus(int status) {
        int numberOfPeople = SimulationSettings.getInstance().getNumberOfPeople();
        int counter = 0;
        for (int i = 0; i < numberOfPeople; i++) {
            if (status == Person.SUSCEPTIBLE && this.people[i].isSusceptible()) counter++;
            if (status == Person.INFECTED && this.people[i].isInfected()) counter++;
            if (status == Person.RESISTANT && this.people[i].isResistant()) counter++;
        }
        return (double)counter / numberOfPeople;
    }


    private void infectRandomIndexCase() {
        int numberOfPeople = SimulationSettings.getInstance().getNumberOfPeople();
        Person indexCase;
        do {
            indexCase = this.people[this.random.nextInt(numberOfPeople)];
        }
        while (!indexCase.isSusceptible());
        this.infectPerson(indexCase);
    }

    private void vaccinate() {
        int numberOfPeople = SimulationSettings.getInstance().getNumberOfPeople();
        for (int i = 0; i < numberOfPeople; i++) {
            if (this.people[i].getVaccinationOpinion().equals("+")) {
                this.people[i].setHealthStatus(Person.VACCINATED);
            }
        }
    }

    private double getFractionOfNegativeVaccinationOpinion() {
        int numberOfPeople = SimulationSettings.getInstance().getNumberOfPeople();
        int numberOfNegativeOpinions = 0;
        for (int i = 0; i < numberOfPeople; i++) {
            if (this.people[i].getVaccinationOpinion().equals("-")) {
                numberOfNegativeOpinions++;
            }
        }
        return (double)numberOfNegativeOpinions / numberOfPeople;
    }

    private void generalExposure() {
        int numberOfPeople = SimulationSettings.getInstance().getNumberOfPeople();
        double rge = SimulationSettings.getInstance().getRge();
        double numberOfPeopleToExpose = rge * numberOfPeople;
        while (numberOfPeopleToExpose > 0) {
            if (numberOfPeopleToExpose < 1) {
                if (random.nextDouble() > numberOfPeopleToExpose) break;
            }
            this.people[random.nextInt(numberOfPeople)].increaseGeneralExposures("generalExposure_t="+this.socialTimestep);
            numberOfPeopleToExpose--;
        }
    }

    private void socialContagion() {
        int T = SimulationSettings.getInstance().getT();
        double omega = SimulationSettings.getInstance().getOmega();
        for (Person person:this.g.getVertices()) {
            if (omega == 0) continue;
            if (person.getVaccinationOpinion().equals("-")) continue;
            for (Person neighbour:this.g.getNeighbors(person)) {
                if (neighbour.getVaccinationOpinion().equals("-")) {
                    if (this.random.nextDouble() < omega) {
                        person.increaseGeneralExposures(neighbour.toString());
                    }
                }
            }
        }
        for (Person person:this.g.getVertices()) {
            if (person.getNumberOfExposures() >= T) {
                person.setTempValue(true);
            }
        }
        for (Person person:this.g.getVertices()) {
            if (person.getTempValue()) {
                this.setAntiVaccinationOpinion(person);
                person.setTempValue(false);
            }
        }
    }

    private void setAntiVaccinationOpinion(Person person) {
        if (this.opinionIsSpreading) {
            if (person.getVaccinationOpinion().equals("-")) return; // no need to overwrite and mistakenly count this as an additional anti vaccine opinion
            person.setVaccinationOpinion("-");
            this.numberOfAntiVaccineOpinions++;
            if (this.numberOfAntiVaccineOpinions >= SimulationSettings.getInstance().getMinimumLevelOfNegativeVaccinationOpinion() * SimulationSettings.getInstance().getNumberOfPeople()) {
                this.opinionIsSpreading = false;
            }
        }
    }

    private void biologicalContagion() {
        this.infect_recover();
        if (this.getFractionHealthStatus(Person.INFECTED) == 0) this.diseaseIsSpreading = false;
    }

    private void recovery() {
        int numberOfPeople = SimulationSettings.getInstance().getNumberOfPeople();
        double recoveryRate = SimulationSettings.getInstance().getRecoveryRate();
        for (int i = 0; i < numberOfPeople; i++) {
            if (this.people[i].isInfected()) {
                if (this.random.nextDouble() < recoveryRate) this.people[i].setHealthStatus(Person.RESISTANT);
            }
        }
    }

    private void infect_recover() {
        double infectionRate = SimulationSettings.getInstance().getInfectionRate();
        for (Person person:this.g.getVertices()) {
            if (!person.isSusceptible()) continue;
            int numberOfInfectedNeighbours = 0;
            for (Person neighbour:this.g.getNeighbors(person)) {
                if (neighbour.isInfected()) {
                    numberOfInfectedNeighbours++;
                }
            }
            double probabilityOfInfection = 1.0 - Math.pow(1.0 - infectionRate,numberOfInfectedNeighbours);
            if (this.random.nextDouble() < probabilityOfInfection) {
                person.setTempValue(true);
            }
        }
        //recovery goes here to ensure that individuals cannot recover IMMEDIATELY
        this.recovery();

        for (Person person:this.g.getVertices()) {
            if (person.getTempValue()) {
                this.infectPerson(person);
                person.setTempValue(false);
            }
        }
    }

    private void infectPerson(Person person) {
        if (!this.diseaseIsSpreading) return;
        person.setHealthStatus(Person.INFECTED);
        this.outbreakSize++;
        if (this.outbreakSize >= SimulationSettings.getInstance().getOutbreakSizeToStopSimulation()) {
            this.diseaseIsSpreading = false;
        }
    }

    private void initGraph() {
        Set components;
        int numberOfPeople = SimulationSettings.getInstance().getNumberOfPeople();
        int k = SimulationSettings.getInstance().getK();
        this.people = new Person[numberOfPeople];
        do {
            this.g = new SparseGraph<Person, Connection>();
            for (int i = 0; i < numberOfPeople; i++) {
                Person person = new Person(i+"","+");
                this.people[i] = person;
                this.g.addVertex(person);
            }
            for (int i = 0; i < numberOfPeople; i++) {
                for (int ii = 0; ii < k; ii++) {
                    int diff = ii/2 + 1;
                    if (ii%2 == 1) diff *= -1;
                    int newIndex = i + diff;
                    if (newIndex < 0) newIndex += numberOfPeople;
                    if (newIndex >= numberOfPeople) newIndex -= numberOfPeople;
                    this.g.addEdge(new Connection(),this.people[i],this.people[newIndex]);
                }
            }
            for (Connection edge:this.g.getEdges()) {
                if (this.random.nextDouble() < SimulationSettings.getInstance().getRewiringProbability()) {
                    Person source = this.g.getEndpoints(edge).getFirst();
                    Person newDestination;
                    do {
                        newDestination = this.people[this.random.nextInt(numberOfPeople)];
                    }
                    while (this.g.isNeighbor(source,newDestination) || source.equals(newDestination));
                    this.g.removeEdge(edge);
                    this.g.addEdge(new Connection(),source,newDestination);
                }
            }
            WeakComponentClusterer wcc = new WeakComponentClusterer();
            components = wcc.transform(this.g);
        }
        while (components.size() > 1);

    }

    private void removeVaccinated() {
        for (int i = 0; i < SimulationSettings.getInstance().getNumberOfPeople(); i++) {
            if (people[i].isVaccinated()) this.g.removeVertex(people[i]);
        }
    }

    private void clusters() {
        this.makeClusters();
        this.measureClusters();

    }

    private void makeClusters() {
        Set negativeClusters;
        WeakComponentClusterer wcc = new WeakComponentClusterer();
        negativeClusters = wcc.transform(this.g);
        MultiGraphs = new Vector<Graph<Person, Connection>>();
        for (Object clusterObject:negativeClusters)  {
            Set cluster = (Set)clusterObject;
            this.SusceptibleGraph = new SparseGraph<Person, Connection>();
            MultiGraphs.add(this.SusceptibleGraph);
            for (Object personObject:cluster) {
                Person person = (Person)personObject;
                this.SusceptibleGraph.addVertex(person);
                for (Person neighbor:this.g.getNeighbors(person)) {
                    this.SusceptibleGraph.addVertex(neighbor);
                    this.SusceptibleGraph.addEdge(new Connection(), person, neighbor);
                }
            }
        }
    }

    private void measureClusters(){
        int graphCounter = 0;
        this.avgClusterDistances = new ArrayList<Double>();
        this.clusterSizeSquared = new int[1000];
        this.clusterSize = new int[1000];
        for (Graph<Person, Connection> SusceptibleGraph:MultiGraphs) {
            graphCounter++;
            double distanceSum = 0;
            for (Person person:SusceptibleGraph.getVertices()) {
                Transformer<Person, Double> distances = DistanceStatistics.averageDistances(SusceptibleGraph);
                distanceSum = distanceSum + (1/distances.transform(person));
            }
            clusterSize[graphCounter] = SusceptibleGraph.getVertexCount();
            clusterSizeSquared[graphCounter] = (SusceptibleGraph.getVertexCount() * SusceptibleGraph.getVertexCount());
            double distanceAverage = distanceSum/SusceptibleGraph.getVertexCount();
            avgClusterDistances.add(distanceAverage);
            //System.out.println("Susceptible Cluster " + "#" + graphCounter +" // "+"Size = "+ SusceptibleGraph.getVertexCount()+" // "+"Average Distance = "+distanceAverage);
        }
    }
    
    public int getClusterCount() {
        return MultiGraphs.size();
    }

    public double getMaxDistance(){
        double maxValue = 0;
        for (int maxCounter = 0; maxCounter < avgClusterDistances.size(); maxCounter++) {
            if (this.avgClusterDistances.get(maxCounter) > maxValue) {
                if (this.avgClusterDistances.get(maxCounter).isNaN()) continue;
                else maxValue = this.avgClusterDistances.get(maxCounter);
            }
        }
        return maxValue;
    }

    public double predictOutbreakSize(){
        int squaredSum = 0;
        for (int i = 0; i < this.clusterSizeSquared.length; i++) {
            squaredSum = squaredSum + this.clusterSizeSquared[i];
        }
        int sizeSum = 0;
        for (int i = 0; i < this.clusterSize.length; i++) {
            sizeSum = sizeSum + this.clusterSize[i];
        }
        this.predictedOutbreakSize = squaredSum/sizeSum;
        return this.predictedOutbreakSize;
    }

    private void resetNegativeOpinions() {
        for (int person = 0; person < SimulationSettings.getInstance().getNumberOfPeople(); person++) {
            if (people[person].getVaccinationOpinion().equals("-")) people[person].setHealthStatus(Person.SUSCEPTIBLE);
        }
    }

    public int getOutbreakSize() {
        return this.outbreakSize;
    }

    private void plotGraph() {
        // The Layout<V, E> is parameterized by the vertex and edge types
        Layout<Person, Connection> layout = new KKLayout<Person, Connection>(this.g);
        layout.setSize(new Dimension(900,900)); // sets the initial size of the space
        // The BasicVisualizationServer<V,E> is parameterized by the edge types
        BasicVisualizationServer<Person,Connection> vv =
                new BasicVisualizationServer<Person,Connection>(layout);
        vv.setPreferredSize(new Dimension(950,950)); //Sets the viewing area size
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        JFrame frame = new JFrame("Simple Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }
}

