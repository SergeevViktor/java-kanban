import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    HashMap<Integer, Subtask> subInEpic = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id, String status) {
        super(name, description, id, status);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
