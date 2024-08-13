package handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    private TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
