package tests.taskManagerTests;

import com.google.gson.Gson;
import manager.Manager;
import manager.server.HttpTaskServer;
import manager.taskManagers.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private final Gson gson = Manager.getGson();
    private TaskManager taskManager;

}