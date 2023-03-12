package manager.taskManagers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Manager;
import manager.client.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String url) {
        super(url);
        client = new KVTaskClient(url);
        gson = Manager.getGson();
    }

    @Override
    public void save() {
        client.put("tasks", gson.toJson(getAllTasks()));
        client.put("epics", gson.toJson(getAllEpic()));
        client.put("subtasks", gson.toJson(getAllSub()));
        client.put("history", gson.toJson(historyManager.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList())));
    }

    public static HttpTaskManager loadFromServer(String url) {
        HttpTaskManager manager = new HttpTaskManager(url);
        int maxId = 0;

        String jsonString = manager.client.load("tasks");
        if (!jsonString.isEmpty()) {
            Type taskMap = new TypeToken<Map<String, Task>>(){}.getType();
            Map<String, Task> tasks = manager.gson.fromJson(jsonString, taskMap);
            for (Map.Entry<String, Task> entry : tasks.entrySet()) {
                manager.tasks.put(entry.getValue().getId(), entry.getValue());
                manager.findCrossTask(entry.getValue());
                if (entry.getValue().getId() >  maxId) {
                    maxId = entry.getValue().getId();
                }
            }
        }
        jsonString = manager.client.load("epics");
        if (!jsonString.isEmpty()) {
            Type epicMap = new TypeToken<Map<String, Epic>>() {}.getType();
            Map<String, Epic> epics = manager.gson.fromJson(jsonString, epicMap);
            for (Map.Entry<String, Epic> entry : epics.entrySet()) {
                manager.epics.put(entry.getValue().getId(), entry.getValue());
                if (entry.getValue().getId() >  maxId) {
                    maxId = entry.getValue().getId();
                }
            }
        }
        jsonString = manager.client.load("subtasks");
        if (!jsonString.isEmpty()) {
            Type subMap = new TypeToken<Map<String, Subtask>>() {}.getType();
            Map<String, Subtask> subs = manager.gson.fromJson(jsonString, subMap);
            for (Map.Entry<String, Subtask> entry : subs.entrySet()) {
                manager.subtasks.put(entry.getValue().getId(), entry.getValue());
                manager.findCrossTask(entry.getValue());
                if (entry.getValue().getId() >  maxId) {
                    maxId = entry.getValue().getId();
                }
            }
        }
        jsonString = manager.client.load("history");
        if (!jsonString.isEmpty()) {
            Type historyList = new TypeToken<List<Integer>>() {}.getType();
            List<Integer> history = manager.gson.fromJson(jsonString, historyList);
            for (Integer id : history) {
                if (manager.tasks.containsKey(id)) {
                    manager.historyManager.add(manager.tasks.get(id));
                } else if (manager.epics.containsKey(id)) {
                    manager.historyManager.add(manager.epics.get(id));
                } else if (manager.subtasks.containsKey(id)) {
                    manager.historyManager.add(manager.subtasks.get(id));
                }
            }
        }
        manager.id = maxId;
        return manager;
    }
}
