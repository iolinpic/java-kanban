import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldReturnHistoryInRightOrder() {
        historyManager.add(new Task("task1", "task", 1));
        historyManager.add(new Task("task2", "task", 2));
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals("task1", history.getFirst().getName());
        assertEquals("task2", history.get(1).getName());
    }

    @Test
    void shouldDeleteTaskFromHistory() {
        historyManager.add(new Task("task2", "task", 2));
        historyManager.remove(2);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldReturnHistoryWithoutDuplicates() {
        historyManager.add(new Task("task1", "task", 1));
        historyManager.add(new Task("task2", "task", 2));
        historyManager.add(new Task("task3", "task", 2));
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals("task3", history.get(1).getName());
    }

    @Test
    void shouldHaveNoLimitIn10Tasks() {
        for (int i = 1; i <= 11; i++) {
            historyManager.add(new Task("task" + i, "task" + i, i));
        }
        List<Task> history = historyManager.getHistory();
        assertEquals(11, history.size());
        historyManager.add(new Task("task12", "task", 12));
        history = historyManager.getHistory();
        assertEquals(12, history.size());
    }

    @Test
    void createdHistoryManagerShouldBeEmpty() {
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void addingToEmptyHistoryShouldBeAvailable() {
        historyManager.add(new Task("task1", "task", 1));
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void removingFromEmptyHistoryShouldBeAvailable() {
        historyManager.remove(1);
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void removingFromHeadShouldShiftPositionWithoutChangingOrder() {
        historyManager.add(new Task("task1", "task", 1));
        historyManager.add(new Task("task2", "task", 2));
        historyManager.add(new Task("task3", "task", 3));
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        historyManager.remove(1);
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(history.get(1), historyManager.getHistory().get(0));
        assertEquals(history.get(2), historyManager.getHistory().get(1));
    }
    @Test
    void removingFromTailShouldSavePositionWithoutChangingOrder() {
        historyManager.add(new Task("task1", "task", 1));
        historyManager.add(new Task("task2", "task", 2));
        historyManager.add(new Task("task3", "task", 3));
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        historyManager.remove(3);
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(history.get(0), historyManager.getHistory().get(0));
        assertEquals(history.get(1), historyManager.getHistory().get(1));
    }
    @Test
    void removingFromMiddleShouldChangeTailPositionWithoutChangingOrder() {
        historyManager.add(new Task("task1", "task", 1));
        historyManager.add(new Task("task2", "task", 2));
        historyManager.add(new Task("task3", "task", 3));
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        historyManager.remove(2);
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(history.get(0), historyManager.getHistory().get(0));
        assertEquals(history.get(2), historyManager.getHistory().get(1));
    }

}