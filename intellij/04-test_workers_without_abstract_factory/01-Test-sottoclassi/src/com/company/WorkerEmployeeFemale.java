package com.company;

public class WorkerEmployeeFemale extends WorkerEmployee {
    public WorkerEmployeeFemale(String name) {
        subjectPronoun = "she";
        objectPronoun = "her";
        this.name = name;
    }
}