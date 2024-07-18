import models.Epic;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }


    @Test
    void shouldAddTaskAndFindItById() {
        taskManager.addTask(new Task("task", "task"));
        assertEquals(1, taskManager.getTasks().size());
        assertEquals("task", taskManager.getTask(1).getName());
    }

    @Test
    void shouldAddEpicAndFindItById() {
        taskManager.addTask(new Epic("epic", "task"));
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(0, taskManager.getTasks().size());
        assertEquals(0, taskManager.getSubTasks().size());
        assertEquals("epic", taskManager.getEpic(1).getName());
    }

    @Test
    void shouldAddSubtaskAndFindItById() {
        taskManager.addTask(new Epic("epic", "task"));
        taskManager.addTask(new SubTask("Subtask", "task", 1));
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(1, taskManager.getSubTasks().size());
        assertEquals(0, taskManager.getTasks().size());
        assertEquals("Subtask", taskManager.getSubTask(2).getName());
    }

    @Test
    void shouldNotConflictInternalIdsWithOuterIds() {
        Task task1 = new Task("task1", "task1");
        task1.setId(3);
        taskManager.addTask(task1);
        taskManager.addTask(new Task("task2", "task2"));
        assertNotNull(taskManager.getTask(1));
        assertNull(taskManager.getTask(3));
    }

    @Test
    void shouldTaskFieldsBeTheSameAfterAddToManager() {
        taskManager.addTask(new Task("task1", "task1description"));
        taskManager.getTask(1);
        assertEquals("task1", taskManager.getTask(1).getName());
        assertEquals("task1description", taskManager.getTask(1).getDetails());
    }

    @Test
    void shouldNotChangeHistoryItemAfterAddToManager() {
        taskManager.addTask(new Task("task1", "task1description"));
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
        taskManager.addTask(new Epic("epic", "task"));
        taskManager.addTask(new SubTask("Subtask", "task", 1));
        taskManager.addTask(new SubTask("Subtask2", "task", 1));
        taskManager.deleteSubTask(2);
        assertEquals(1, taskManager.getSubTasks().size());
        assertEquals(1, taskManager.getEpicSubTasks(taskManager.getEpic(1)).size());
        assertEquals("Subtask2", taskManager.getEpicSubTasks(taskManager.getEpic(1)).getFirst().getName());
    }
}