package main.taskManagers;

import main.historyManager.InMemoryHistoryManager;
import main.util.enums.Status;
import main.util.exceptions.ManagerSaveException;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.util.enums.TaskType;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.Month.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final String pathToSave;

    public FileBackedTasksManager(String path) {
        this.pathToSave = path;
    }

    public static void main(String[] args) {
        String path = "./resources/SaveData.csv";
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(path);

        Task task1 = new Task("Task 1", "Desc 1",
                LocalDateTime.of(2023, FEBRUARY, 19, 19, 9), 60);
        fileBackedTasksManager.createTask(task1);
        Task task2 = new Task("Task 2", "Desc 2");
        fileBackedTasksManager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Epic 1 with 1 sub");
        fileBackedTasksManager.createEpic(epic1);
        Epic epic2 = new Epic("Epic 2", "Epic 2 with 2 sub");
        fileBackedTasksManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Sub 1", "Sub 1 in Epic 1");
        fileBackedTasksManager.createSub(subtask1, epic1.getId());
        Subtask subtask2 = new Subtask("Sub 2", "Sub 2 in Epic 2",
                LocalDateTime.of(2023, FEBRUARY, 19, 21, 10), 540);
        fileBackedTasksManager.createSub(subtask2, epic2.getId());
        Subtask subtask3 = new Subtask("Sub 3", "Sub 3 in Epic 2",
                LocalDateTime.of(2023, FEBRUARY, 21, 7, 50), 200);
        fileBackedTasksManager.createSub(subtask3, epic2.getId());

        System.out.println(fileBackedTasksManager.getTask(1));
        System.out.println(fileBackedTasksManager.getTask(2));

        System.out.println(fileBackedTasksManager.getEpic(3));
        System.out.println(fileBackedTasksManager.getEpic(4));

        System.out.println(fileBackedTasksManager.getSub(5));
        System.out.println(fileBackedTasksManager.getSub(7));
        Subtask subtask4 = new Subtask("Sub 1", "Sub 1 in Epic 1", 5,
                String.valueOf(Status.DONE), 3);
        fileBackedTasksManager.updateSub(subtask4);
        System.out.println(fileBackedTasksManager.getSub(5));

        System.out.println(fileBackedTasksManager.historyManager.getHistory());

        System.out.println(fileBackedTasksManager.getPrioritySet());

        System.out.println(loadFromFile(new File(path)).historyManager.getHistory());
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager
                = new FileBackedTasksManager(file.toString());
        if (file.length() != 0) {
            List<String> tasksList = new ArrayList<>();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                while (bufferedReader.ready()) {
                    String line = bufferedReader.readLine();
                    tasksList.add(line);
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
            for (int i = 1; i <= (tasksList.size() - 2); i++) {
                if (!tasksList.get(i).equals("")) {
                    fileBackedTasksManager.fromString(tasksList.get(i));
                }
            }

            List<Integer> historyId = new ArrayList<>(historyFromString(tasksList.get(tasksList.size() - 1)));
            for (int id : historyId) {
                if (fileBackedTasksManager.tasks.containsKey(id)) {
                    fileBackedTasksManager.getTask(id);
                } else if (fileBackedTasksManager.epics.containsKey(id)) {
                    fileBackedTasksManager.getEpic(id);
                } else if (fileBackedTasksManager.subtasks.containsKey(id)) {
                    fileBackedTasksManager.getSub(id);
                }
            }
        }
        return fileBackedTasksManager;
    }

    protected void save() {
        final String historyString = historyToString((InMemoryHistoryManager) historyManager);
        try (FileWriter fileWriter = new FileWriter(String.valueOf(pathToSave))) {
            fileWriter.write("id,type,name,status,description,startTime,duration,epicId");
            fileWriter.write("\n");
            for (Task task : tasks.values()) {
                fileWriter.write(task.toString());
                fileWriter.write("\n");
            }
            for (Epic epic : epics.values()) {
                fileWriter.write(epic.toString());
                fileWriter.write("\n");
            }
            for (Subtask subtask : subtasks.values()) {
                fileWriter.write(subtask.toString());
                fileWriter.write("\n");
            }
            fileWriter.write("\n");
            fileWriter.write(historyString);
        } catch (IOException exception) {
            throw new RuntimeException(new ManagerSaveException("Неудалось сохранить данные."));
        }
    }

    public Task fromString(String value) {
        String[] taskFromString = value.split(",");
        int id = Integer.parseInt(taskFromString[0]);
        String type = taskFromString[1];
        String name = taskFromString[2];
        String status = taskFromString[3];
        String description = taskFromString[4];
        LocalDateTime startTime;
        Long duration;
        if (!taskFromString[5].equals("null") && !taskFromString[6].equals("null")) {
            startTime = LocalDateTime.parse(taskFromString[5], DateTimeFormatter.ofPattern("dd.MM.yyyy/HH:mm"));
            duration = Long.parseLong(taskFromString[6]);
        } else {
            startTime = null;
            duration = null;
        }
        switch(TaskType.valueOf(type)) {
            case TASK: {
                Task task;
                if (startTime == null) {
                    task = new Task(name, description, id, status, type);
                } else {
                    task = new Task(name, description, id, status, type, startTime, duration);
                }
                tasks.put(task.getId(), task);
                return task;
            }
            case EPIC: {
                Epic epic = new Epic(name, description, id, status, type);
                epics.put(epic.getId(), epic);
                return epic;
            }
            case SUBTASK: {
                int epicId = Integer.parseInt(taskFromString[7]);
                Subtask subtask;
                if (startTime == null) {
                    subtask = new Subtask(name, description, id, status, type, epicId);
                } else {
                    subtask = new Subtask(name, description, id, status, type, epicId, startTime, duration);
                }
                subtasks.put(subtask.getId(), subtask);
                epics.get(epicId).getSubInEpic().put(subtask.getId(), subtask);
                changeEpicProgress(epicId);
                return subtask;
            }
            default: {
                throw new IllegalArgumentException("Неверный тип задачи: " + type);
            }
        }
    }

    static String historyToString(InMemoryHistoryManager manager) {
        List<Integer> tasksIdHistory = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            tasksIdHistory.add(task.getId());
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < tasksIdHistory.size(); i++) {
            if (i != tasksIdHistory.size() - 1) {
                stringBuilder.append(tasksIdHistory.get(i)).append(",");
            } else {
                stringBuilder.append(tasksIdHistory.get(i));
            }
        }
        return stringBuilder.toString();
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> historyList = new ArrayList<>();
        if (!value.equals("")) {
            String[] tasksIdHistory = value.split(",");
            for (String tasks : tasksIdHistory) {
                historyList.add(Integer.parseInt(tasks));
            }
        }
        return historyList;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSub(Subtask subtask, int epicId) {
        super.createSub(subtask, epicId);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSub(Subtask subtask) {
        super.updateSub(subtask);
        save();
    }

    @Override
    public Task getTask(int taskId) {
        Task task = super.getTask(taskId);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = super.getEpic(epicId);
        save();
        return epic;
    }

    @Override
    public Subtask getSub(int subId) {
        Subtask subtask = super.getSub(subId);
        save();
        return subtask;
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void deleteSubtask(int subId) {
        super.deleteSubtask(subId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSub() {
        super.deleteAllSub();
        save();
    }
}