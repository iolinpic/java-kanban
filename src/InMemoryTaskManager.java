import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, SubTask> subtasks;
    private final HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager;

    private int index;

    InMemoryTaskManager(HistoryManager historyManager) {
        index = 1;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        this.historyManager = historyManager;
    }

    @Override
    public void addTask(Task task) {
        task.setId(getNextIndex());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addTask(Epic epic) {
        epic.setId(getNextIndex());
        epics.put(epic.getId(), epic);
    }

    @Override
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


    @Override
    public Task getTask(int index) {
        Task task = tasks.get(index);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubTask(int index) {
        SubTask subTask = subtasks.get(index);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpic(int index) {
        Epic epic = epics.get(index);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearSubTasks() {
        for (SubTask subtask : subtasks.values()) {
            onBeforeSubtaskDelete(subtask);
        }
        subtasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            onBeforeEpicDelete(epic);
        }
        epics.clear();
    }

    @Override
    public void deleteTask(int index) {
        tasks.remove(index);
    }

    @Override
    public void deleteSubTask(int index) {
        onBeforeSubtaskDelete(subtasks.get(index));
        subtasks.remove(index);
    }

    @Override
    public void deleteEpic(int index) {
        onBeforeEpicDelete(epics.get(index));
        epics.remove(index);
    }

    @Override
    public void update(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}