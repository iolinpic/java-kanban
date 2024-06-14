package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        Epic task1 = new Epic("task1", "task1");
        Epic task2 = new Epic("task2", "task2");
        Epic task3 = new Epic("task3", "task3");
        task3.setId(1);
        Epic task4 = new Epic("task3", "task3");
        task4.setId(2);
        assertEquals(task1, task2);
        assertNotEquals(task3, task4);
    }
}