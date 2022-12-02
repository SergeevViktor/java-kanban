package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {

    List<Task> callHistory = new ArrayList<>();

    void add(Task task);

    List<Task> getHistory();
}
