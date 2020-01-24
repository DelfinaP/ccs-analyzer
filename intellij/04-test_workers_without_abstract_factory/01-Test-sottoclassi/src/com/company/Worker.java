package com.company;

public abstract class Worker {
    public String title;
    public String name;
    public String subjectPronoun;
    public String objectPronoun;
    
    public void presentation() {
        System.out.println("This is " + name + ". " + subjectPronoun + "'s a " + title + ". We welcome " + objectPronoun + ".");
    } 
}
