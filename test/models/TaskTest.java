package models;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void timelineIntersectShouldBeTrueIfBordersCross() {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 10, 0));
        Task task2 = new Task("task2", "task2", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 10, 15));
        assertTrue(Task.isTasksTimelineIntersect(task1, task2));
        assertTrue(Task.isTasksTimelineIntersect(task2, task1));
    }

    @Test
    void timelineIntersectShouldBeFalseIfIntervalsDoesntCross() {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 10, 0));
        Task task2 = new Task("task2", "task2", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 11, 15));
        assertFalse(Task.isTasksTimelineIntersect(task1, task2));
        assertFalse(Task.isTasksTimelineIntersect(task2, task1));
    }

    @Test
    void timelineIntersectShouldBeTrueIfIntervalsCross() {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 10, 0));
        Task task2 = new Task("task2", "task2", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 10, 10));
        assertTrue(Task.isTasksTimelineIntersect(task1, task2));
        assertTrue(Task.isTasksTimelineIntersect(task2, task1));
    }
}