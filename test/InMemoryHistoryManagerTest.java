import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldReturnHistoryInRightOrder() {
        historyManager.add(new Task("task1", "task"));
        historyManager.add(new Task("task2", "task"));
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals("task1", history.getFirst().getName());
        assertEquals("task2", history.get(1).getName());
    }
    @Test
    void shouldHaveLimitIn10Tasks() {
        historyManager.add(new Task("task1", "task"));
        historyManager.add(new Task("task2", "task"));
        historyManager.add(new Task("task3", "task"));
        historyManager.add(new Task("task4", "task"));
        historyManager.add(new Task("task5", "task"));
        historyManager.add(new Task("task6", "task"));
        historyManager.add(new Task("task7", "task"));
        historyManager.add(new Task("task8", "task"));
        historyManager.add(new Task("task9", "task"));
        historyManager.add(new Task("task10", "task"));
        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size());
        historyManager.add(new Task("task11", "task"));
        history = historyManager.getHistory();
        assertEquals(10, history.size());
    }


}