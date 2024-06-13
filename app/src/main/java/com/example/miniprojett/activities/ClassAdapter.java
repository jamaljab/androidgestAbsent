package com.example.miniprojett.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniprojett.R;
import com.example.miniprojett.models.ClassModel;

import java.util.ArrayList;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private ArrayList<ClassModel> classesList;
    private OnLongClickListener longClickListener;

    public ClassAdapter(ArrayList<ClassModel> classesList) {
        this.classesList = classesList;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        final ClassModel classModel = classesList.get(position);
        holder.textViewClassName.setText(classModel.getClassName());
        holder.textViewClassSubject.setText(classModel.getClassSubject());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickListener != null) {
                    longClickListener.onLongClick(classModel);
                    return true;
                }
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(classModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return classesList.size();
    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        this.longClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(ClassModel classModel);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnLongClickListener {
        void onLongClick(ClassModel classModel);
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {

        TextView textViewClassName;
        TextView textViewClassSubject;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewClassName = itemView.findViewById(R.id.textViewClassName);
            textViewClassSubject = itemView.findViewById(R.id.textViewClassSubject);
        }
    }
}
