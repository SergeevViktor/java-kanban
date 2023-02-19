import manager.Manager;
import manager.taskManagers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;

import static java.time.Month.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Manager.getDefault();
    /*Task task = new Task("11", "22");
        Epic epic = new Epic("1", "2");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("2", "1",
                LocalDateTime.of(2023, FEBRUARY, 19, 19, 9), 540);
        manager.createSub(subtask, 1);


        System.out.println(task.getStartTime());
        System.out.println(manager.getEpic(epic.getId()));
        System.out.println(subtask.getEndTime());*/
        System.out.println(manager.getPrioritySet());
    }
}
