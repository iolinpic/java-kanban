package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task implements Comparable<Task> {
    public static final DateTimeFormatter SERIALISATION_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private int id;
    private String name;
    private String details;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;


    public Task(String name, String details) {
        this.id = 0;
        this.duration = Duration.ZERO;
        this.startTime = LocalDateTime.now();
        this.name = name;
        this.details = details;
        this.status = TaskStatus.NEW;
    }

    public Task(String name, String details, TaskStatus status) {
        this.id = 0;
        this.duration = Duration.ZERO;
        this.startTime = LocalDateTime.now();
        this.name = name;
        this.details = details;
        this.status = status;
    }

    public Task(String name, String details, TaskStatus status,
                Duration duration, LocalDateTime startTime) {
        this.id = 0;
        this.duration = duration;
        this.startTime = startTime;
        this.name = name;
        this.details = details;
        this.status = status;
    }

    //конструктор для упрощения тестирования
    public Task(String name, String details, int id) {
        this.duration = Duration.ZERO;
        this.startTime = LocalDateTime.now();
        this.id = id;
        this.name = name;
        this.details = details;
        this.status = TaskStatus.NEW;
    }

    public Task(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.details = task.getDetails();
        this.status = task.getStatus();
        this.duration = task.getDuration();
        this.startTime = task.getStartTime();
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        String startTimeStr = "null";
        if (startTime != null) {
            startTimeStr = startTime.format(SERIALISATION_FORMATTER);
        }
        return id +
                "," +
                name +
                "," +
                status +
                "," +
                details +
                "," +
                duration.toMinutes() +
                "," +
                startTimeStr +
                ",";

    }

    public void setId(int id) {
        this.id = id;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public static int compareByStartTime(Task task1, Task task2) {
        return task1.getStartTime().compareTo(task2.getStartTime());
    }

    public static int compareByEndTime(Task task1, Task task2) {
        return task1.getEndTime().compareTo(task2.getEndTime());
    }

    @Override
    public int compareTo(Task o) {
        return startTime.compareTo(o.getStartTime());
    }
}
