import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Subtask> sub = new HashMap<>();
    HashMap<Integer, Epic> epic = new HashMap<>();
    int id = 0;

    public void createTask(String name, String description, String status) {
        tasks.put(id++, new Task(name, description, id, status));
    }

}
