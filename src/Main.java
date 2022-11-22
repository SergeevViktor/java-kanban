public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task first = new Task("Задача 1", "Описание 1");
        taskManager.createTask(first);
        System.out.println(first);
        Epic epic1 = new Epic("EX 1", "Description 1");
        taskManager.createEpic(epic1);
        System.out.println(epic1);
        Subtask subtask1 = new Subtask("Sub1", "Desc1 sub1");
        Subtask subtask2 = new Subtask("Sub2", "Desc2 sub2");
        taskManager.createSub(subtask1, epic1.getId());
        taskManager.createSub(subtask2, epic1.getId());
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(taskManager.getSubInEpic(epic1.getId()));
        taskManager.deleteAllSub();
        System.out.println(taskManager.getAllSub());
    }
}
