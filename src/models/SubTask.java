package models;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String details, Epic epic) {
        super(name, details);
        this.epicId = epic.getId();
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Epic epic) {
        this.epicId = epic.getId();

    }
}
