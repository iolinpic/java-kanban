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

class SubTasksEndpointsTest {

    public static final String DEFAULT_URL = "http://localhost:8080/subtasks";
    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson;
    HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        Epic epic = new Epic("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0));
        manager.addTask(epic);
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
        SubTask task = new SubTask("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0), 1);
        SubTask task2 = new SubTask("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 2, 1, 0), 1);
        manager.addTask(task);
        manager.addTask(task2);
        String jsonSample = gson.toJson(manager.getSubTasks());
        URI url = URI.create(DEFAULT_URL);
        HttpRequest request = HttpRequest.newBuilder(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(jsonSample, response.body());
    }

    @Test
    public void shouldSuccessfullyReturnTaskById() throws IOException, InterruptedException {
        SubTask task = new SubTask("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0), 1);
        manager.addTask(task);
        String jsonSample = gson.toJson(manager.getSubTask(2));

        URI url = URI.create(DEFAULT_URL + "/2");
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
    public void shouldSuccessfullyAddNewTask() throws IOException, InterruptedException {
        SubTask task = new SubTask("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0), 1);
        String jsonBody = gson.toJson(task);
        URI url = URI.create(DEFAULT_URL);
        HttpRequest request = HttpRequest.newBuilder(url).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getSubTasks().size());
        assertEquals(task.getName(), manager.getSubTask(2).getName());
    }

    @Test
    public void shouldReturn406OnAddWhenIntercept() throws IOException, InterruptedException {
        SubTask task = new SubTask("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0), 1);
        String jsonBody = gson.toJson(task);
        manager.addTask(task);

        URI url = URI.create(DEFAULT_URL);
        HttpRequest request = HttpRequest.newBuilder(url).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void shouldSuccessfullyUpdateTask() throws IOException, InterruptedException {
        SubTask task = new SubTask("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0), 1);
        manager.addTask(task);
        SubTask taskToUpdate = manager.getSubTask(2);
        taskToUpdate.setName("updated");
        String jsonBody = gson.toJson(taskToUpdate);

        URI url = URI.create(DEFAULT_URL);
        HttpRequest request = HttpRequest.newBuilder(url).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getSubTasks().size());
        assertEquals(taskToUpdate.getName(), manager.getSubTask(2).getName());
    }

    @Test
    public void shouldReturn404WhenNoTaskForUpdateExist() throws IOException, InterruptedException {
        SubTask task = new SubTask(1, "task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0), 1);
        String jsonBody = gson.toJson(task);

        URI url = URI.create(DEFAULT_URL);
        HttpRequest request = HttpRequest.newBuilder(url).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldReturn406WhenUpdatingTaskInterceptsWithOthers() throws IOException, InterruptedException {
        SubTask task = new SubTask("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0), 1);
        SubTask task2 = new SubTask("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 2, 1, 0), 1);
        manager.addTask(task);
        manager.addTask(task2);
        Task taskToUpdate = manager.getSubTask(2);
        taskToUpdate.setStartTime(LocalDateTime.of(2000, 1, 2, 1, 0));
        String jsonBody = gson.toJson(taskToUpdate);

        URI url = URI.create(DEFAULT_URL);
        HttpRequest request = HttpRequest.newBuilder(url).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void shouldSuccessfullyDeleteTask() throws IOException, InterruptedException {
        SubTask task = new SubTask("task", "detail", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2000, 1, 1, 1, 0), 1);
        manager.addTask(task);
        URI url = URI.create(DEFAULT_URL + "/2");
        HttpRequest request = HttpRequest.newBuilder(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getSubTasks().size());
    }
}