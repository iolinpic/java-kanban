package handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler {

    private TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
