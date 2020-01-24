package com.company;

public class Main {

    public static void main(String[] args) {
        Worker workerA = new WorkerEmployeeFemale("Alice");
        workerA.presentation();

        Worker workerB = new WorkerEmployeeMale("Bob");
        workerB.presentation();

        Worker workerC = new WorkerManagerFemale("Claude");
        workerC.presentation();

        Worker workerD = new WorkerManagerMale("David");
        workerD.presentation();
    }
}
