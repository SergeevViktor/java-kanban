package manager.taskManagers;

import manager.Manager;
import manager.historyManager.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.exceptions.ManagerValidationException;
import util.enums.Status;
import util.enums.TaskType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy/HH:mm");

    protected final HistoryManager historyManager = Manager.getDefaultHistory();

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final Set<Task> prioritySet = new TreeSet<>((t1, t2) -> {
        if (t1.getStartTime() == null) {
            if (t2.getStartTime() == null) {
                return t1.getId() - t2.getId();
            } else {
                return 1;
            }
        } else if (t2.getStartTime() == null) {
            return -1;
        }
        return LocalDateTime.parse(t1.getStartTime(), formatter).compareTo(
                LocalDateTime.parse(t2.getStartTime(), formatter));
    });

    protected int id = 0;

    @Override
    public void createTask(Task task) {
        tasks.put(getNextId(), task);
        task.setStatus(String.valueOf(Status.NEW));
        task.setType(String.valueOf(TaskType.TASK));
        task.setId(id);
        findCrossTask(task);
    }

    @Override
    public void createEpic(Epic epic) {
        epics.put(getNextId(), epic);
        epic.setStatus(String.valueOf(Status.NEW));
        epic.setType(String.valueOf(TaskType.EPIC));
        epic.setId(id);
        changeEpicProgress(epic.getId());
    }

    @Override
    public void createSub(Subtask subtask, int epicId) {
        subtasks.put(getNextId(), subtask);
        subtask.setStatus(String.valueOf(Status.NEW));
        subtask.setType(String.valueOf(TaskType.SUBTASK));
        subtask.setId(id);
        subtask.setEpicId(epicId);
        if (epics.containsKey(epicId)) {
            epics.get(epicId).getSubInEpic().put(id, subtask);
        }
        changeEpicProgress(epicId);
        findCrossTask(subtask);
    }

    @Override
    public void updateTask(Task task) {
        task.setType(String.valueOf(TaskType.TASK));
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            prioritySet.remove(tasks.get(task.getId()));
            findCrossTask(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epic.setType(String.valueOf(TaskType.EPIC));
        if (epics.containsKey(epic.getId())) {
            epic.setSubInEpic(epics.get(epic.getId()).getSubInEpic());
            epic.setStatus(epics.get(epic.getId()).getStatus());
            epics.put(epic.getId(), epic);
            changeEpicProgress(epic.getId());
        }
    }

    @Override
    public void updateSub(Subtask subtask) {
        subtask.setType(String.valueOf(TaskType.SUBTASK));
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).getSubInEpic().put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).setStatus(updateStatus(subtask));
            changeEpicProgress(subtask.getEpicId());
            prioritySet.remove(subtasks.get(subtask.getId()));
            findCrossTask(subtask);
        }
    }


    private String updateStatus(Subtask subtask) {
        int statusNew = 0;
        int statusDone = 0;
        Epic useObject = epics.get(subtask.getEpicId());

        for (Subtask object : useObject.getSubInEpic().values()) {
            if (object.getStatus().equals("NEW")) {
                statusNew++;
            } else if (object.getStatus().equals("DONE")) {
                statusDone++;
            }
        }
        if (statusNew == useObject.getSubInEpic().size()) {
            return String.valueOf(Status.NEW);
        } else if (statusDone == useObject.getSubInEpic().size()) {
            return String.valueOf((Status.DONE));
        } else {
            return String.valueOf(Status.IN_PROGRESS);
        }
    }

    protected void findCrossTask(Task task) {
        if (task.getStartTime() != null) {
            final LocalDateTime startTime = LocalDateTime.parse(task.getStartTime(), formatter);
            final LocalDateTime endTime = LocalDateTime.parse(task.getEndTime(), formatter);
            for (Task t : prioritySet) {
                if (t.getStartTime() != null && t.getEndTime() != null) {
                    final LocalDateTime existStart = LocalDateTime.parse(t.getStartTime(), formatter);
                    final LocalDateTime existEnd = LocalDateTime.parse(t.getEndTime(), formatter);
                    if (endTime.isBefore(existStart) || existEnd.isBefore(startTime)) {
                        continue;
                    } else {
                        throw new ManagerValidationException("Задача пересекаются с id=" + t.getId() + " c " +
                                existStart.format(formatter) + " по " + existEnd.format(formatter));
                    }
                }
            }
        }
        prioritySet.add(task);
    }

    protected void changeEpicProgress(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        if (epics.get(epicId).getSubInEpic() != null) {
            epicSubtasks.addAll(epics.get(epicId).getSubInEpic().values());
        }
        changeEpicTime(epicId, epicSubtasks);
    }

    private void changeEpicTime(int epicId, List<Subtask> epicSubtasks) {
        long duration = 0L;
        LocalDateTime MIN_LOCAL_DATE_TIME = LocalDateTime.MAX;
        LocalDateTime MAX_LOCAL_DATE_TIME = LocalDateTime.MIN;

        for (Subtask epicSub : epicSubtasks) {
            if ((epicSub.getDuration()) != null) {
                duration += Long.parseLong(String.valueOf(epicSub.getDuration()));
            }

            if (epicSub.getStartTime() != null && epicSub.getEndTime() != null) {
                LocalDateTime startTimeSub = LocalDateTime.parse(epicSub.getStartTime(), formatter);
                LocalDateTime endTimeSub =  LocalDateTime.parse(epicSub.getEndTime(), formatter);

                if (MIN_LOCAL_DATE_TIME.isAfter(startTimeSub)) {
                    MIN_LOCAL_DATE_TIME = startTimeSub;
                }
                if (MAX_LOCAL_DATE_TIME.isBefore(endTimeSub)) {
                    MAX_LOCAL_DATE_TIME = endTimeSub;
                }
            }
        }
        if (MIN_LOCAL_DATE_TIME != LocalDateTime.MAX) {
            epics.get(epicId).setStartTime(MIN_LOCAL_DATE_TIME);
        }
        if (MAX_LOCAL_DATE_TIME != LocalDateTime.MIN) {
            epics.get(epicId).setEndTime(MAX_LOCAL_DATE_TIME);
        }
        if (duration != 0) {
            epics.get(epicId).setDuration(duration);
        }
    }

    @Override
    public Task getTask(int taskId) {
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpic(int epicId) {
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public Subtask getSub(int subId) {
        historyManager.add(subtasks.get(subId));
        return subtasks.get(subId);
    }

    @Override
    public HashMap<Integer, Subtask> getSubInEpic(int epicId) {
        return epics.get(epicId).getSubInEpic();
    }

    @Override
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getAllEpic() {
        return epics;
    }

    @Override
    public HashMap<Integer, Subtask> getAllSub() {
        return subtasks;
    }

    @Override
    public Set<Task> getPrioritySet() {
        return prioritySet;
    }


    @Override
    public void deleteTask(int taskId) {
        historyManager.remove(taskId);
        prioritySet.remove(tasks.get(taskId));
        tasks.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        if (epics.get(epicId).getSubInEpic() != null){
            for (Integer subId : epics.get(epicId).getSubInEpic().keySet()) {
                historyManager.remove(subId);
                subtasks.remove(subId);
            }
        }
        historyManager.remove(epicId);
        epics.remove(epicId);
    }

    @Override
    public void deleteSubtask(int subId) {
        epics.get(subtasks.get(subId).getEpicId()).getSubInEpic().remove(subId);
        epics.get(subtasks.get(subId).getEpicId()).setStatus(updateStatus(subtasks.get(subId)));
        changeEpicProgress(subtasks.get(subId).getEpicId());
        historyManager.remove(subId);
        prioritySet.remove(subtasks.get(subId));
        subtasks.remove(subId);
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritySet.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        ArrayList<Integer> objectId = new ArrayList<>();
        for (Integer epicId : epics.keySet()) {
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getEpicId() == epicId) {
                    objectId.add(subtask.getId());
                }
            }
        }
        for (Integer object : objectId) {
            historyManager.remove(object);
            subtasks.remove(object);
        }
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    @Override
    public void deleteAllSub() {
        for (Subtask subtask : subtasks.values()) {
            epics.get(subtask.getEpicId()).getSubInEpic().clear();
            epics.get(subtask.getEpicId()).setStatus(updateStatus(subtask));
            historyManager.remove(subtask.getId());
            changeEpicProgress(subtasks.get(subtask.getId()).getEpicId());
            prioritySet.remove(subtask);
        }
        subtasks.clear();
    }

    @Override
    public int getNextId() {
        id++;
        return id;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public boolean isTaskPresent(int taskId) {
        return tasks.containsKey(taskId);
    }

    public boolean isEpicPresent(int epicId) {
        return epics.containsKey(epicId);
    }

    public boolean isSubPresent(int subId) {
        return subtasks.containsKey(subId);
    }
}