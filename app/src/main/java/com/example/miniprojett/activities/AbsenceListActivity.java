package com.example.miniprojett.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.miniprojett.R;
import com.example.miniprojett.database.DatabaseHelper;
import com.example.miniprojett.models.AbsenceModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AbsenceListActivity extends AppCompatActivity {

    private ArrayList<AbsenceModel> absencesList;
    private ArrayAdapter<String> adapter;
    private String studentId;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_list);

        // Récupérer l'ID de l'étudiant envoyé depuis l'activité précédente
        studentId = getIntent().getStringExtra("studentId");

        // Initialiser la base de données
        databaseHelper = new DatabaseHelper(this);

        // Récupérer la liste des absences de l'étudiant depuis la base de données
        absencesList = databaseHelper.getAbsencesByStudent(studentId);

        // Créer une liste de chaînes contenant les informations sur les absences
        ArrayList<String> absencesInfoList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        for (AbsenceModel absence : absencesList) {
            // Formater la date de l'absence dans le format souhaité
            String formattedDate = dateFormat.format(absence.getDate());
            String absenceInfo = "Date : " + formattedDate + " - " + (absence.isJustified() ? "Justifiée" : "Non justifiée");
            absencesInfoList.add(absenceInfo);
        }

        // Afficher la liste des absences dans un ListView
        ListView listViewAbsences = findViewById(R.id.listViewAbsences);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, absencesInfoList);
        listViewAbsences.setAdapter(adapter);

        // Ajouter un écouteur de long clic sur les éléments de la liste
        listViewAbsences.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AbsenceModel selectedAbsence = absencesList.get(position);
                showEditAbsenceDialog(selectedAbsence);
                return true;
            }
        });
    }

    private void showEditAbsenceDialog(final AbsenceModel absence) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier l'absence");

        // Créer un layout personnalisé pour la boîte de dialogue
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_absence, null);
        builder.setView(dialogView);

        // Récupérer les vues du layout
        final EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        final EditText editTextTime = dialogView.findViewById(R.id.editTextTime);
        final EditText editTextJustified = dialogView.findViewById(R.id.editTextJustified);

        // Pré-remplir les champs avec les valeurs actuelles de l'absence
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        editTextDate.setText(dateFormat.format(absence.getDate()));
        editTextTime.setText(timeFormat.format(absence.getDate()));
        editTextJustified.setText(absence.isJustified() ? "Justifiée" : "Non justifiée");

        builder.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Récupérer les nouvelles valeurs saisies par l'utilisateur
                String newDateStr = editTextDate.getText().toString();
                String newTimeStr = editTextTime.getText().toString();
                String newJustifiedStr = editTextJustified.getText().toString();

                // Convertir la date et l'heure en objet Date
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                Date newDate = null;
                try {
                    newDate = dateTimeFormat.parse(newDateStr + " " + newTimeStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Convertir le statut de justification en boolean
                boolean newJustified = newJustifiedStr.equalsIgnoreCase("Justifiée");

                // Mettre à jour l'absence dans la base de données
                updateAbsenceInDatabase(absence.getId(), newJustified);

                // Mettre à jour l'affichage de la liste des absences
                updateAbsenceListView();
            }
        });

        builder.setNegativeButton("Annuler", null);

        // Afficher la boîte de dialogue
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateAbsenceInDatabase(int absenceId, boolean newJustified) {
        databaseHelper.updateAbsence(absenceId, newJustified);
        Toast.makeText(this, "Absence mise à jour avec succès.", Toast.LENGTH_SHORT).show();
    }

    private void updateAbsenceListView() {
        absencesList.clear();
        absencesList.addAll(databaseHelper.getAbsencesByStudent(studentId));

        ArrayList<String> absencesInfoList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        for (AbsenceModel absence : absencesList) {
            String formattedDate = dateFormat.format(absence.getDate());
            String absenceInfo = "Date : " + formattedDate + " - " + (absence.isJustified() ? "Justifiée" : "Non justifiée");
            absencesInfoList.add(absenceInfo);
        }

        adapter.clear();
        adapter.addAll(absencesInfoList);
        adapter.notifyDataSetChanged();
    }
}
