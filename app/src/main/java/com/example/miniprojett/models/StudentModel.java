package com.example.miniprojett.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class StudentModel implements Parcelable {
    private String nom;
    private String prenom;
    private String cne;
    private Date dateNaissance;
    private String className;
    private int absenceCount; // Nouvel attribut pour le nombre d'absences

    public StudentModel(String nom, String prenom, String cne, Date dateNaissance, String className) {
        this.nom = nom;
        this.prenom = prenom;
        this.cne = cne;
        this.dateNaissance = dateNaissance;
        this.className = className;
        this.absenceCount = 0; // Initialisé à 0 par défaut
    }

    protected StudentModel(Parcel in) {
        nom = in.readString();
        prenom = in.readString();
        cne = in.readString();
        dateNaissance = new Date(in.readLong());
        className = in.readString();
        absenceCount = in.readInt(); // Lecture du nombre d'absences depuis le Parcel
    }

    public static final Creator<StudentModel> CREATOR = new Creator<StudentModel>() {
        @Override
        public StudentModel createFromParcel(Parcel in) {
            return new StudentModel(in);
        }

        @Override
        public StudentModel[] newArray(int size) {
            return new StudentModel[size];
        }
    };

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getCne() {
        return cne;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public String getClassName() {
        return className;
    }

    public int getAbsenceCount() {
        return absenceCount;
    }

    public void setAbsenceCount(int absenceCount) {
        this.absenceCount = absenceCount;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nom);
        dest.writeString(prenom);
        dest.writeString(cne);
        dest.writeLong(dateNaissance.getTime());
        dest.writeString(className);
        dest.writeInt(absenceCount); // Écriture du nombre d'absences dans le Parcel
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
