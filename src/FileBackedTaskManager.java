import models.Epic;
import models.SubTask;
import models.Task;

import java.io.File;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final String filename;

    public FileBackedTaskManager(String filename) {
        super();
        this.filename = filename;
    }

    private void save() {
        //todo добавить сохранение состояния
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        //todo добавить метод для загрузки сосотяния
        return new FileBackedTaskManager(file.getName());
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addTask(SubTask subTask) {
        super.addTask(subTask);
        save();
    }

    @Override
    public void addTask(Epic epic) {
        super.addTask(epic);
        save();
    }

    @Override
    public void deleteSubTask(int subTaskId) {
        super.deleteSubTask(subTaskId);
        save();
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void clearTasks(){
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics(){
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubTasks(){
        super.clearSubTasks();
        save();
    }

    @Override
    public void update(Epic epic) {
        super.update(epic);
        save();
    }

    @Override
    public void update(Task task) {
        super.update(task);
        save();
    }

    @Override
    public void update(SubTask subTask) {
        super.update(subTask);
        save();
    }
}
