package com.example.miniprojett.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.miniprojett.models.AbsenceModel;
import com.example.miniprojett.models.ClassModel;
import com.example.miniprojett.models.StudentModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "app_database.db";
    private static final int DATABASE_VERSION = 1;


    private static final String TABLE_CLASSES = "classes";
    private static final String COLUMN_CLASS_NAME = "class_name";
    private static final String COLUMN_CLASS_SUBJECT = "class_subject";
    private static final String CREATE_TABLE_CLASSES = "CREATE TABLE " + TABLE_CLASSES + " (" +
            COLUMN_CLASS_NAME + " TEXT," +
            COLUMN_CLASS_SUBJECT + " TEXT)";





    private static final String TABLE_STUDENTS = "students";
    private static final String COLUMN_STUDENT_FIRST_NAME = "first_name";
    private static final String COLUMN_STUDENT_LAST_NAME = "last_name";
    private static final String COLUMN_STUDENT_CNE = "cne";
    private static final String COLUMN_STUDENT_DOB = "dob";
    private static final String COLUMN_STUDENT_CLASS_NAME = "class_name";
    private static final String COLUMN_STUDENT_ABSENCE_COUNT = "absence_count"; // Champ pour le nombre d'absences

    private static final String CREATE_TABLE_STUDENTS = "CREATE TABLE " + TABLE_STUDENTS + " (" +
            COLUMN_STUDENT_FIRST_NAME + " TEXT," +
            COLUMN_STUDENT_LAST_NAME + " TEXT," +
            COLUMN_STUDENT_CNE + " TEXT PRIMARY KEY," +
            COLUMN_STUDENT_DOB + " TEXT," +
            COLUMN_STUDENT_CLASS_NAME + " TEXT," +
            COLUMN_STUDENT_ABSENCE_COUNT + " INTEGER DEFAULT 0)"; // Par défaut, absence_count est 0





    private static final String TABLE_ABSENCES = "absences";
    private static final String COLUMN_ABSENCE_ID = "id";
    private static final String COLUMN_ABSENCE_STUDENT_ID = "student_id";
    private static final String COLUMN_ABSENCE_DATE = "date";
    private static final String COLUMN_ABSENCE_JUSTIFIED = "justified";

    private static final String CREATE_TABLE_ABSENCES = "CREATE TABLE " + TABLE_ABSENCES + " (" +
            COLUMN_ABSENCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_ABSENCE_STUDENT_ID + " TEXT," +
            COLUMN_ABSENCE_DATE + " DATETIME," + // Utilisation de DATETIME pour stocker la date et l'heure
            COLUMN_ABSENCE_JUSTIFIED + " INTEGER)";






    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLASSES);
        db.execSQL(CREATE_TABLE_STUDENTS);
        db.execSQL(CREATE_TABLE_ABSENCES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Mettez à jour la base de données si nécessaire
    }





    public void addClass(String className, String classSubject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_NAME, className);
        values.put(COLUMN_CLASS_SUBJECT, classSubject);
        db.insert(TABLE_CLASSES, null, values);
        db.close();
    }

    public void addStudent(StudentModel student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_FIRST_NAME, student.getPrenom());
        values.put(COLUMN_STUDENT_LAST_NAME, student.getNom());
        values.put(COLUMN_STUDENT_CNE, student.getCne());
        values.put(COLUMN_STUDENT_DOB, new SimpleDateFormat("dd/MM/yyyy").format(student.getDateNaissance()));
        values.put(COLUMN_STUDENT_CLASS_NAME, student.getClassName());
        values.put(COLUMN_STUDENT_ABSENCE_COUNT, student.getAbsenceCount()); // Ajout du champ pour le nombre d'absences
        db.insert(TABLE_STUDENTS, null, values);
        db.close();
    }
    public void addAbsence(String studentId, boolean justified) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ABSENCE_STUDENT_ID, studentId);

        // Obtenir la date et l'heure actuelles
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = dateFormat.format(new Date());

        // Insérer la date et l'heure actuelles dans la base de données
        values.put(COLUMN_ABSENCE_DATE, currentDateAndTime);
        values.put(COLUMN_ABSENCE_JUSTIFIED, justified ? 1 : 0);
        db.insert(TABLE_ABSENCES, null, values);
        db.close();
    }




    public void updateClass(ClassModel classToUpdate, String newClassName, String newClassSubject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_NAME, newClassName);
        values.put(COLUMN_CLASS_SUBJECT, newClassSubject);
        db.update(TABLE_CLASSES, values, COLUMN_CLASS_NAME + " = ?", new String[]{classToUpdate.getClassName()});
        db.close();
    }

    public boolean updateStudent(StudentModel studentToUpdate, String newFirstName, String newLastName, String newCNE, String newDOB, String newClassName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_FIRST_NAME, newFirstName);
        values.put(COLUMN_STUDENT_LAST_NAME, newLastName);
        values.put(COLUMN_STUDENT_CNE, newCNE);
        values.put(COLUMN_STUDENT_DOB, newDOB);
        values.put(COLUMN_STUDENT_CLASS_NAME, newClassName);
        int rowsAffected = db.update(TABLE_STUDENTS, values, COLUMN_STUDENT_CNE + " = ?", new String[]{studentToUpdate.getCne()});
        db.close();
        return rowsAffected > 0;
    }






    public void deleteClass(ClassModel classToDelete) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASSES, COLUMN_CLASS_NAME + " = ?", new String[]{classToDelete.getClassName()});
        db.close();
    }

    public void deleteStudent(StudentModel studentToDelete) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENTS, COLUMN_STUDENT_CNE + " = ?", new String[]{studentToDelete.getCne()});
        db.close();
    }

    public ArrayList<ClassModel> getAllClasses() {
        ArrayList<ClassModel> classesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLASSES,
                new String[]{COLUMN_CLASS_NAME, COLUMN_CLASS_SUBJECT},
                null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String className = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_NAME));
                String classSubject = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_SUBJECT));
                ClassModel classModel = new ClassModel(className, classSubject);
                classesList.add(classModel);
            }
            cursor.close();
        }
        db.close();
        return classesList;
    }


    public ArrayList<StudentModel> getStudentsByClassName(String className) {
        ArrayList<StudentModel> studentsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_STUDENT_CLASS_NAME + " = ?";
        String[] selectionArgs = {className};
        Cursor cursor = db.query(TABLE_STUDENTS,
                new String[]{COLUMN_STUDENT_FIRST_NAME, COLUMN_STUDENT_LAST_NAME, COLUMN_STUDENT_CNE, COLUMN_STUDENT_DOB, COLUMN_STUDENT_CLASS_NAME, COLUMN_STUDENT_ABSENCE_COUNT}, // Ajout du champ pour le nombre d'absences
                selection, selectionArgs, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_LAST_NAME));
                String cne = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_CNE));
                // Convertir la date de naissance de la base de données en objet Date
                String dobString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_DOB));
                Date dob = null;
                try {
                    dob = new SimpleDateFormat("dd/MM/yyyy").parse(dobString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int absenceCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ABSENCE_COUNT)); // Récupération du nombre d'absences
                StudentModel studentModel = new StudentModel(firstName, lastName, cne, dob, className);
                studentModel.setAbsenceCount(absenceCount); // Définition du nombre d'absences
                studentsList.add(studentModel);
            }
            cursor.close();
        }
        db.close();
        return studentsList;
    }

    public ArrayList<StudentModel> getAllStudents() {
        ArrayList<StudentModel> studentsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS,
                new String[]{COLUMN_STUDENT_FIRST_NAME, COLUMN_STUDENT_LAST_NAME, COLUMN_STUDENT_CNE, COLUMN_STUDENT_DOB, COLUMN_STUDENT_CLASS_NAME, COLUMN_STUDENT_ABSENCE_COUNT}, // Ajout du champ pour le nombre d'absences
                null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_LAST_NAME));
                String cne = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_CNE));
                String className = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_CLASS_NAME));
                // Convertir la date de naissance de la base de données en objet Date
                String dobString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_DOB));
                Date dob = null;
                try {
                    dob = new SimpleDateFormat("dd/MM/yyyy").parse(dobString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int absenceCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ABSENCE_COUNT)); // Récupération du nombre d'absences
                StudentModel studentModel = new StudentModel(firstName, lastName, cne, dob, className);
                studentModel.setAbsenceCount(absenceCount); // Définition du nombre d'absences
                studentsList.add(studentModel);
            }
            cursor.close();
        }
        db.close();
        return studentsList;
    }



    public ArrayList<AbsenceModel> getAbsencesByStudent(String studentId) {
        ArrayList<AbsenceModel> absencesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_ABSENCE_STUDENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(studentId)};
        Cursor cursor = db.query(TABLE_ABSENCES,
                new String[]{COLUMN_ABSENCE_ID, COLUMN_ABSENCE_STUDENT_ID, COLUMN_ABSENCE_DATE, COLUMN_ABSENCE_JUSTIFIED},
                selection, selectionArgs, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ABSENCE_ID));
                Date date = null;
                try {
                    date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ABSENCE_DATE)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                boolean justified = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ABSENCE_JUSTIFIED)) == 1;
                AbsenceModel absence = new AbsenceModel(studentId, date, justified);
                absencesList.add(absence);
            }
            cursor.close();
        }
        db.close();
        return absencesList;
    }


    public void incrementAbsenceCount(String cne) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String selection = COLUMN_STUDENT_CNE + " = ?";
        String[] selectionArgs = { cne };

        // Sélectionner le nombre d'absences actuel pour l'étudiant spécifié
        String[] projection = { COLUMN_STUDENT_ABSENCE_COUNT };
        Cursor cursor = db.query(TABLE_STUDENTS, projection, selection, selectionArgs, null, null, null);
        int currentAbsenceCount = 0;
        if (cursor != null && cursor.moveToFirst()) {
            currentAbsenceCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ABSENCE_COUNT));
            cursor.close();
        }

        // Incrémenter le nombre d'absences
        values.put(COLUMN_STUDENT_ABSENCE_COUNT, currentAbsenceCount + 1);

        // Mettre à jour le nombre d'absences dans la table des étudiants
        db.update(TABLE_STUDENTS, values, selection, selectionArgs);
        db.close();
    }

    public void updateAbsence(int absenceId, boolean newJustified) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ABSENCE_JUSTIFIED, newJustified ? 1 : 0);

        String selection = COLUMN_ABSENCE_ID + " = ?";
        String[] selectionArgs = { String.valueOf(absenceId) };

        db.update(TABLE_ABSENCES, values, selection, selectionArgs);
        db.close();
    }





}
