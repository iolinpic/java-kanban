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
        historyManager.add(new Task("task1", "task",1));
        historyManager.add(new Task("task2", "task",2));
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals("task1", history.getFirst().getName());
        assertEquals("task2", history.get(1).getName());
    }

    @Test
    void shouldDeleteTaskFromHistory(){
        historyManager.add(new Task("task2", "task",2));
        historyManager.remove(2);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldReturnHistoryWithoutDuplicates(){
        historyManager.add(new Task("task1", "task",1));
        historyManager.add(new Task("task2", "task",2));
        historyManager.add(new Task("task3", "task",2));
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals("task3", history.get(1).getName());
    }

    @Test
    void shouldHaveNoLimitIn10Tasks() {
        historyManager.add(new Task("task1", "task",1));
        historyManager.add(new Task("task2", "task",2));
        historyManager.add(new Task("task3", "task",3));
        historyManager.add(new Task("task4", "task",4));
        historyManager.add(new Task("task5", "task",5));
        historyManager.add(new Task("task6", "task",6));
        historyManager.add(new Task("task7", "task",7));
        historyManager.add(new Task("task8", "task",8));
        historyManager.add(new Task("task9", "task",9));
        historyManager.add(new Task("task10", "task",10));
        historyManager.add(new Task("task11", "task",11));
        List<Task> history = historyManager.getHistory();
        assertEquals(11, history.size());
        historyManager.add(new Task("task12", "task",12));
        history = historyManager.getHistory();
        assertEquals(12, history.size());
    }

}