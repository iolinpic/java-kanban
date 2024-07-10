package models;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTasks;

    public Epic(String name, String details) {
        super(name, details);
        subTasks = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic);
        subTasks = new ArrayList<>(epic.subTasks);
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask.getId());
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask.getId());
    }

    public ArrayList<Integer> getSubTasks() {
        return subTasks;
    }
}
