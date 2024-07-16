import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import models.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final String filename;

    public FileBackedTaskManager(String filename) {
        super();
        this.filename = filename;
    }

    public FileBackedTaskManager(String filename,
                                 HashMap<Integer, Task> tasks,
                                 HashMap<Integer, SubTask> subtasks,
                                 HashMap<Integer, Epic> epics) {
        super(tasks, subtasks, epics);
        this.filename = filename;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, StandardCharsets.UTF_8))) {
            for (Task task : getTasks()) {
                writer.write(taskToString(task));
            }
            for (Epic epic : getEpics()) {
                writer.write(epicToString(epic));
            }
            for (SubTask subTask : getSubTasks()) {
                writer.write(subTaskToString(subTask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        HashMap<Integer, Task> tasks = new HashMap<>();
        HashMap<Integer, Epic> epics = new HashMap<>();
        HashMap<Integer, SubTask> subTasks = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String line = reader.readLine();
                // десериализация
                String[] parts = line.split(",");
                switch (Tasks.valueOf(parts[0])) {
                    case Tasks.TASK:
                        Task task = stringToTask(parts);
                        tasks.put(task.getId(), task);
                        break;
                    case Tasks.EPIC:
                        Epic epic = stringToEpic(parts);
                        epics.put(epic.getId(), epic);
                        break;
                    case Tasks.SUBTASK:
                        SubTask subTask = stringToSubTask(parts);
                        subTasks.put(subTask.getId(), subTask);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException();
        }
        // восстанавливаем список сабтасок в эпиках
        for (SubTask subTask : subTasks.values()) {
            if (epics.containsKey(subTask.getId())) {
                epics.get(subTask.getEpicId()).addSubTask(subTask);
            }
        }
        return new FileBackedTaskManager(file.getName(), tasks, subTasks, epics);
    }

    private static String taskToString(Task task) {
        return Tasks.TASK + "," + task.toString() + "\n";
    }

    private static String epicToString(Epic epic) {
        return Tasks.EPIC + "," + epic.toString() + "\n";
    }

    private static String subTaskToString(SubTask subTask) {
        return Tasks.SUBTASK + "," + subTask.toString() + "\n";
    }

    private static Task stringToTask(String[] parts) {
        Task newTask = new Task(parts[2], parts[4]);
        newTask.setId(Integer.parseInt(parts[1]));
        newTask.setStatus(TaskStatus.valueOf(parts[3]));
        return newTask;
    }

    private static Epic stringToEpic(String[] parts) {
        Epic newEpic = new Epic(parts[2], parts[4]);
        newEpic.setId(Integer.parseInt(parts[1]));
        newEpic.setStatus(TaskStatus.valueOf(parts[3]));
        return newEpic;
    }

    private static SubTask stringToSubTask(String[] parts) {
        SubTask newSubtask = new SubTask(parts[2], parts[4], Integer.parseInt(parts[5]));
        newSubtask.setId(Integer.parseInt(parts[1]));
        newSubtask.setStatus(TaskStatus.valueOf(parts[3]));
        return newSubtask;
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
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubTasks() {
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
