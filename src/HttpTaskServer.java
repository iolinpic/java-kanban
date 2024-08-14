import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import handlers.*;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager manager;
    private final Gson gson;
    private HttpServer httpServer = null;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
        server.start();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(manager, getGson()));
        httpServer.createContext("/subtasks", new SubTaskHandler(manager, getGson()));
        httpServer.createContext("/epics", new EpicHandler(manager, getGson()));
        httpServer.createContext("/history", new HistoryHandler(manager, getGson()));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager, getGson()));
        httpServer.start();
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            httpServer = null;
        }
    }

    public Gson getGson() {
        return gson;
    }
}
