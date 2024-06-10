import models.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int HISTORY_SIZE = 10;
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
        if (history.size() == HISTORY_SIZE) {
            history.removeFirst();
        }
        history.add(task);
    }
}
