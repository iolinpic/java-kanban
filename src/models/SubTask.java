package models;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String details, Epic epic) {
        super(name, details);
        this.epicId = epic.getId();
    }

    public SubTask(String name, String details, int epicId) {
        super(name, details);
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
