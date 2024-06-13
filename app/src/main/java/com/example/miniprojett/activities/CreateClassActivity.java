package com.example.miniprojett.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.miniprojett.R;
import com.example.miniprojett.database.DatabaseHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CreateClassActivity extends AppCompatActivity {

    private EditText editTextClassName;
    private EditText editTextClassSubject;
    private Button buttonAddClass;
    private Button buttonDisplayClasses;
    private Button buttonImport;
    private DatabaseHelper dbHelper;

    private static final int FILE_PICK_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        editTextClassName = findViewById(R.id.editTextClassName);
        editTextClassSubject = findViewById(R.id.editTextClassSubject);
        buttonAddClass = findViewById(R.id.buttonAddClass);
        buttonDisplayClasses = findViewById(R.id.buttonDisplayClasses);
        buttonImport = findViewById(R.id.buttonImport);
        dbHelper = new DatabaseHelper(this);

        buttonAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClass();
            }
        });

        buttonDisplayClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDisplayClassesActivity();
            }
        });

        buttonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importClassesFromFile();
            }
        });
    }

    private void addClass() {
        String className = editTextClassName.getText().toString().trim();
        String classSubject = editTextClassSubject.getText().toString().trim();

        if (className.isEmpty() || classSubject.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.addClass(className, classSubject);

        Toast.makeText(this, "Classe ajoutée à la base de données", Toast.LENGTH_SHORT).show();
    }

    private void openDisplayClassesActivity() {
        Intent intent = new Intent(this, DisplayClassesActivity.class);
        startActivity(intent);
    }

    private void importClassesFromFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_PICK_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                importClassesFromFile(uri);
            }
        }
    }

    private void importClassesFromFile(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String className = parts[0].trim();
                    String classSubject = parts[1].trim();
                    dbHelper.addClass(className, classSubject);
                }
            }
            reader.close();
            inputStream.close();
            Toast.makeText(this, "Classes importées avec succès", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace(); // Affiche les détails de l'erreur dans la console Logcat
            Toast.makeText(this, "Erreur lors de l'importation des classes", Toast.LENGTH_SHORT).show();
        }
    }

}
