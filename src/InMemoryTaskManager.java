import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    final HashMap<Integer, Task> tasks;
    final HashMap<Integer, SubTask> subtasks;
    final HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager;

    int index;

    InMemoryTaskManager() {
        index = 1;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    void updateIndexCounter() {
        index = 1;
        for (Integer key : tasks.keySet()) {
            index = Math.max(index, key);
        }
        for (Integer key : epics.keySet()) {
            index = Math.max(index, key);
        }
        for (Integer key : subtasks.keySet()) {
            index = Math.max(index, key);
        }
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
        epic.addSubTask(subTask);
        updateEpicStatus(epic);
    }


    @Override
    public Task getTask(int index) {

        Task task = tasks.get(index);
        if (task == null) {
            return null;
        }
        task = new Task(task);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubTask(int index) {
        SubTask subTask = subtasks.get(index);
        if (subTask == null) {
            return null;
        }
        subTask = new SubTask(subTask);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpic(int index) {
        Epic epic = epics.get(index);
        if (epic == null) {
            return null;
        }
        epic = new Epic(epic);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values().stream().map(Task::new).toList());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subtasks.values().stream().map(SubTask::new).toList());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values().stream().map(Epic::new).toList());
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void clearSubTasks() {
        for (SubTask subtask : subtasks.values()) {
            onBeforeSubtaskDelete(subtask);
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            onBeforeEpicDelete(epic);
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    @Override
    public void deleteTask(int index) {
        historyManager.remove(index);
        tasks.remove(index);
    }

    @Override
    public void deleteSubTask(int index) {
        onBeforeSubtaskDelete(subtasks.get(index));
        historyManager.remove(index);
        subtasks.remove(index);
    }

    @Override
    public void deleteEpic(int index) {
        onBeforeEpicDelete(epics.get(index));
        historyManager.remove(index);
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
            if (oldSubtask.getEpicId() != subtask.getEpicId()) {
                //если в старом сабтаске был другой епик
                Epic oldEpic = epics.get(oldSubtask.getEpicId());
                oldEpic.removeSubTask(oldSubtask);
                updateEpicStatus(oldEpic);
                epic.addSubTask(subtask);
            }
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void update(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());
            // удаляем те индексы что не относятся к сабтаскам
            for (Integer newId : epic.getSubTasks()) {
                if (!subtasks.containsKey(newId)) {
                    epic.getSubTasks().remove(newId);
                }
            }
            if (!Arrays.equals(epic.getSubTasks().toArray(), oldEpic.getSubTasks().toArray())) {
                //удаляем те сабтаски что были в старом но отсутствуют в новом
                for (Integer oldId : oldEpic.getSubTasks()) {
                    if (!epic.getSubTasks().contains(oldId)) {
                        this.deleteSubTask(oldId);
                    }
                }
                //по идее мы не можем переложить внутрь эпика чужие сабтаски, обновляя эпик
                //(тоесть у нас просто не может быть такой ситуации когда есть сабтаска без эпика,
                // а изменение связи эпик сабтаск производим через обновление сабтаска),
            }
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic);
        }
    }

    /**
     * Удаляем сабтаски перед удалением эпика
     */
    private void onBeforeEpicDelete(Epic epic) {
        if (epic == null) {
            return;
        }
        for (Integer subTaskId : epic.getSubTasks()) {
//            this.deleteSubTask(subTaskId);
            historyManager.remove(subTaskId);
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
    public List<SubTask> getEpicSubTasks(Epic epic) {
        ArrayList<Integer> subTaskIds = epic.getSubTasks();
        ArrayList<SubTask> result = new ArrayList<>(subTaskIds.size());
        for (Integer id : subTaskIds) {
            result.add(new SubTask(subtasks.get(id)));
        }
        return result;
    }

    /**
     * Функция для вызова перед удалением сабтаска (удаляет связь с эпиком)
     */
    private void onBeforeSubtaskDelete(SubTask sub) {
        epics.get(sub.getEpicId()).removeSubTask(sub);
        updateEpicStatus(epics.get(sub.getEpicId()));
    }

    /**
     * Функция для обновления статуса в случае изменения сабтасков
     */
    private void updateEpicStatus(Epic epic) {
        List<SubTask> subTasks = getEpicSubTasks(epic);
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
    private boolean isStatus(TaskStatus status, List<SubTask> subTasks) {
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
