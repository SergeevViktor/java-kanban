package manager;

import tasks.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    @Override
    public void add(Task task) {
        callHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return callHistory;
    }

}
