package com.example.miniprojett.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniprojett.R;
import com.example.miniprojett.models.StudentModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class StudentDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        // Récupérer l'objet StudentModel envoyé depuis l'activité précédente
        StudentModel student = getIntent().getParcelableExtra("student");

// Afficher les informations de l'étudiant dans les TextViews
        TextView textViewFirstName = findViewById(R.id.textViewFirstName);
        TextView textViewLastName = findViewById(R.id.textViewLastName);
        TextView textViewCNE = findViewById(R.id.textViewCNE);
        TextView textViewDOB = findViewById(R.id.textViewDOB);
        TextView textViewClassName = findViewById(R.id.textViewClassName);
        TextView textViewAbsenceCount = findViewById(R.id.textViewAbsenceCount); // Ajout de TextView pour le nombre d'absences
        Button buttonViewAbsences = findViewById(R.id.buttonViewAbsences); // Bouton pour afficher les absences

        textViewFirstName.setText("Prénom : " + student.getPrenom());
        textViewLastName.setText("Nom : " + student.getNom());
        textViewCNE.setText("CNE : " + student.getCne());

// Formater la date de naissance dans le format souhaité
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String formattedDOB = dateFormat.format(student.getDateNaissance());
        textViewDOB.setText("Date de naissance : " + formattedDOB);

        textViewClassName.setText("Classe : " + student.getClassName());

// Afficher le nombre d'absences
        textViewAbsenceCount.setText("Nombre d'absences non justifiees : " + student.getAbsenceCount());

// Définir un écouteur d'événements pour le bouton d'affichage des absences
        buttonViewAbsences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer une nouvelle activité pour afficher la liste des absences de l'étudiant
                Intent intent = new Intent(StudentDetailActivity.this, AbsenceListActivity.class);
                intent.putExtra("studentId", student.getCne()); // Envoyer l'ID de l'étudiant à l'activité suivante
                startActivity(intent);
            }
        });

    }
}
