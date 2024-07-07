import models.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history;

    InMemoryHistoryManager() {
        history = new LinkedList<>();
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        history.add(new Task(task));
    }

    @Override
    public void remove(int id) {
        //todo реализовать удаление из очереди
    }
}
