package models;

public class SubTask extends Task {
    private int epicId;
    private final Tasks type = Tasks.SUBTASK;

    public SubTask(String name, String details, Epic epic) {
        super(name, details);
        this.epicId = epic.getId();
    }

    public SubTask(SubTask subTask) {
        super(subTask);
        this.epicId = subTask.getEpicId();
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Epic epic) {
        this.epicId = epic.getId();

    }

    @Override
    public String toString() {
        return super.toString() + epicId;
    }
}
