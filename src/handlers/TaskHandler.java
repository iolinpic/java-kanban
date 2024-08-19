package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.InterceptionException;
import exceptions.NotFoundException;
import managers.TaskManager;
import models.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    getHandle(exchange);
                    break;
                case "POST":
                    postHandle(exchange);
                    break;
                case "DELETE":
                    deleteHandle(exchange);
                    break;
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (NumberFormatException e) {
            sendAppError(exchange);
        } catch (InterceptionException e) {
            sendHasInteractions(exchange);
        }

    }

    private void getHandle(HttpExchange exchange) throws IOException, NotFoundException, NumberFormatException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length == 2) {
            getList(exchange);
        }
        if (pathParts.length == 3) {
            int taskId = Integer.parseInt(pathParts[2]);
            getItem(exchange, taskId);
        }
    }

    private void getList(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getTasks();
        String json = gson.toJson(tasks);
        sendText(exchange, json);
    }

    private void getItem(HttpExchange exchange, int index) throws IOException, NotFoundException {
        Task task = taskManager.getTask(index);
        String json = gson.toJson(task);
        sendText(exchange, json);
    }

    private void postHandle(HttpExchange exchange) throws IOException, InterceptionException, NotFoundException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length == 2) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, Task.class);
            if (task.getId() == 0) {
                taskManager.addTask(task);

            } else {
                taskManager.update(task);
            }
            sendSuccessfullyAdded(exchange);
        }
    }

    private void deleteHandle(HttpExchange exchange) throws IOException, NumberFormatException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length == 3) {
            int taskId = Integer.parseInt(pathParts[2]);
            taskManager.deleteTask(taskId);
            sendText(exchange, "");
        }
    }
}
