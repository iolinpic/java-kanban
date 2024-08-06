package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TaskTest {

    @Test
    void shouldBeEqualIfIdsAreEqual() {
        Task task1 = new Task("task1", "task1");
        Task task2 = new Task("task2", "task2");
        Task task3 = new Task("task3", "task3");
        task3.setId(1);
        Task task4 = new Task("task3", "task3");
        task4.setId(2);
        assertEquals(task1, task2);
        assertNotEquals(task3, task4);
    }


}