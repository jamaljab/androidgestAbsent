package com.example.miniprojett.models;

import java.util.Date;

public class AbsenceModel {
    private int id;
    private String studentId;
    private Date date;
    private boolean justified;

    public AbsenceModel(String studentId, Date date, boolean justified) {
        this.studentId = studentId;
        this.date = date;
        this.justified = justified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isJustified() {
        return justified;
    }

    public void setJustified(boolean justified) {
        this.justified = justified;
    }
}
