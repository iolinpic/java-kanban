import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager tm = new TaskManager();
        tm.addTask(new Task("задача 1", "делай дело 1"));
        tm.addTask(new Task("задача 2", "делай дело 2"));
        System.out.println(tm.getTasks());
        tm.addEpic(new Epic("Эпик 1", "делай большое дело 1"));
        tm.addEpic(new Epic("Эпик 2", "делай большое дело 2"));
        System.out.println(tm.getEpics());
        tm.addSubTask(new SubTask("подзадача 1", "делай маленькое дело 1", tm.getEpic(3)));
        tm.addSubTask(new SubTask("подзадача 2", "делай маленькое дело 2", tm.getEpic(4)));
        tm.addSubTask(new SubTask("подзадача 3", "делай маленькое дело 3", tm.getEpic(4)));
        System.out.println(tm.getSubTasks());
        tm.deleteEpic(3);
        System.out.println(tm.getEpics());
        System.out.println(tm.getSubTasks());
        SubTask tmp = tm.getSubTask(6);
        tmp.setStatus(TaskStatus.DONE);
        tm.updateSubTask(tmp);
        System.out.println(tm.getEpics());
        System.out.println(tm.getSubTasks());
        System.out.println();
        System.out.println(tm.getEpicSubTasks(tm.getEpic(4)));
    }
}
