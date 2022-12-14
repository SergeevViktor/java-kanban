package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    // По поводу LinkedList почитаю, если что, потом добавлю. (Спасибо за совет!)
    private final List<Task> history = new ArrayList<>();
    private static final int NUMBER_OF_VIEWS = 10;

    @Override
    public void add(Task task) {
        if (history.size() == NUMBER_OF_VIEWS) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

}
