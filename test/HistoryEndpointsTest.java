import com.google.gson.Gson;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import models.Epic;
import models.SubTask;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryEndpointsTest {

    public static final String DEFAULT_URL = "http://localhost:8080/history";
    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson;
    HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = taskServer.getGson();
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
        taskServer = null;
        gson = null;
        manager = null;
    }

    @Test
    public void shouldReturnEmptyTaskList() throws IOException, InterruptedException {
        URI url = URI.create(DEFAULT_URL);
        HttpRequest request = HttpRequest.newBuilder(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    public void shouldReturnHistoryTaskList() throws IOException, InterruptedException {
        Task task = new Task("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 2, 1, 0));
        Epic epic = new Epic("epic", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0));
        SubTask subTask = new SubTask("subtask", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0), 2);
        manager.addTask(task);
        manager.addTask(epic);
        manager.addTask(subTask);
        manager.getEpic(2);
        manager.getSubTask(3);
        String json = gson.toJson(manager.getHistory());
        URI url = URI.create(DEFAULT_URL);
        HttpRequest request = HttpRequest.newBuilder(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(json, response.body());
    }


}