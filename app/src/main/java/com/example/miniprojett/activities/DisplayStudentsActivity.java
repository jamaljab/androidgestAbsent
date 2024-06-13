package com.example.miniprojett.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniprojett.R;
import com.example.miniprojett.database.DatabaseHelper;
import com.example.miniprojett.models.StudentModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DisplayStudentsActivity extends AppCompatActivity implements StudentAdapter.OnItemClickListener, StudentAdapter.OnLongClickListener {

    private RecyclerView recyclerViewStudents;
    private DatabaseHelper dbHelper;
    private StudentAdapter studentAdapter;
    private TextView textViewClassName;
    private Button buttonCreateStudent;
    private Button buttonRetour;
    private Button buttonImporter;
    private Button buttonMettreAbsence;
    private ArrayList<StudentModel> studentsList; // Déclaration de la variable ici
    private static final int FILE_PICK_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_students);

        // Initialisations des vues
        buttonMettreAbsence = findViewById(R.id.buttonMettreAbsence);

        buttonRetour = findViewById(R.id.buttonroutour);
        buttonImporter = findViewById(R.id.buttonimporter);
        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        textViewClassName = findViewById(R.id.textViewClassName);
        buttonCreateStudent = findViewById(R.id.buttonCreateStudent);
        dbHelper = new DatabaseHelper(this);

        // Récupération du nom de la classe depuis l'intent
        String className = getIntent().getStringExtra("className");
        textViewClassName.setText("Nom de la classe : " + className);

        // Récupération de la liste des étudiants de la classe
        studentsList = dbHelper.getStudentsByClassName(className); // Initialisation de la liste ici

        // Configuration de l'adaptateur pour la liste des étudiants
        if (!studentsList.isEmpty()) {
            studentAdapter = new StudentAdapter(studentsList, this, this);
            recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewStudents.setAdapter(studentAdapter);
        }

        // Gestionnaire de clic pour le bouton d'importation
        buttonImporter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importStudentsFromFile();
            }
        });

        // Gestionnaire de clic pour le bouton de création d'étudiant
        buttonCreateStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = textViewClassName.getText().toString();
                className = className.replace("Nom de la classe : ", "");
                Intent intent = new Intent(DisplayStudentsActivity.this, CreateStudentActivity.class);
                intent.putExtra("className", className);
                startActivity(intent);
            }
        });

        // Gestionnaire de clic pour le bouton de retour
        buttonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayStudentsActivity.this, DisplayClassesActivity.class);
                startActivity(intent);
            }
        });

        buttonMettreAbsence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAbsenceDialog(className);
            }
        });
    }

    // Méthode pour importer les étudiants depuis un fichier
    private void importStudentsFromFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Sélectionner tous les types de fichiers
        startActivityForResult(intent, FILE_PICK_REQUEST_CODE);
    }

    // Méthode pour gérer le résultat de la sélection du fichier
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                importStudentsFromFile(uri);
            }
        }
    }

    // Méthode pour importer les étudiants depuis le fichier sélectionné
    private void importStudentsFromFile(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String className = null;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String firstName = parts[0].trim();
                    String lastName = parts[1].trim();
                    String cne = parts[2].trim();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dob = dateFormat.parse(parts[3].trim());
                    className = parts[4].trim();

                    StudentModel student = new StudentModel(firstName, lastName, cne, dob, className);
                    dbHelper.addStudent(student);
                    if (studentAdapter != null) {
                        studentAdapter.addStudent(student);
                    }
                }
            }
            reader.close();
            inputStream.close();
            Toast.makeText(this, "Étudiants importés avec succès", Toast.LENGTH_SHORT).show();

            // Actualiser l'adaptateur après l'importation
            studentsList = dbHelper.getStudentsByClassName(className); // Réinitialisation de la liste ici
            if (!studentsList.isEmpty()) {
                studentAdapter = new StudentAdapter(studentsList, this, this);
                recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
                recyclerViewStudents.setAdapter(studentAdapter);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de l'importation des étudiants", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(StudentModel student) {
        Intent intent = new Intent(DisplayStudentsActivity.this, StudentDetailActivity.class);
        intent.putExtra("student", student);
        startActivity(intent);
    }

    @Override
    public void onLongClick(final StudentModel studentModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier ou supprimer l'étudiant")
                .setItems(new CharSequence[]{"Modifier", "Supprimer"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showEditDialog(studentModel);
                                break;
                            case 1:
                                dbHelper.deleteStudent(studentModel);
                                studentsList.remove(studentModel); // Supprimer l'étudiant de la liste des étudiants affichés
                                if (studentAdapter != null) {
                                    studentAdapter.notifyDataSetChanged(); // Actualiser l'adaptateur après la suppression
                                }
                                break;
                        }
                    }
                })
                .show();
    }

    private void showEditDialog(final StudentModel studentModel) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_student, null);

        final EditText editTextFirstName = dialogView.findViewById(R.id.editTextFirstName);
        final EditText editTextLastName = dialogView.findViewById(R.id.editTextLastName);
        final EditText editTextCNE = dialogView.findViewById(R.id.editTextCNE);
        final EditText editTextDOB = dialogView.findViewById(R.id.editTextDOB);
        final EditText editTextClassName = dialogView.findViewById(R.id.editTextClassName);

        editTextFirstName.setText(studentModel.getPrenom());
        editTextLastName.setText(studentModel.getNom());
        editTextCNE.setText(studentModel.getCne());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = dateFormat.format(studentModel.getDateNaissance());
        editTextDOB.setText(formattedDate);
        editTextClassName.setText(studentModel.getClassName());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Modifier l'étudiant")
                .setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newFirstName = editTextFirstName.getText().toString().trim();
                        String newLastName = editTextLastName.getText().toString().trim();
                        String newCNE = editTextCNE.getText().toString().trim();
                        String newDOBText = editTextDOB.getText().toString().trim();
                        String newClassName = editTextClassName.getText().toString().trim();

                        if (!newFirstName.isEmpty() && !newLastName.isEmpty() && !newCNE.isEmpty() && !newDOBText.isEmpty() && !newClassName.isEmpty()) {
                            Date newDOB = null;
                            try {
                                newDOB = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(newDOBText);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (newDOB != null) {
                                boolean isUpdated = dbHelper.updateStudent(studentModel, newFirstName, newLastName, newCNE, newDOBText, newClassName);
                                if (isUpdated) {
                                    studentsList = dbHelper.getStudentsByClassName(newClassName);
                                    if (!studentsList.isEmpty()) {
                                        studentAdapter = new StudentAdapter(studentsList, DisplayStudentsActivity.this, DisplayStudentsActivity.this);
                                        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(DisplayStudentsActivity.this));
                                        recyclerViewStudents.setAdapter(studentAdapter);
                                    }
                                } else {
                                    Toast.makeText(DisplayStudentsActivity.this, "Erreur lors de la mise à jour de l'étudiant", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(DisplayStudentsActivity.this, "Format de date invalide", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DisplayStudentsActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss(); // Fermer le dialogue après l'enregistrement
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void showAbsenceDialog(final String className) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sélectionner les étudiants absents");

        // Créer une liste de noms d'étudiants à partir de la liste d'étudiants actuelle
        ArrayList<String> studentNames = new ArrayList<>();
        for (StudentModel student : studentsList) {
            studentNames.add(student.getPrenom() + " " + student.getNom());
        }

        // Convertir la liste en tableau de chaînes
        final String[] studentNamesArray = studentNames.toArray(new String[0]);

        // Suivi des éléments sélectionnés
        final boolean[] selectedStudents = new boolean[studentNamesArray.length];

        builder.setMultiChoiceItems(studentNamesArray, selectedStudents, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // Mettre à jour le tableau des étudiants sélectionnés
                selectedStudents[which] = isChecked;
            }
        });

        builder.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Traitement pour enregistrer les absences
                for (int i = 0; i < selectedStudents.length; i++) {
                    if (selectedStudents[i]) {
                        // Marquer l'étudiant correspondant comme absent
                        StudentModel student = studentsList.get(i);
                        dbHelper.addAbsence(student.getCne(), false); // Ajouter l'absence à la base de données
                        dbHelper.incrementAbsenceCount(student.getCne()); // Incrémenter le nombre d'absences dans la table des étudiants
                    }
                }
                // Mettre à jour la liste des étudiants affichés
                studentsList = dbHelper.getStudentsByClassName(className);
                if (!studentsList.isEmpty()) {
                    studentAdapter = new StudentAdapter(studentsList, DisplayStudentsActivity.this, DisplayStudentsActivity.this);
                    recyclerViewStudents.setLayoutManager(new LinearLayoutManager(DisplayStudentsActivity.this));
                    recyclerViewStudents.setAdapter(studentAdapter);
                }
                // Afficher un message de succès
                Toast.makeText(DisplayStudentsActivity.this, "Absences enregistrées avec succès", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", null);

        builder.create().show();
    }



}
