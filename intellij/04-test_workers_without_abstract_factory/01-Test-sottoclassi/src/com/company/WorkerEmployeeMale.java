package com.company;

public class WorkerEmployeeMale extends WorkerEmployee {
    public WorkerEmployeeMale(String name) {
        subjectPronoun = "he";
        objectPronoun = "him";
        this.name = name;
    }
}
