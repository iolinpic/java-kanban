package models;

public class SubTask extends Task {
    private Epic parent;

    public SubTask(int id, String name, String details, Epic parent) {
        super(id, name, details);
        this.parent = parent;
        parent.addSubTask(this);
    }

    public Epic getParent() {
        return parent;
    }

    public void setParent(Epic parent) {
        this.parent.removeSubTask(this);
        this.parent = parent;
        this.parent.addSubTask(this);
    }

    /**
     * Метод для вызова перед удалением, убираем ссылку из parent сущности
     */
    public void onBeforeDelete() {
        parent.removeSubTask(this);
    }
}
