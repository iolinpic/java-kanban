package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import managers.TaskManager;
import models.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final Gson gson;
    private TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            if (method.equals("GET")) {
                getHandle(exchange);
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
            List<Task> list = taskManager.getHistory();
            String json = gson.toJson(list);
            sendText(exchange, json);
        }
    }
}
