package com.salathegroup.socialcontagion;


public class Connection {

    private int edgeType;
    private Person source;
    private Person destination;
    private int id;

    public static final int BASIC = 1;
    public static final int SOCIAL = 2;
    public static final int mixedSOCIAL = 3;


    public Connection(int id, Person source, Person destination, int edgeType) {
        this.id = id;
        this.destination = destination;
        this.source = source;
        this.edgeType = edgeType;
    }

    public boolean isSOCIAL() {
        return this.edgeType==Connection.SOCIAL;
    }

    public boolean isBASIC() {
        return this.edgeType==Connection.BASIC;
    }

    public boolean ismixedSOCIAL() {
        return this.edgeType==Connection.mixedSOCIAL;
    }

    public void setEdgeType(int edgeType) {
        this.edgeType = edgeType;
    }

    public void setID(int id) {
        this.id = id;
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
