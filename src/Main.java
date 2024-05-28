import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager tm = new TaskManager();
        // TASK functions test
        Task task1 = new Task("задача 1", "делай дело 1");
        Task task2 = new Task("задача 2", "делай дело 2");
        tm.addTask(task1);
        tm.addTask(task2);
        System.out.println(tm.getTasks());
        boolean res1 = tm.getTasks().size() == 2 && tm.getTasks().containsValue(task1) && tm.getTasks().containsValue(task2);
        if (res1) System.out.println("TASK:добавление, вывод списка - ок");
        tm.deleteTask(task2.getId());
        boolean res2 = tm.getTasks().size() == 1 && tm.getTasks().containsValue(task1);
        if (res2) System.out.println("TASK:удаление - ок");
        boolean res3 = tm.getTask(task1.getId()).equals(task1);
        if (res3) System.out.println("TASK:получение - ок");
        task1.setName("задача 1 updated");
        tm.updateTask(task1);
        boolean res4 = tm.getTask(task1.getId()).equals(task1);
        if (res4) System.out.println("TASK:обновление - ок");
        System.out.println(tm.getTasks());
        // Epic functions test
        Epic epic = new Epic("Эпик 1", "делай большое дело 1");
        Epic epic2 = new Epic("Эпик 2", "делай большое дело 1");
        SubTask subTask1 = new SubTask("подзадача 1", "делай маленькое дело 1", epic);
        SubTask subTask2 = new SubTask("подзадача 2", "делай маленькое дело 2", epic2);
        SubTask subTask3 = new SubTask("подзадача 3", "делай маленькое дело 3", epic2);
        tm.addSubTask(subTask1);
        tm.addEpic(epic2);
        System.out.println(tm.getSubTasks());
        System.out.println(tm.getEpics());
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        tm.updateSubTask(subTask1);
        System.out.println(tm.getSubTasks());
        System.out.println(tm.getEpics());
        tm.deleteSubTask(subTask3.getId());
        subTask2.setStatus(TaskStatus.DONE);
        System.out.println(tm.getEpics());
        System.out.println(tm.getEpicSubTasks(tm.getEpic(epic2.getId())));
        tm.deleteEpic(epic.getId());
        System.out.println(tm.getEpics());
        System.out.println(tm.getSubTasks());
        tm.deleteSubTask(subTask2.getId());
        System.out.println(tm.getEpics());
        System.out.println(tm.getSubTasks());

    }
}
