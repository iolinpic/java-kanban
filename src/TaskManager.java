import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        task.setId(getNextIndex());
        tasks.put(task.getId(), task);
    }

    public void addTask(Epic epic) {
        epic.setId(getNextIndex());
        epics.put(epic.getId(), epic);
    }

    public void addTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());
        if (epic == null) {
            return;
        }
        subTask.setId(getNextIndex());
        subtasks.put(subTask.getId(), subTask);
        epic.addSubTask(subTask.getId());
        updateEpicStatus(epic);
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

    /**
     * Список тасков
     */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Список сабтасков
     */
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    /**
     * Список эпиков
     */
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Удаление всех тасков
     */
    public void clearTasks() {
        tasks.clear();
    }

    /**
     * Удаление всех сабтасков
     */
    public void clearSubTasks() {
        for (SubTask subtask : subtasks.values()) {
            onBeforeSubtaskDelete(subtask);
        }
        subtasks.clear();
    }

    /**
     * Удаление всех эпиков
     */
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            onBeforeEpicDelete(epic);
        }
        epics.clear();
    }

    /**
     * Удаление таска
     */
    public void deleteTask(int index) {
        tasks.remove(index);
    }

    /**
     * Удаление сабтаска
     */
    public void deleteSubTask(int index) {
        onBeforeSubtaskDelete(subtasks.get(index));
        subtasks.remove(index);
    }

    /**
     * Удаление эпика
     */
    public void deleteEpic(int index) {
        onBeforeEpicDelete(epics.get(index));
        epics.remove(index);
    }

    /**
     * Обновление таска
     */
    public void update(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    /**
     * Обновление сабтаска
     */
    public void update(SubTask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Epic epic = epics.get(subtask.getEpicId());
            SubTask oldSubtask = subtasks.get(subtask.getId());
            if (!oldSubtask.getEpicId().equals(subtask.getEpicId())) {
                //если в старом сабтаске был другой епик
                Epic oldEpic = epics.get(oldSubtask.getEpicId());
                oldEpic.removeSubTask(oldSubtask.getId());
                updateEpicStatus(oldEpic);
                epic.addSubTask(subtask.getId());
            }
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(epic);
        }
    }

    /**
     * Обновление эпика
     */
    public void update(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());
            if (!Arrays.equals(epic.getSubTasks().toArray(), oldEpic.getSubTasks().toArray())) {
                //удаляем те сабтаски что были в старом но отсутствуют в новом
                for (Integer oldId : oldEpic.getSubTasks()) {
                    if (!epic.getSubTasks().contains(oldId)) {
                        subtasks.remove(oldId);
                    }
                }
                //по идее мы не можем переложить внутрь эпика чужие сабтаски, обновляя эпик
                //(тоесть у нас просто не может быть такой ситуации когда есть сабтаска без эпика)
            }
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic);
        }
    }

    /**
     * Выполняем перед удалением эпика (удаляем все сабтаски)
     */
    private void onBeforeEpicDelete(Epic epic) {
        if (epic == null) {
            return;
        }
        for (Integer subTaskId : epic.getSubTasks()) {
            subtasks.remove(subTaskId);
        }
    }

    /**
     * Выдаем следующий индекс + повышаем значение в учете
     */
    private int getNextIndex() {
        return index++;
    }

    /**
     * Возвращает список сабтасков переданного эпика
     */
    public ArrayList<SubTask> getEpicSubTasks(Epic epic) {
        ArrayList<Integer> subTaskIds = epic.getSubTasks();
        ArrayList<SubTask> result = new ArrayList<>(subTaskIds.size());
        for (Integer id : subTaskIds) {
            result.add(subtasks.get(id));
        }
        return result;
    }

    /**
     * Функция для вызова перед удалением сабтаска (удаляет связь с эпиком)
     */
    private void onBeforeSubtaskDelete(SubTask sub) {
        if (sub.getEpicId() != null) {
            epics.get(sub.getEpicId()).removeSubTask(sub.getEpicId());
            updateEpicStatus(epics.get(sub.getEpicId()));
        }
    }

    /**
     * Функция для обновления статуса в случае изменения сабтасков
     */
    private void updateEpicStatus(Epic epic) {
        ArrayList<SubTask> subTasks = getEpicSubTasks(epic);
        if (isStatus(TaskStatus.NEW, subTasks)) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        if (isStatus(TaskStatus.DONE, subTasks)) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }
        epic.setStatus(TaskStatus.IN_PROGRESS);
    }

    /**
     * Служебная функция для проверки что у всех сабтасков в списке статус равен переданному
     */
    private boolean isStatus(TaskStatus status, ArrayList<SubTask> subTasks) {
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() != status) {
                return false;
            }
        }
        return true;
    }
}
