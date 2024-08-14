package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import managers.TaskManager;
import models.Epic;
import models.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {

    private TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
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
        if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
            int taskId = Integer.parseInt(pathParts[2]);
            getSubtasks(exchange, taskId);
        }
    }

    private void getList(HttpExchange exchange) throws IOException {
        List<Epic> tasks = taskManager.getEpics();
        String json = gson.toJson(tasks);
        sendText(exchange, json);
    }

    private void getItem(HttpExchange exchange, int index) throws IOException, NotFoundException {
        Epic task = taskManager.getEpic(index);
        String json = gson.toJson(task);
        sendText(exchange, json);
    }

    private void getSubtasks(HttpExchange exchange, int taskId) throws IOException, NotFoundException {
        List<SubTask> tasks = taskManager.getEpicSubTasks(taskManager.getEpic(taskId));
        String json = gson.toJson(tasks);
        sendText(exchange, json);
    }

    private void postHandle(HttpExchange exchange) throws IOException, NotFoundException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length == 2) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Epic task = gson.fromJson(body, Epic.class);
            if (task.getId() == 0) {
                taskManager.addTask(task);
            }
            sendSuccessfullyAdded(exchange);
        }
    }

    private void deleteHandle(HttpExchange exchange) throws IOException, NumberFormatException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length == 3) {
            int taskId = Integer.parseInt(pathParts[2]);
            taskManager.deleteEpic(taskId);
            sendText(exchange, "");
        }
    }
}
