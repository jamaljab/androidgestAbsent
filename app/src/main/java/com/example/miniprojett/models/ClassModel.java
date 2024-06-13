package com.example.miniprojett.models;


public class ClassModel {
    private String className;
    private String classSubject;

    public ClassModel(String className, String classSubject) {
        this.className = className;
        this.classSubject = classSubject;
    }

    public String getClassName() {
        return className;
    }

    public String getClassSubject() {
        return classSubject;
    }


    public String getclassName() {
        return this.className;
    }
}
