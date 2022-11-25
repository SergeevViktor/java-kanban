package tasks;

import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subInEpic = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id, String status) {
        super(name, description, id, status);
    }

    public HashMap<Integer, Subtask> getSubInEpic() {
        return subInEpic;
    }

    public void setSubInEpic(HashMap<Integer, Subtask> subInEpic) {
        this.subInEpic = subInEpic;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
