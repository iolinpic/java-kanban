package models;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String name, String details, int epicId) {
        super(name, details);
        this.epicId = epicId;
    }

    public SubTask(String name, String details, TaskStatus status, int epicId) {
        super(name, details, status);
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
