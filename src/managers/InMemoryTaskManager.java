package managers;

import exceptions.InterceptionException;
import exceptions.NotFoundException;
import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, SubTask> subtasks;
    protected final HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager;
    protected final Set<Task> prioritizedTasks = new TreeSet<>();

    int index;

    public InMemoryTaskManager() {
        index = 1;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    public static boolean isTasksTimelineIntersect(Task task1, Task task2) {
        if (task1 == null || task2 == null || task1.getStartTime() == null || task2.getStartTime() == null || task1.getEndTime() == null) {
            return false;
        }
        if (task1.getStartTime().isEqual(task2.getStartTime())) {
            return true;
        }
        if (task1.getStartTime().isBefore(task2.getStartTime())) {
            return task1.getEndTime().isAfter(task2.getStartTime()) || task1.getEndTime().isEqual(task2.getStartTime());
        } else {
            return isTasksTimelineIntersect(task2, task1);
        }
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
    public void addTask(Task task) throws InterceptionException {
        if (isTaskInvalid(task, null)) {
            throw new InterceptionException();
        }
        task.setId(getNextIndex());
        tasks.put(task.getId(), task);
        addToPrioritizedTaskList(task, false);
    }

    @Override
    public void addTask(Epic epic) {
        epic.setId(getNextIndex());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        addToPrioritizedTaskList(epic, false);
    }

    @Override
    public void addTask(SubTask subTask) throws InterceptionException {
        if (isTaskInvalid(subTask, null)) {
            throw new InterceptionException();
        }
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
    public Task getTask(int index) throws NotFoundException {

        Task task = tasks.get(index);
        if (task == null) {
            throw new NotFoundException();
        }
        task = new Task(task);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubTask(int index) throws NotFoundException {
        SubTask subTask = subtasks.get(index);
        if (subTask == null) {
            throw new NotFoundException();
        }
        subTask = new SubTask(subTask);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpic(int index) throws NotFoundException {
        Epic epic = epics.get(index);
        if (epic == null) {
            throw new NotFoundException();
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
        if (!tasks.containsKey(index)) {
            return;
        }
        historyManager.remove(index);
        prioritizedTasks.remove(tasks.get(index));
        tasks.remove(index);
    }

    @Override
    public void deleteSubTask(int index) {
        if (!subtasks.containsKey(index)) {
            return;
        }
        onBeforeSubtaskDelete(subtasks.get(index));
        historyManager.remove(index);
        prioritizedTasks.remove(subtasks.get(index));
        subtasks.remove(index);
    }

    @Override
    public void deleteEpic(int index) {
        if (!epics.containsKey(index)) {
            return;
        }
        onBeforeEpicDelete(epics.get(index));
        historyManager.remove(index);
        prioritizedTasks.remove(epics.get(index));
        epics.remove(index);
    }

    @Override
    public void update(Task task) throws InterceptionException, NotFoundException {
        if (tasks.containsKey(task.getId())) {
            if (isTaskInvalid(task, tasks.get(task.getId()))) {
                throw new InterceptionException();
            }
            tasks.put(task.getId(), task);
            addToPrioritizedTaskList(task, true);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public void update(SubTask subtask) throws InterceptionException, NotFoundException {
        if (subtasks.containsKey(subtask.getId())) {
            Epic epic = epics.get(subtask.getEpicId());
            SubTask oldSubtask = subtasks.get(subtask.getId());
            if (isTaskInvalid(subtask, oldSubtask)) {
                throw new InterceptionException();
            }
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
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public void update(Epic epic) throws NotFoundException {
        if (epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());
            // удаляем те индексы что не относятся к сабтаскам
            epic.getSubTasks().removeIf(newId -> !subtasks.containsKey(newId));

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
        } else {
            throw new NotFoundException();
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
        if (subTasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        }
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
     */
    private void updateEpic(Epic epic) {
        updateEpicStatus(epic);
        updateEpicDates(epic);
    }

    /**
     * Выполняем расчет startTime - как минимальную дату начала, duration как сумму продолжительностей
     * и endTime как самую позднюю из дат окончания из всех сабтасков
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

    /**
     * Проверяем что добавляемая задача не пересекается по временному интервалу с существующими
     */
    private boolean isTaskInvalid(Task task, Task updatedTask) {
//        return false;
        return getPrioritizedTasks().stream().filter(t -> !t.equals(updatedTask)).anyMatch(t -> isTasksTimelineIntersect(t, task));
    }


}
