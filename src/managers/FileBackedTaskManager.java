package managers;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import models.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String filename;

    public FileBackedTaskManager(String filename) {
        super();
        this.filename = filename;
    }

    public static void main(String[] args) {
        FileBackedTaskManager tm = new FileBackedTaskManager("tasks.csv");
        tm.addTask(new Epic("epic1", "epic1"));
        tm.addTask(new Epic("epic2", "epic2"));
        tm.addTask(new SubTask("sub1", "sub1", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 0, 30), 1));
        tm.addTask(new SubTask("sub2", "sub2", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 2, 1, 0, 30), 1));
        tm.addTask(new SubTask("sub3", "sub3", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 3, 1, 0, 30), 1));
        tm.addTask(new Task("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 4, 1, 0, 30)));
        tm.addTask(new Task("task2", "detail2", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 5, 1, 0, 30)));
        tm.addTask(new Task("task2", "detail2", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 6, 1, 0, 30)));
        FileBackedTaskManager tm2 = FileBackedTaskManager.loadFromFile(new File("tasks.csv"));
        System.out.println("Список задач идентичен: " + Arrays.equals(tm.getTasks().toArray(), tm2.getTasks().toArray()));
        System.out.println("Список эпиков идентичен: " + Arrays.equals(tm.getEpics().toArray(), tm2.getEpics().toArray()));
        System.out.println("Список подзадач идентичен: " + Arrays.equals(tm.getSubTasks().toArray(), tm2.getSubTasks().toArray()));
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, StandardCharsets.UTF_8))) {
            String labels = "type,id,name,status,description,duration,start,epicId\n";
            writer.write(labels);
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
            throw new ManagerSaveException(e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerLoadException {
        //на случай если указанного файла не существует
        try {
            if (file.createNewFile()) {
                //если файл пустой то сразу отдаем дефолтный конструктор
                return new FileBackedTaskManager(file.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new ManagerLoadException(e);
        }

        FileBackedTaskManager tm = new FileBackedTaskManager(file.getAbsolutePath());
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String line = reader.readLine();
                // десериализация
                String[] parts = line.split(",");
                //пропускаем строчку с заголовками
                if (parts[0].equals("type")) {
                    continue;
                }
                switch (TaskType.valueOf(parts[0])) {
                    case TaskType.TASK:
                        Task task = stringToTask(parts);
                        tm.tasks.put(task.getId(), task);
                        break;
                    case TaskType.EPIC:
                        Epic epic = stringToEpic(parts);
                        tm.epics.put(epic.getId(), epic);
                        break;
                    case TaskType.SUBTASK:
                        SubTask subTask = stringToSubTask(parts);
                        tm.subtasks.put(subTask.getId(), subTask);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException(e);
        }
        // восстанавливаем список сабтасок в эпиках
        for (SubTask subTask : tm.subtasks.values()) {
            if (tm.epics.containsKey(subTask.getEpicId())) {
                tm.epics.get(subTask.getEpicId()).addSubTask(subTask);
            }
        }
        // пересчитываем endTime для epics
        for (Epic epic : tm.epics.values()) {
            tm.updateEpicDates(epic);
        }
        //восстанавливаем prioritized в TreeSet
        tm.prioritizedTasks.addAll(tm.tasks.values().stream().filter(task -> task.getStartTime() != null).toList());
        tm.prioritizedTasks.addAll(tm.epics.values().stream().filter(task -> task.getStartTime() != null).toList());
        tm.prioritizedTasks.addAll(tm.subtasks.values().stream().filter(task -> task.getStartTime() != null).toList());

        // выставляем следующий индекс
        tm.updateIndexCounter();
        return tm;
    }

    private static String taskToString(Task task) {
        return TaskType.TASK + "," + task.toString() + "\n";
    }

    private static String epicToString(Epic epic) {
        return TaskType.EPIC + "," + epic.toString() + "\n";
    }

    private static String subTaskToString(SubTask subTask) {
        return TaskType.SUBTASK + "," + subTask.toString() + "\n";
    }

    private static Task stringToTask(String[] parts) {
        Task newTask = new Task(parts[2], parts[4], TaskStatus.valueOf(parts[3]),
                Duration.ofMinutes(Long.parseLong(parts[5])),
                parseDate(parts[6]));
        newTask.setId(Integer.parseInt(parts[1]));
        return newTask;
    }

    private static Epic stringToEpic(String[] parts) {
        Epic newEpic = new Epic(parts[2], parts[4], TaskStatus.valueOf(parts[3]),
                Duration.ofMinutes(Long.parseLong(parts[5])),
                parseDate(parts[6]));
        newEpic.setId(Integer.parseInt(parts[1]));
        return newEpic;
    }

    private static SubTask stringToSubTask(String[] parts) {
        SubTask newSubtask = new SubTask(parts[2], parts[4], TaskStatus.valueOf(parts[3]),
                Duration.ofMinutes(Long.parseLong(parts[5])),
                parseDate(parts[6]),
                Integer.parseInt(parts[7]));
        newSubtask.setId(Integer.parseInt(parts[1]));
        return newSubtask;
    }

    private static LocalDateTime parseDate(String date) {
        if (date == null) {
            return null;
        }
        if (date.equals("null")) {
            return null;
        }
        return LocalDateTime.parse(date, Task.SERIALISATION_FORMATTER);
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
