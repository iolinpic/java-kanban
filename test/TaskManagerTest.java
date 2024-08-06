import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    abstract void init();

    @Test
    void shouldAddTaskAndFindItById() {
        taskManager.addTask(new Task("task", "task", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(2)));
        assertEquals(1, taskManager.getTasks().size());
        assertEquals("task", taskManager.getTask(1).getName());
    }

    @Test
    void shouldAddEpicAndFindItById() {
        taskManager.addTask(new Epic("epic", "task", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(2)));
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(0, taskManager.getTasks().size());
        assertEquals(0, taskManager.getSubTasks().size());
        assertEquals("epic", taskManager.getEpic(1).getName());
    }

    @Test
    void shouldAddSubtaskAndFindItById() {
        taskManager.addTask(new Epic("epic", "task", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(2)));
        taskManager.addTask(new SubTask("Subtask", "task", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(2), 1));
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(1, taskManager.getSubTasks().size());
        assertEquals(0, taskManager.getTasks().size());
        assertEquals("Subtask", taskManager.getSubTask(2).getName());
    }

    @Test
    void shouldNotConflictInternalIdsWithOuterIds() {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(2));
        task1.setId(3);
        taskManager.addTask(task1);
        taskManager.addTask(new Task("task2", "task2"));
        assertNotNull(taskManager.getTask(1));
        assertNull(taskManager.getTask(3));
    }

    @Test
    void shouldTaskFieldsBeTheSameAfterAddToManager() {
        taskManager.addTask(new Task("task1", "task1description", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(2)));
        taskManager.getTask(1);
        assertEquals("task1", taskManager.getTask(1).getName());
        assertEquals("task1description", taskManager.getTask(1).getDetails());
    }

    @Test
    void shouldNotChangeHistoryItemAfterAddToManager() {
        taskManager.addTask(new Task("task1", "task1description", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(2)));
        Task task = taskManager.getTask(1);
        task.setName("fuuu");
        task.setDetails("baar");
        assertEquals("task1", taskManager.getTask(1).getName());
        assertEquals("task1description", taskManager.getTask(1).getDetails());
        taskManager.update(task);
        List<Task> history = taskManager.getHistory();
        assertEquals("fuuu", taskManager.getTask(1).getName());
        assertEquals("task1", history.getFirst().getName());
    }

    @Test
    void shouldRemoveSubtaskIdFromEpicAfterDeleteFromManager() {
        taskManager.addTask(new Epic("epic", "task", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(1)));
        taskManager.addTask(new SubTask("Subtask", "task", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(2), 1));
        taskManager.addTask(new SubTask("Subtask2", "task", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(3), 1));
        taskManager.deleteSubTask(2);
        assertEquals(1, taskManager.getSubTasks().size());
        assertEquals(1, taskManager.getEpicSubTasks(taskManager.getEpic(1)).size());
        assertEquals("Subtask2", taskManager.getEpicSubTasks(taskManager.getEpic(1)).getFirst().getName());
    }

    //todo  добавить тест на приоритетный список

    @Test
    void epicStatusShouldBeNewIfAllSubtasksAreNew() {
        taskManager.addTask(new Epic("epic", "task", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(1)));
        taskManager.addTask(new SubTask("Subtask", "task", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(2), 1));
        taskManager.addTask(new SubTask("Subtask2", "task", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(3), 1));
        assertEquals(TaskStatus.NEW, taskManager.getEpic(1).getStatus());
    }

    @Test
    void epicStatusShouldBeDoneIfAllSubtasksAreDone() {
        taskManager.addTask(new Epic("epic", "task", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(1)));
        taskManager.addTask(new SubTask("Subtask", "task", TaskStatus.DONE, Duration.ofMinutes(15), LocalDateTime.now().minusDays(2), 1));
        taskManager.addTask(new SubTask("Subtask2", "task", TaskStatus.DONE, Duration.ofMinutes(15), LocalDateTime.now().minusDays(3), 1));
        assertEquals(TaskStatus.DONE, taskManager.getEpic(1).getStatus());
    }

    @Test
    void epicStatusShouldBeInProgressIfNotAllSubtasksAreDone() {
        taskManager.addTask(new Epic("epic", "task", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(1)));
        taskManager.addTask(new SubTask("Subtask", "task", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(2), 1));
        taskManager.addTask(new SubTask("Subtask2", "task", TaskStatus.DONE, Duration.ofMinutes(15), LocalDateTime.now().minusDays(3), 1));
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(1).getStatus());
    }

    @Test
    void epicStatusShouldBeInProgressIfAllSubtasksAreInProgress() {
        taskManager.addTask(new Epic("epic", "task", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(1)));
        taskManager.addTask(new SubTask("Subtask", "task", TaskStatus.IN_PROGRESS, Duration.ofMinutes(15), LocalDateTime.now().minusDays(2), 1));
        taskManager.addTask(new SubTask("Subtask2", "task", TaskStatus.IN_PROGRESS, Duration.ofMinutes(15), LocalDateTime.now().minusDays(3), 1));
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(1).getStatus());
    }

    @Test
    void shouldNotAddSubtaskWithoutEpic() {
        taskManager.addTask(new SubTask("Subtask", "task", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusDays(2), 1));
        assertEquals(0, taskManager.getSubTasks().size());
    }

    @Test
    void epicStatusShouldBeUpdatedByTaskManager() {
        taskManager.addTask(new Epic("epic", "task", TaskStatus.IN_PROGRESS, Duration.ofMinutes(15), LocalDateTime.now().minusDays(1)));
        assertEquals(TaskStatus.NEW, taskManager.getEpic(1).getStatus());
    }

    @Test
    void shouldReturnTasksInTimelineOrderIfAddOrderCorrect() {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 10, 0));
        Task task2 = new Task("task2", "task2", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 11, 15));
        Task task3 = new Task("task3", "task3", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 12, 15));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        assertEquals(3, taskManager.getPrioritizedTasks().size());
        assertEquals(task1, taskManager.getPrioritizedTasks().get(0));
        assertEquals(task2, taskManager.getPrioritizedTasks().get(1));
        assertEquals(task3, taskManager.getPrioritizedTasks().get(2));
    }

    @Test
    void shouldReturnTasksInTimelineOrderIfAddOrderIncorrect() {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 11, 0));
        Task task2 = new Task("task2", "task2", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 9, 15));
        Task task3 = new Task("task3", "task3", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 12, 15));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        assertEquals(3, taskManager.getPrioritizedTasks().size());
        assertEquals(task2, taskManager.getPrioritizedTasks().get(0));
        assertEquals(task1, taskManager.getPrioritizedTasks().get(1));
        assertEquals(task3, taskManager.getPrioritizedTasks().get(2));
    }

    @Test
    void shouldNotAddTaskIfTimelinesCross() {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 11, 0));
        Task task2 = new Task("task2", "task2", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 11, 15));
        Task task3 = new Task("task3", "task3", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 12, 15));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertEquals(task1, taskManager.getPrioritizedTasks().get(0));
        assertEquals(task3, taskManager.getPrioritizedTasks().get(1));
    }

    @Test
    void timelineIntersectShouldBeTrueIfBordersCross() {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 10, 0));
        Task task2 = new Task("task2", "task2", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 10, 15));
        assertTrue(InMemoryTaskManager.isTasksTimelineIntersect(task1, task2));
        assertTrue(InMemoryTaskManager.isTasksTimelineIntersect(task2, task1));
    }

    @Test
    void timelineIntersectShouldBeFalseIfIntervalsDoesntCross() {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 10, 0));
        Task task2 = new Task("task2", "task2", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 11, 15));
        assertFalse(InMemoryTaskManager.isTasksTimelineIntersect(task1, task2));
        assertFalse(InMemoryTaskManager.isTasksTimelineIntersect(task2, task1));
    }

    @Test
    void timelineIntersectShouldBeTrueIfIntervalsCross() {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 10, 0));
        Task task2 = new Task("task2", "task2", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 5, 10, 10));
        assertTrue(InMemoryTaskManager.isTasksTimelineIntersect(task1, task2));
        assertTrue(InMemoryTaskManager.isTasksTimelineIntersect(task2, task1));
    }
}