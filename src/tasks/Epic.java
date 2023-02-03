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

    public Epic(String name, String description, int id, String status, String type) {
        super(name, description, id, status, type);
    }

    public HashMap<Integer, Subtask> getSubInEpic() {
        return subInEpic;
    }

    public void setSubInEpic(HashMap<Integer, Subtask> subInEpic) {
        this.subInEpic = subInEpic;
    }

    @Override
    public String toString() {
        return getId() +
                "," + getType() +
                "," + getName() +
                "," + getStatus() +
                "," + getDescription()
                ;
    }
}
