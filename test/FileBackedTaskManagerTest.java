import models.Epic;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private void addTasks(FileBackedTaskManager tm) {
        tm.addTask(new Epic("epic1", "epic1"));
        tm.addTask(new Epic("epic2", "epic2"));
        tm.addTask(new SubTask("sub1", "sub1", 1));
        tm.addTask(new SubTask("sub2", "sub2", 1));
        tm.addTask(new SubTask("sub3", "sub3", 1));
        tm.addTask(new Task("task", "detail"));
        tm.addTask(new Task("task2", "detail2"));
    }

    @Test
    void shouldSaveToEmptyFile() throws IOException {
        File file = File.createTempFile("save", ".csv");
        file.deleteOnExit();
        FileBackedTaskManager tm = new FileBackedTaskManager(file.getAbsolutePath());
        assertDoesNotThrow(() -> addTasks(tm));
    }

    @Test
    void shouldLoadFromEmptyFile() throws IOException {
        File file = File.createTempFile("load", ".csv");
        file.deleteOnExit();
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(file));
    }

    @Test
    void shouldSaveToFileMultipleTasks() throws IOException {
        File file = File.createTempFile("saveMultiple", ".csv");
        file.deleteOnExit();
        FileBackedTaskManager tm = new FileBackedTaskManager(file.getAbsolutePath());
        addTasks(tm);
        int linesCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                br.readLine();
                linesCount++;
            }
        }
        assertEquals(linesCount, tm.getEpics().size() + tm.getSubTasks().size() + tm.getTasks().size() + 1);
    }

    @Test
    void shouldLoadFromFileMultipleTasks() throws IOException {
        File file = File.createTempFile("loadMultiple", ".csv");
        file.deleteOnExit();
        FileBackedTaskManager tm = new FileBackedTaskManager(file.getAbsolutePath());
        addTasks(tm);
        FileBackedTaskManager loadedTm = FileBackedTaskManager.loadFromFile(file);
        assertEquals(tm.getEpics().size(), loadedTm.getEpics().size());
        assertEquals(tm.getTasks().size(), loadedTm.getTasks().size());
        assertEquals(tm.getSubTasks().size(), loadedTm.getSubTasks().size());
        assertArrayEquals(tm.getTasks().toArray(), loadedTm.getTasks().toArray());
        assertArrayEquals(tm.getEpics().toArray(), loadedTm.getEpics().toArray());
        assertArrayEquals(tm.getSubTasks().toArray(), loadedTm.getSubTasks().toArray());
    }
}