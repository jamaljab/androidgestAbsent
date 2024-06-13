package com.example.miniprojett.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniprojett.R;
import com.example.miniprojett.database.DatabaseHelper;
import com.example.miniprojett.models.StudentModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateStudentActivity extends AppCompatActivity {

    private EditText editTextStudentFirstName, editTextStudentLastName, editTextStudentCNE, editTextStudentDOB, editTextClassName;
    private Button buttonAddStudent;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        editTextStudentFirstName = findViewById(R.id.editTextStudentFirstName);
        editTextStudentLastName = findViewById(R.id.editTextStudentLastName);
        editTextStudentCNE = findViewById(R.id.editTextStudentCNE);
        editTextStudentDOB = findViewById(R.id.editTextStudentDOB);
        editTextClassName = findViewById(R.id.editTextClassName);
        buttonAddStudent = findViewById(R.id.buttonAddStudent);

        dbHelper = new DatabaseHelper(this);

        buttonAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStudent();
            }
        });

        String className = getIntent().getStringExtra("className");

        // Utiliser le nom de la classe pour pré-remplir le champ "nom de la classe"
        editTextClassName.setText(className);
    }

    private void addStudent() {
        String firstName = editTextStudentFirstName.getText().toString().trim();
        String lastName = editTextStudentLastName.getText().toString().trim();
        String cne = editTextStudentCNE.getText().toString().trim();
        String dobString = editTextStudentDOB.getText().toString().trim();
        String className = editTextClassName.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || cne.isEmpty() || dobString.isEmpty() || className.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        Date dob;
        try {
            dob = new SimpleDateFormat("dd/MM/yyyy").parse(dobString);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Format de date incorrect (JJ/MM/AAAA)", Toast.LENGTH_SHORT).show();
            return;
        }

        StudentModel student = new StudentModel(firstName, lastName, cne, dob, className);
        dbHelper.addStudent(student);
        Toast.makeText(this, "Étudiant ajouté avec succès", Toast.LENGTH_SHORT).show();

        // Après avoir ajouté l'étudiant avec succès, démarrer DisplayStudentsActivity
        Intent intent = new Intent(CreateStudentActivity.this, DisplayStudentsActivity.class);
        intent.putExtra("className", className);
        startActivity(intent);
        finish();
    }
}
