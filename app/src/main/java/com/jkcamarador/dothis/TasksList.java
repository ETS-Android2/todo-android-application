package com.jkcamarador.dothis;

public class TasksList {

    String taskName, deadline, weight;
    int taskIcon;

    public TasksList(String taskName, String deadline, String weight, int taskIcon) {
        this.taskName = taskName;
        this.deadline = deadline;
        this.weight = weight;
        this.taskIcon = taskIcon;
    }
}
