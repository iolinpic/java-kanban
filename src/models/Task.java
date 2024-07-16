package models;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String details;
    private TaskStatus status;


    public Task(String name, String details) {
        this.id = 0;
        this.name = name;
        this.details = details;
        this.status = TaskStatus.NEW;
    }

    //конструктор для упрощения тестирования
    public Task(String name, String details, int id) {
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
        return id +
                "," +
                name +
                "," +
                status +
                "," +
                details +
                ",";
    }

    public void setId(int id) {
        this.id = id;
    }
}
