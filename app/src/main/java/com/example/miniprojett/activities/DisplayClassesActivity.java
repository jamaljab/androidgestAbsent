package com.example.miniprojett.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniprojett.R;
import com.example.miniprojett.database.DatabaseHelper;
import com.example.miniprojett.models.ClassModel;

import java.util.ArrayList;

public class DisplayClassesActivity extends AppCompatActivity implements ClassAdapter.OnLongClickListener, ClassAdapter.OnItemClickListener {

    private RecyclerView recyclerViewClasses;
    private DatabaseHelper dbHelper;
    private ClassAdapter classAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_classes);

        recyclerViewClasses = findViewById(R.id.recyclerViewClasses);
        dbHelper = new DatabaseHelper(this);

        Button buttonAddClass = findViewById(R.id.buttonCreateClass);
        Button buttonRefresh = findViewById(R.id.buttonRefresh);

        buttonAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvrir l'activité pour créer une classe
                Intent intent = new Intent(DisplayClassesActivity.this, CreateClassActivity.class);
                startActivity(intent);
            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Actualiser la liste des classes
                refreshPage();
            }
        });

        displayClasses();
    }

    private void displayClasses() {
        ArrayList<ClassModel> classesList = dbHelper.getAllClasses();

        if (classesList.isEmpty()) {
            Toast.makeText(this, "Aucune classe trouvée dans la base de données", Toast.LENGTH_SHORT).show();
        } else {
            classAdapter = new ClassAdapter(classesList);
            classAdapter.setOnLongClickListener(this);
            classAdapter.setOnItemClickListener(this); // Définir le gestionnaire de clic
            recyclerViewClasses.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewClasses.setAdapter(classAdapter);
        }
    }
    @Override
    public void onItemClick(ClassModel classModel) {
        // Ouvrir l'activité pour afficher les étudiants de cette classe
        Intent intent = new Intent(DisplayClassesActivity.this, DisplayStudentsActivity.class);
        intent.putExtra("className", classModel.getClassName());
        startActivity(intent);
    }

    @Override
    public void onLongClick(final ClassModel classModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier ou supprimer la classe")
                .setItems(new CharSequence[]{"Modifier", "Supprimer"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showEditDialog(classModel);
                                break;
                            case 1:
                                dbHelper.deleteClass(classModel);
                                refreshPage();
                                break;
                        }
                    }
                })
                .show();
    }






    private void showEditDialog(final ClassModel classModel) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_class, null);
        final EditText editTextClassName = dialogView.findViewById(R.id.editTextClassName);
        final EditText editTextClassSubject = dialogView.findViewById(R.id.editTextClassSubject);
        editTextClassName.setText(classModel.getClassName());
        editTextClassSubject.setText(classModel.getClassSubject());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Modifier la classe")
                .setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newClassName = editTextClassName.getText().toString().trim();
                        String newClassSubject = editTextClassSubject.getText().toString().trim();
                        if (!newClassName.isEmpty() && !newClassSubject.isEmpty()) {
                            dbHelper.updateClass(classModel, newClassName, newClassSubject);
                            refreshPage();
                        } else {
                            Toast.makeText(DisplayClassesActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }





    private void refreshPage() {
        ArrayList<ClassModel> refreshedClassesList = dbHelper.getAllClasses();

        if (refreshedClassesList.isEmpty()) {
            Toast.makeText(this, "Aucune classe trouvée dans la base de données", Toast.LENGTH_SHORT).show();
        } else {
            classAdapter = new ClassAdapter(refreshedClassesList);
            classAdapter.setOnLongClickListener(this);
            recyclerViewClasses.setAdapter(classAdapter);
        }
    }



}
