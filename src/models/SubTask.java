package models;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String details, Integer epicId) {
        super(name, details);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;

    }
}
