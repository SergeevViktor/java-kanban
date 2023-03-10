package manager.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Manager;
import manager.taskManagers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", this::handlePrioritySet);
        server.createContext("/tasks/history", this::handleHistory);
        server.createContext("/tasks/task", this::handleTasks);
        server.createContext("/tasks/epic", this::handleEpics);
        server.createContext("/tasks/subtask", this::handleSubtasks);
        gson =  Manager.getGson();
        taskManager = Manager.getDefault();
    }

    public static void main(String[] args) throws IOException {
        new HttpTaskServer().start();
    }

    private void handleHistory(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String response;
            if ("GET".equals(method)) {
                response = gson.toJson(taskManager.getHistory());
                writeResponse(exchange, response, 200);
            } else {
                writeResponse(exchange,
                        "Некорректно указан метод для запроса /tasks/history: " + method + ". Необходим GET-запрос",
                        404);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void handlePrioritySet(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String response;
            if ("GET".equals(method)) {
                response = gson.toJson(taskManager.getPrioritySet());
                writeResponse(exchange, response, 200);
            } else {
                writeResponse(exchange,
                        "Некорректно указан метод для запроса /tasks: " + method + ". Необходим GET-запрос",
                        404);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void handleSubtasks(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            String method = exchange.getRequestMethod();
            String response;
            switch (method) {
                case "GET": {
                    if (query == null) {
                        response = gson.toJson(taskManager.getAllSub());
                        writeResponse(exchange, response, 200);
                        return;
                    } else if (Pattern.matches("^id=\\d+$", query)) {
                        String pathId = query.replaceFirst("id=", "");
                        int id = parsePathId(pathId);
                        if (Pattern.matches("^/tasks/subtask/epic/$", path)) {
                            if (taskManager.isEpicPresent(id)) {
                                response = gson.toJson(taskManager.getSubInEpic(id));
                                writeResponse(exchange, response, 200);
                                return;
                            } else {
                                writeResponse(exchange, "Нет Epic с Id " + id, 400);
                            }
                        } else if (Pattern.matches("^/tasks/subtask/$", path)) {
                            if (taskManager.isSubPresent(id)) {
                                response = gson.toJson(taskManager.getSub(id));
                                writeResponse(exchange, response, 200);
                                return;
                            } else {
                                writeResponse(exchange, "Subtask с Id " + id + " не существует.",
                                        400);
                            }
                        } else {
                            writeResponse(exchange, "Данный запрос не может быть обработан.",
                                    404);
                        }
                    } else {
                        writeResponse(exchange, "Данный запрос не может быть обработан.", 404);
                    }
                    break;
                }
                case "POST": {
                    handlePostSubtask(exchange);
                    break;
                }
                case "DELETE": {
                    if (query == null) {
                        taskManager.deleteAllSub();
                        writeResponse(exchange, "Все subtask удалены.", 200);
                        return;
                    } else if (Pattern.matches("^id=\\d+$", query)) {
                        String pathId = query.replaceFirst("id=", "");
                        int id = parsePathId(pathId);
                        if (Pattern.matches("^/tasks/subtask/$", path)) {
                            if (taskManager.isSubPresent(id)) {
                                taskManager.deleteSubtask(id);
                                writeResponse(exchange, "Subtask с Id " + id + " удален.", 200);
                            } else {
                                writeResponse(exchange, "Subtask с Id " + id + " не существует.",
                                        400);
                            }
                        }
                    } else {
                        writeResponse(exchange, "Данный запрос не может быть обработан.",
                                404);
                    }
                    break;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void handleEpics(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            String method = exchange.getRequestMethod();
            String response;
            switch (method) {
                case "GET": {
                    if (query == null) {
                        response = gson.toJson(taskManager.getAllEpic());
                        writeResponse(exchange, response, 200);
                        return;
                    } else if (Pattern.matches("^id=\\d+$", query)) {
                        String pathId = query.replaceFirst("id=", "");
                        int id = parsePathId(pathId);
                        if (Pattern.matches("^/tasks/epic/$", path)) {
                            if (taskManager.isEpicPresent(id)) {
                                response = gson.toJson(taskManager.getEpic(id));
                                writeResponse(exchange, response, 200);
                                return;
                            } else {
                                writeResponse(exchange, "Epic с Id " + id + " не существует.",
                                        400);
                            }
                        } else {
                            writeResponse(exchange, "Данный запрос не может быть обработан.",
                                    404);
                        }
                    } else {
                        writeResponse(exchange, "Данный запрос не может быть обработан.", 404);
                    }
                    break;
                }
                case "POST": {
                    handlePostEpic(exchange);
                    break;
                }
                case "DELETE": {
                    if (query == null) {
                        taskManager.deleteAllEpic();
                        writeResponse(exchange, "Все subtask удалены.", 200);
                        return;
                    } else if (Pattern.matches("^id=\\d+$", query)) {
                        String pathId = query.replaceFirst("id=", "");
                        int id = parsePathId(pathId);
                        if (Pattern.matches("^/tasks/epic/$", path)) {
                            if (taskManager.isEpicPresent(id)) {
                                taskManager.deleteEpic(id);
                                writeResponse(exchange, "Epic с Id " + id + " удален.", 200);
                            } else {
                                writeResponse(exchange, "Epic с Id " + id + " не существует.",
                                        400);
                            }
                        }
                    } else {
                        writeResponse(exchange, "Данный запрос не может быть обработан.",
                                404);
                    }
                    break;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void handleTasks(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            String method = exchange.getRequestMethod();
            String response;
            switch (method) {
                case "GET": {
                    if (query == null) {
                        response = gson.toJson(taskManager.getAllTasks());
                        writeResponse(exchange, response, 200);
                        return;
                    } else if (Pattern.matches("^id=\\d+$", query)) {
                        String pathId = query.replaceFirst("id=", "");
                        int id = parsePathId(pathId);
                        if (Pattern.matches("^/tasks/task/$", path)) {
                            if (taskManager.isTaskPresent(id)) {
                                response = gson.toJson(taskManager.getTask(id));
                                writeResponse(exchange, response, 200);
                                return;
                            } else {
                                writeResponse(exchange, "Task с Id " + id + " не существует.",
                                        400);
                            }
                        } else {
                            writeResponse(exchange, "Данный запрос не может быть обработан.",
                                    404);
                        }
                    } else {
                        writeResponse(exchange, "Данный запрос не может быть обработан.", 404);
                    }
                    break;
                }
                case "POST": {
                    handlePostTask(exchange);
                    break;
                }
                case "DELETE": {
                    if (query == null) {
                        taskManager.deleteAllTasks();
                        writeResponse(exchange, "Все subtask удалены.", 200);
                        return;
                    } else if (Pattern.matches("^id=\\d+$", query)) {
                        String pathId = query.replaceFirst("id=", "");
                        int id = parsePathId(pathId);
                        if (Pattern.matches("^/tasks/task/$", path)) {
                            if (taskManager.isTaskPresent(id)) {
                                taskManager.deleteTask(id);
                                writeResponse(exchange, "Task с Id " + id + " удален.", 200);
                            } else {
                                writeResponse(exchange, "Task с Id " + id + " не существует.",
                                        400);
                            }
                        }
                    } else {
                        writeResponse(exchange, "Данный запрос не может быть обработан.",
                                404);
                    }
                    break;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        String body = readText(exchange);
        Subtask subtask;
        try {
            subtask = gson.fromJson(body, Subtask.class);
            if (subtask.getName() == null || subtask.getDescription() == null) {
                writeResponse(exchange, "Поля subtask не могут быть пустыми.", 400);
                return;
            }
        } catch (JsonSyntaxException exception) {
            writeResponse(exchange, "Получени некорректный Json.", 400);
            return;
        }
        int id = subtask.getId();
        int epicId = subtask.getEpicId();
        if (id == 0) {
            taskManager.createSub(subtask, epicId);
            writeResponse(exchange, "Subtask добавлен.", 200);
            return;
        } else if (taskManager.isSubPresent(id)) {
            taskManager.updateSub(subtask);
            writeResponse(exchange, "Subtask обновлен.", 200);
            return;
        }
        writeResponse(exchange, "Subtask с Id " + id + " не существует.", 400);
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String body = readText(exchange);
        Epic epic;
        try {
            epic = gson.fromJson(body, Epic.class);
            if (epic.getName() == null || epic.getDescription() == null) {
                writeResponse(exchange, "Поля epic не могут быть пустыми.", 400);
                return;
            }
        } catch (JsonSyntaxException exception) {
            writeResponse(exchange, "Получени некорректный Json.", 400);
            return;
        }
        int id = epic.getId();
        if (id == 0) {
            taskManager.createEpic(epic);
            writeResponse(exchange, "Epic добавлен.", 200);
            return;
        } else if (taskManager.isEpicPresent(id)) {
            taskManager.updateEpic(epic);
            writeResponse(exchange, "Epic обновлен.", 200);
            return;
        }
        writeResponse(exchange, "Epic с Id " + id + " не существует.", 400);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        String body = readText(exchange);
        Task task;
        try {
            task = gson.fromJson(body, Task.class);
            if (task.getName() == null || task.getDescription() == null) {
                writeResponse(exchange, "Поля task не могут быть пустыми.", 400);
                return;
            }
        } catch (JsonSyntaxException exception) {
            writeResponse(exchange, "Получени некорректный Json.", 400);
            return;
        }
        int id = task.getId();
        if (id == 0) {
            taskManager.createTask(task);
            writeResponse(exchange, "Task добавлен.", 200);
            return;
        } else if (taskManager.isTaskPresent(id)) {
            taskManager.updateTask(task);
            writeResponse(exchange, "Task обновлен.", 200);
            return;
        }
        writeResponse(exchange, "Task с Id " + id + " не существует.", 400);
    }

    private void writeResponse(HttpExchange exchange, String responseString,
                               int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(UTF_8);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    protected String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
    }
}