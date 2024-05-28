import models.Epic;
import models.SubTask;
import models.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager tm = new TaskManager();
        // TASK functions test
        Task task1 = new Task(tm.getNextIndex(), "задача 1", "делай дело 1");
        Task task2 = new Task(tm.getNextIndex(), "задача 2", "делай дело 2");
        tm.addTask(task1);
        tm.addTask(task2);
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
        // Epic functions test
        Epic epic = new Epic(tm.getNextIndex(), "Эпик 1", "делай большое дело 1");
        Epic epic2 = new Epic(tm.getNextIndex(), "Эпик 2", "делай большое дело 1");
        SubTask subTask1 = new SubTask(tm.getNextIndex(), "подзадача 1", "делай маленькое дело 1", epic);
        SubTask subTask2 = new SubTask(tm.getNextIndex(), "подзадача 2", "делай маленькое дело 2", epic2);
        SubTask subTask3 = new SubTask(tm.getNextIndex(), "подзадача 3", "делай маленькое дело 3", epic2);
        tm.addTask(subTask1);
        tm.addTask(subTask2);
        tm.addTask(subTask3);
        tm.addTask(epic);
        tm.addTask(epic2);
    }
}
