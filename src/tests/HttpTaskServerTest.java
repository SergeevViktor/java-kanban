package tests;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import main.Manager;
import main.server.HttpTaskServer;
import main.server.KVServer;
import main.taskManagers.TaskManager;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.util.enums.Status;
import main.util.enums.TaskType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private HttpTaskServer server;
    private final Gson gson = Manager.getGson();
    private final KVServer kvServer;

    {
        try {
            kvServer = new KVServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @BeforeEach
    void setUp() throws IOException {
        kvServer.start();
        TaskManager manager = Manager.getDefault();
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
        kvServer.stop();
    }

    @Test
    void shouldHandleSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Desc");
        Subtask subtask = new Subtask("Sub in Epic 1", "Desc", 1);

        //POST /tasks/epic/Body:{epic}
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(epic);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        //POST /tasks/subtask/Body:{epic}
        url = URI.create("http://localhost:8080/tasks/subtask");
        json = gson.toJson(subtask);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        //GET /task/subtask
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type subMap = new TypeToken<Map<String, Subtask>>() {}.getType();
        Map<String, Subtask> subs = gson.fromJson(response.body(), subMap);

        assertEquals(200, response.statusCode());
        assertNotNull(subs, "Задачи не возвращаются.");
        subtask.setId(2);
        subtask.setStatus(String.valueOf(Status.NEW));
        subtask.setType(String.valueOf(TaskType.SUBTASK));
        assertEquals(1, subs.size(), "Количество задач не совпадает.");
        assertEquals(subtask, subs.get("2"), "Задачи не совпадают.");

        //POST /tasks/subtask/Body:{task} - обновление
        subtask.setStartTime(LocalDateTime.of(2023,3,14,16,15));
        subtask.setDuration(60);

        json =  gson.toJson(subtask);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        //GET /tasks/subtask/?id=
        url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        //GET /tasks/history
        List<Task> history = getHistory(client);

        assertNotNull(history, "История не возвращается.");
        assertEquals(1, history.size(), "Количество задач не совпадает.");
        assertEquals(subtask, history.get(0), "История не совпадает.");

        //GET /task
        List<Task> priority = getPrioritySet(client);

        assertNotNull(priority, "Задачи не возвращается.");
        assertEquals(2, priority.size(), "Количество задач не совпадает.");
        assertEquals(subtask, priority.get(0), "Задачи не совпадает.");

        //GET /tasks/subtask/epic/?id=
        url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        subs = gson.fromJson(response.body(), subMap);

        assertEquals(200, response.statusCode());
        assertEquals(subtask, subs.get("2"), "Задачи не совпадают.");

        //DELETE /tasks/subtask/?id=
        url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        //DELETE /tasks/subtask
        url = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldHandleEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Desc");

        //POST /tasks/epic/Body:{epic}
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(epic);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        //GET /task/epic
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type epicMap = new TypeToken<Map<String, Epic>>() {}.getType();
        Map<String, Epic> epics = gson.fromJson(response.body(), epicMap);

        assertEquals(200, response.statusCode());
        assertNotNull(epics, "Задачи не возвращаются.");
        epic.setId(1);
        epic.setStatus(String.valueOf(Status.NEW));
        epic.setType(String.valueOf(TaskType.EPIC));
        assertEquals(1, epics.size(), "Количество задач не совпадает.");
        assertEquals(epic, epics.get("1"), "Задачи не совпадают.");

        //POST /tasks/task/Body:{task} - обновление
        epic.setStartTime(LocalDateTime.of(2023,3,14,16,15));
        epic.setDuration(60);

        json =  gson.toJson(epic);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        //GET /tasks/epic/?id=
        url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epic1 = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode());
        assertEquals(epic, epic1, "Задачи не совпадают.");

        //GET /tasks/history
        List<Task> history = getHistory(client);

        assertNotNull(history, "История не возвращается.");
        assertEquals(1, history.size(), "Количество задач не совпадает.");
        assertEquals(epic, history.get(0), "История не совпадает.");

        //DELETE /tasks/epic/?id=
        url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        //DELETE /tasks/epic
        url = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldHandleTasks() throws IOException, InterruptedException {
        Task task = new Task("Task", "Desc");

        //POST /tasks/task/Body:{task}
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        ///GET /task/task
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskMap = new TypeToken<Map<String, Task>>(){}.getType();
        Map<String, Task> tasks = gson.fromJson(response.body(), taskMap);

        assertEquals(200, response.statusCode());
        assertNotNull(tasks, "Задачи не возвращаются.");
        task.setId(1);
        task.setStatus(String.valueOf(Status.NEW));
        task.setType(String.valueOf(TaskType.TASK));
        assertEquals(1, tasks.size(), "Количество задач не совпадает.");
        assertEquals(task, tasks.get("1"), "Задачи не совпадают.");

        //POST /tasks/task/Body:{task} - обновление
        task.setStatus(String.valueOf(Status.DONE));
        task.setStartTime(LocalDateTime.of(2023,3,14,16,15));
        task.setDuration(60);

        json =  gson.toJson(task);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        //GET /tasks/task/?id=
        url = URI.create("http://localhost:8080/tasks/task/?id=1");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task task1 = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(task, task1, "Задачи не совпадают.");

        //GET /tasks/history
        List<Task> history = getHistory(client);

        assertNotNull(history, "История не возвращается.");
        assertEquals(1, history.size(), "Количество задач не совпадает.");
        assertEquals(task, history.get(0), "История не совпадает.");

        //GET /task
        List<Task> priority = getPrioritySet(client);

        assertNotNull(priority, "Задачи не возвращается.");
        assertEquals(2, priority.size(), "Количество задач не совпадает.");
        assertEquals(task, priority.get(0), "Задачи не совпадает.");

        //DELETE /tasks/task/?id=
        url = URI.create("http://localhost:8080/tasks/task/?id=1");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        //DELETE /tasks/task
        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldHandleHistory() throws IOException, InterruptedException {
        //пустой список задач, остальный случаи в других тестах
        HttpClient client = HttpClient.newHttpClient();
        List<Task> history = getHistory(client);

        assertNotNull(history, "История не возвращается.");
        assertEquals(0, history.size(), "Количество задач не совпадает.");
    }

    @Test
    void shouldHandlePriority() throws IOException, InterruptedException {
        //пустой список задач, остальный случаи в других тестах
        HttpClient client = HttpClient.newHttpClient();
        List<Task> priority = getPrioritySet(client);

        assertNotNull(priority, "История не возвращается.");
        assertEquals(0, priority.size(), "Количество задач не совпадает.");
    }

    @Test
    void shouldHandleTaskWithOtherValues() throws IOException, InterruptedException {
        Task task = new Task(null, null);
        //POST /tasks/task/Body:{task}
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Поля задачи не могут быть пустыми.");

        client = HttpClient.newHttpClient();
        json = gson.toJson("lclclc");
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());

        ///GET /task/task
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prikol/task"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Данный запрос не может быть обработан.");

        //GET /tasks/task/?id=?
        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/?id=1"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Task с таким Id не существует.");

        //DELETE /tasks/task/?id=?
        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/?id=1"))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Task с таким Id не существует.");
    }

    @Test
    void shouldHandleEpicWithOtherValues() throws IOException, InterruptedException {
        Epic epic = new Epic(null, null);
        //POST /tasks/epic/Body:{task}
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(epic);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Поля задачи не могут быть пустыми.");

        client = HttpClient.newHttpClient();
        json = gson.toJson("lclclc");
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());

        ///GET /task/epic
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prikol/epic"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Данный запрос не может быть обработан.");

        //GET /tasks/epic/?id=?
        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/?id=1"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Epic с таким Id не существует.");

        //DELETE /tasks/epic/?id=?
        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/?id=1"))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Epic с таким Id не существует.");
    }

    @Test
    void shouldHandleSubWithOtherValues() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(null, null);
        //POST /tasks/subtask/Body:{task}
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(subtask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Поля задачи не могут быть пустыми.");

        client = HttpClient.newHttpClient();
        json = gson.toJson("lclclc");
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());

        ///GET /task/subtask
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prikol/subtask"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Данный запрос не может быть обработан.");

        //GET /tasks/subtask/?id=?
        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/?id=1"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Subtask с таким Id не существует.");

        //DELETE /tasks/subtask/?id=?
        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/?id=1"))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Subtask с таким Id не существует.");
    }

    List<Task> getHistory(HttpClient client) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/history"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());

        List<Task> history = new ArrayList<>();
        Type taskType = new TypeToken<Task>() {}.getType();
        Type epicType = new TypeToken<Epic>() {}.getType();
        Type subType = new TypeToken<Subtask>() {}.getType();

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String objectType = jsonObject.get("type").getAsString();
            if (objectType.equals(String.valueOf(TaskType.TASK))) history.add(gson.fromJson(jsonObject, taskType));
            if (objectType.equals(String.valueOf(TaskType.EPIC))) history.add(gson.fromJson(jsonObject, epicType));
            if (objectType.equals(String.valueOf(TaskType.SUBTASK))) history.add(gson.fromJson(jsonObject, subType));
        } else if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                String objectType = element.getAsJsonObject().get("type").getAsString();
                if (objectType.equals(String.valueOf(TaskType.TASK))) history.add(gson.fromJson(element, taskType));
                if (objectType.equals(String.valueOf(TaskType.EPIC))) history.add(gson.fromJson(element, epicType));
                if (objectType.equals(String.valueOf(TaskType.SUBTASK))) history.add(gson.fromJson(element, subType));
            }
        }
        return history;
    }

    List<Task> getPrioritySet(HttpClient client) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());

        List<Task> priority = new ArrayList<>();
        Type taskType = new TypeToken<Task>() {}.getType();
        Type epicType = new TypeToken<Epic>() {}.getType();
        Type subType = new TypeToken<Subtask>() {}.getType();

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String objectType = jsonObject.get("type").getAsString();
            if (objectType.equals(String.valueOf(TaskType.TASK))) priority.add(gson.fromJson(jsonObject, taskType));
            if (objectType.equals(String.valueOf(TaskType.EPIC))) priority.add(gson.fromJson(jsonObject, epicType));
            if (objectType.equals(String.valueOf(TaskType.SUBTASK))) priority.add(gson.fromJson(jsonObject, subType));
        } else if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                String objectType = element.getAsJsonObject().get("type").getAsString();
                if (objectType.equals(String.valueOf(TaskType.TASK))) priority.add(gson.fromJson(element, taskType));
                if (objectType.equals(String.valueOf(TaskType.EPIC))) priority.add(gson.fromJson(element, epicType));
                if (objectType.equals(String.valueOf(TaskType.SUBTASK))) priority.add(gson.fromJson(element, subType));
            }
        }
        return priority;
    }
}