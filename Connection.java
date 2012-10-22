package com.salathegroup.socialcontagion;

public class Connection {
    private int edgeType;
    private String rewire;
    private Person source;
    private Person destination;
    private int id;
    public static final int BASIC = 1;
    public static final int SOCIAL = 2;

    public Connection(int id, Person source, Person destination, int edgeType) {
        this.id = id;
        this.destination = destination;
        this.source = source;
        this.edgeType = edgeType;
        this.rewire = Integer.toString(0);
    }

    public boolean isSOCIAL() {
        return this.edgeType==Connection.SOCIAL;
    }

    public boolean isBASIC() {
        return this.edgeType==Connection.BASIC;
    }

    public void setEdgeType(int edgeType) {
        this.edgeType = edgeType;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setRewire() {
        this.rewire = Integer.toString(1);
    }

    public String getRewire() {
        return this.rewire;
    }

    public int getID() {
        return this.id;
    }

    public Person getDestination() {
        return this.destination;
    }

    public Person getSource() {
        return this.source;
    }

    public int getEdgeType() {
        return this.edgeType;
    }
}
