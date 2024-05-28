package models;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<SubTask> subTasks;

    public Epic(String name, String details) {
        super(name, details);
        subTasks = new ArrayList<>();
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
        updateStatus();
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask);
        updateStatus();
    }

    /**
     * Функция для обновления статуса в случае изменения сабтасков
     */
    public void updateStatus() {
        if (isStatus(TaskStatus.NEW)) {
            setStatus(TaskStatus.NEW);
            return;
        }
        if (isStatus(TaskStatus.DONE)) {
            setStatus(TaskStatus.DONE);
            return;
        }
        setStatus(TaskStatus.IN_PROGRESS);
    }

    /**
     * Служебная функция для проверки что у всех сабтасков статус равен переданному
     *
     * @param status
     * @return
     */
    private boolean isStatus(TaskStatus status) {
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() != status) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }
}
