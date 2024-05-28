package models;

public class SubTask extends Task {
    private Epic epic;

    public SubTask(int id, String name, String details, Epic epic) {
        super(id, name, details);
        this.epic = epic;
        epic.addSubTask(this);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic.removeSubTask(this);
        this.epic = epic;
        this.epic.addSubTask(this);
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
        this.epic.updateStatus();
    }

    /**
     * Метод для вызова перед удалением, убираем ссылку из parent сущности
     */
    public void onBeforeDelete() {
        epic.removeSubTask(this);
    }
}
