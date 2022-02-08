package com.jkcamarador.dothis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<TasksList> {

    public ListAdapter(Context context, ArrayList<TasksList> userArrayList){

        super(context,R.layout.list_item,userArrayList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        TasksList tasksList = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }

        ImageView taskIcon = convertView.findViewById(R.id.task_icon);
        TextView taskName = convertView.findViewById(R.id.task_name);
        TextView deadline = convertView.findViewById(R.id.task_deadline);
        TextView weight = convertView.findViewById(R.id.task_weight);

        taskIcon.setImageResource(tasksList.taskIcon);
        taskName.setText(tasksList.taskName);
        deadline.setText(tasksList.deadline);
        weight.setText(tasksList.weight);

        return convertView;
    }
}
