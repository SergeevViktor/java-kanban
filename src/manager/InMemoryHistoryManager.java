package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    // По поводу LinkedList почитаю, если что, потом добавлю. (Спасибо за совет!)
    List<Task> callHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (callHistory.size() == 10) {
            callHistory.remove(0);
        }
        callHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return callHistory;
    }

}
