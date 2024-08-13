package managers;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        //вообще в тз нет задачи переделать дефолтный таск менеджер
        //return new managers.InMemoryTaskManager();
        return FileBackedTaskManager.loadFromFile(new File("data.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
