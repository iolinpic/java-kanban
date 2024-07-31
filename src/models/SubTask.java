package models;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String name, String details, int epicId) {
        super(name, details);
        this.epicId = epicId;
    }

    public SubTask(String name, String details, TaskStatus status, Duration duration,
                   LocalDateTime startTime, int epicId) {
        super(name, details, status, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(SubTask subTask) {
        super(subTask);
        this.epicId = subTask.getEpicId();
    }


    public int getEpicId() {
        return epicId;
    }


    @Override
    public String toString() {
        return super.toString() + epicId;
    }
}
