import com.google.gson.Gson;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import models.Epic;
import models.SubTask;
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

class EpicsEndpointsTest {

    public static final String DEFAULT_URL = "http://localhost:8080/epics";
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
    public void shouldReturnSameTaskListAsAddedToManager() throws IOException, InterruptedException {
        Epic task = new Epic("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0));
        Epic task2 = new Epic("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 2, 1, 0));
        manager.addTask(task);
        manager.addTask(task2);
        String jsonSample = gson.toJson(manager.getEpics());
        URI url = URI.create(DEFAULT_URL);
        HttpRequest request = HttpRequest.newBuilder(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(jsonSample, response.body());
    }

    @Test
    public void shouldSuccessfullyReturnTaskById() throws IOException, InterruptedException {
        Epic task = new Epic("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0));
        manager.addTask(task);
        String jsonSample = gson.toJson(manager.getEpic(1));

        URI url = URI.create(DEFAULT_URL + "/1");
        HttpRequest request = HttpRequest.newBuilder(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(jsonSample, response.body());
    }

    @Test
    public void shouldReturn404WhenTaskNotFound() throws IOException, InterruptedException {
        URI url = URI.create(DEFAULT_URL + "/1");
        HttpRequest request = HttpRequest.newBuilder(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldReturnSubTaskList() throws IOException, InterruptedException {
        Epic task = new Epic("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0));
        SubTask task1 = new SubTask("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0), 1);
        manager.addTask(task);
        manager.addTask(task1);
        String jsonSample = gson.toJson(manager.getEpicSubTasks(manager.getEpic(1)));
        URI url = URI.create(DEFAULT_URL + "/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(jsonSample, response.body());
    }

    @Test
    public void shouldReturn404IfEpicNotFoundDuringSubTaskListLoad() throws IOException, InterruptedException {
        URI url = URI.create(DEFAULT_URL + "/2/subtasks");
        HttpRequest request = HttpRequest.newBuilder(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldSuccessfullyAddNewTask() throws IOException, InterruptedException {
        Epic task = new Epic("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0));
        String jsonBody = gson.toJson(task);
        URI url = URI.create(DEFAULT_URL);
        HttpRequest request = HttpRequest.newBuilder(url).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getEpics().size());
        assertEquals(task.getName(), manager.getEpic(1).getName());
    }


    @Test
    public void shouldSuccessfullyDeleteTask() throws IOException, InterruptedException {
        Epic task = new Epic("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0));
        manager.addTask(task);
        URI url = URI.create(DEFAULT_URL + "/1");
        HttpRequest request = HttpRequest.newBuilder(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getEpics().size());
    }
}