import models.Epic;
import models.SubTask;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, SubTask> subtasks;
    private final HashMap<Integer, Epic> epics;
    private int index;

    TaskManager() {
        index = 1;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addSubTask(SubTask subTask) {
        subtasks.put(subTask.getId(), subTask);
        //добавляем родителя
        epics.put(subTask.getEpic().getId(), subTask.getEpic());
    }

    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        //добавляем связанные сабтаски
        for (SubTask subTask : epic.getSubTasks()) {
            subtasks.put(subTask.getId(), subTask);
        }
    }

    public Task getTask(int index) {
        return tasks.get(index);
    }

    public SubTask getSubTask(int index) {
        return subtasks.get(index);
    }

    public Epic getEpic(int index) {
        return epics.get(index);
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subtasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubTasks() {
        for (SubTask subtask : subtasks.values()) {
            subtask.onBeforeDelete();
        }
        subtasks.clear();
    }

    public void clearEpics() {
        for (Epic epic : epics.values()) {
            onBeforeEpicDelete(epic);
        }
        epics.clear();
    }

    public void deleteTask(int index) {
        tasks.remove(index);
    }

    public void deleteSubTask(int index) {
        subtasks.get(index).onBeforeDelete();
        subtasks.remove(index);
    }

    public void deleteEpic(int index) {
        onBeforeEpicDelete(epics.get(index));
        epics.remove(index);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubTask(SubTask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    private void onBeforeEpicDelete(Epic epic) {
        if (epic == null) {
            return;
        }
        for (SubTask subtask : epic.getSubTasks()) {
            subtasks.remove(subtask.getId());
        }
    }

    public int getNextIndex() {
        return index++;
    }

    public ArrayList<SubTask> getEpicSubTasks(Epic epic) {
        return epic.getSubTasks();
    }
}
