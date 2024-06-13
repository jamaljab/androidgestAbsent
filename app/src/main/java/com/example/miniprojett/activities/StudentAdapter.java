package com.example.miniprojett.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniprojett.R;
import com.example.miniprojett.models.StudentModel;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private ArrayList<StudentModel> studentsList;
    private OnItemClickListener clickListener;
    private OnLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(StudentModel student);
    }

    public interface OnLongClickListener {
        void onLongClick(StudentModel student);
    }

    public StudentAdapter(ArrayList<StudentModel> studentsList, OnItemClickListener clickListener, OnLongClickListener longClickListener) {
        this.studentsList = studentsList;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        final StudentModel student = studentsList.get(position);
        holder.bind(student);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onItemClick(student);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickListener != null) {
                    longClickListener.onLongClick(student);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    public void addStudent(StudentModel student) {
        studentsList.add(student);
        notifyDataSetChanged();
    }

    public void removeStudent(StudentModel student) {
        studentsList.remove(student);
        notifyDataSetChanged();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewStudentName;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStudentName = itemView.findViewById(R.id.textViewStudentName);
        }

        public void bind(StudentModel student) {
            textViewStudentName.setText(student.getNom() + " " + student.getPrenom());
        }
    }
}
