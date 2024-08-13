package managers;

import exceptions.InterceptionException;
import exceptions.NotFoundException;
import models.Epic;
import models.SubTask;
import models.Task;

import java.util.List;

public interface TaskManager {
    void addTask(Task task) throws InterceptionException;

    void addTask(Epic epic);

    void addTask(SubTask subTask) throws InterceptionException;

    Task getTask(int index) throws NotFoundException;

    SubTask getSubTask(int index) throws NotFoundException;

    Epic getEpic(int index) throws NotFoundException;

    List<Task> getTasks();

    List<SubTask> getSubTasks();

    List<Epic> getEpics();

    void clearTasks();

    void clearSubTasks();

    void clearEpics();

    void deleteTask(int index);

    void deleteSubTask(int index);

    void deleteEpic(int index);

    void update(Task task) throws InterceptionException, NotFoundException;

    void update(SubTask subtask) throws InterceptionException, NotFoundException;

    void update(Epic epic) throws NotFoundException;

    List<SubTask> getEpicSubTasks(Epic epic);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
