package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTasks;
    private LocalDateTime endTime;

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Epic(String name, String details) {
        super(name, details);
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String details, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(name, details, status, duration, startTime);
        subTasks = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic);
        subTasks = new ArrayList<>(epic.subTasks);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask.getId());
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.removeIf(id -> id.equals(subTask.getId()));
//        subTasks.remove(subTask.getId());
    }

    public ArrayList<Integer> getSubTasks() {
        return subTasks;
    }


}
