package models;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTasks;

    public Epic(String name, String details) {
        super(name, details);
        subTasks = new ArrayList<>();
    }

    public void addSubTask(Integer subTaskId) {
        subTasks.add(subTaskId);
    }

    public void removeSubTask(Integer subTaskId) {
        subTasks.remove(subTaskId);
    }

    public ArrayList<Integer> getSubTasks() {
        return subTasks;
    }
}
