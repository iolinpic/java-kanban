import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        userCase();
    }

    public static void userCase() {
        TaskManager tm = Managers.getDefault();
        tm.addTask(new Epic("epic1", "epic1"));
        tm.addTask(new Epic("epic2", "epic2"));
        tm.addTask(new SubTask("sub1", "sub1", tm.getEpic(1)));
        tm.addTask(new SubTask("sub2", "sub2", tm.getEpic(1)));
        tm.addTask(new SubTask("sub3", "sub3", tm.getEpic(1)));
        //случай 1
        List<Task> taskList = tm.getHistory();
        System.out.println("Длинна истории 1:"+taskList.size());
        System.out.println(taskList);
        //случай 2
        tm.getSubTask(3);
        tm.getSubTask(4);
        tm.getSubTask(3);
        tm.getSubTask(5);
        tm.getSubTask(3);
        tm.getEpic(1);
        tm.getEpic(2);
        tm.getEpic(2);
        tm.getEpic(1);

        taskList = tm.getHistory();
        System.out.println("Длинна истории 5:"+taskList.size());
        System.out.println(taskList);

        //случай 3
        tm.deleteEpic(1);
        taskList = tm.getHistory();
        System.out.println("Длинна истории 1:"+taskList.size());
        System.out.println(taskList);
    }
}
