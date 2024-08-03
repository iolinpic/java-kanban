import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, SubTask> subtasks;
    protected final HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager;
    protected final Set<Task> prioritizedTasks = new TreeSet<>();

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
        addToPrioritizedTaskList(task, false);
    }

    @Override
    public void addTask(Epic epic) {
        epic.setId(getNextIndex());
        epics.put(epic.getId(), epic);
        addToPrioritizedTaskList(epic, false);
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
        updateEpic(epic);
        addToPrioritizedTaskList(epic, false);
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
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void clearSubTasks() {
        for (SubTask subtask : subtasks.values()) {
            onBeforeSubtaskDelete(subtask);
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            onBeforeEpicDelete(epic);
            historyManager.remove(epic.getId());
            prioritizedTasks.remove(epic);
        }
        epics.clear();
    }

    @Override
    public void deleteTask(int index) {
        historyManager.remove(index);
        prioritizedTasks.remove(tasks.get(index));
        tasks.remove(index);
    }

    @Override
    public void deleteSubTask(int index) {
        onBeforeSubtaskDelete(subtasks.get(index));
        historyManager.remove(index);
        prioritizedTasks.remove(subtasks.get(index));
        subtasks.remove(index);
    }

    @Override
    public void deleteEpic(int index) {
        onBeforeEpicDelete(epics.get(index));
        historyManager.remove(index);
        prioritizedTasks.remove(epics.get(index));
        epics.remove(index);
    }

    @Override
    public void update(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            addToPrioritizedTaskList(task, true);
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
                updateEpic(oldEpic);
                epic.addSubTask(subtask);
            }
            subtasks.put(subtask.getId(), subtask);
            addToPrioritizedTaskList(subtask, true);
            updateEpic(epic);
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
            updateEpic(epic);
            addToPrioritizedTaskList(epic, true);
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
            prioritizedTasks.remove(subtasks.get(subTaskId));
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
        return new ArrayList<>(epic.getSubTasks().stream().map(subtasks::get).map(SubTask::new).toList());
    }

    /**
     * Функция для вызова перед удалением сабтаска (удаляет связь с эпиком)
     */
    private void onBeforeSubtaskDelete(SubTask sub) {
        epics.get(sub.getEpicId()).removeSubTask(sub);
        updateEpic(epics.get(sub.getEpicId()));
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
     * Объединяем параметры которые нужно обновлять в эпике при добавлении\изменении сабтасков
     *
     * @param epic
     */
    private void updateEpic(Epic epic) {
        updateEpicStatus(epic);
        updateEpicDates(epic);
    }

    /**
     * Выполняем расчет startTime - как минимальную дату начала, duration как сумму продолжительностей
     * и endTime как самую позднюю из дат окончания из всех сабтасков
     *
     * @param epic
     */
    protected void updateEpicDates(Epic epic) {
        List<SubTask> subTasks = getEpicSubTasks(epic);
        subTasks.stream()
                .filter(subTask -> subTask.getStartTime() != null)
                .peek((subTask -> epic.setDuration(epic.getDuration().plus(subTask.getDuration()))))
                .min(Epic::compareByStartTime)
                .ifPresentOrElse((subTask -> epic.setStartTime(subTask.getStartTime())), () -> epic.setStartTime(null));
        subTasks.stream()
                .filter(subTask -> subTask.getEndTime() != null)
                .max(Epic::compareByEndTime)
                .ifPresentOrElse((subTask -> epic.setEndTime(subTask.getEndTime())), () -> epic.setEndTime(null));
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    /**
     * Перед добавлением в treeSet проверяем что startTime задан,
     * также учитываем что при обновлении нужно удалить элемент и потом добавить
     *
     * @param task
     * @param update
     */
    private void addToPrioritizedTaskList(Task task, boolean update) {
        if (task.getStartTime() == null) {
            return;
        }
        if (update) {
            prioritizedTasks.remove(task);
        }
        prioritizedTasks.add(task);
    }
}
