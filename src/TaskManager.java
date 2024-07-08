import models.Epic;
import models.SubTask;
import models.Task;

import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    void addTask(Epic epic);

    void addTask(SubTask subTask);

    Task getTask(int index);

    SubTask getSubTask(int index);

    Epic getEpic(int index);

    List<Task> getTasks();

    List<SubTask> getSubTasks();

    List<Epic> getEpics();

    void clearTasks();

    void clearSubTasks();

    void clearEpics();

    void deleteTask(int index);

    void deleteSubTask(int index);

    void deleteEpic(int index);

    void update(Task task);

    void update(SubTask subtask);

    void update(Epic epic);

    List<SubTask> getEpicSubTasks(Epic epic);

    List<Task> getHistory();
}
