import TaskManager.*;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        System.out.println("Тест создания и получения списка всех задач");

        Task strollTask = new Task("прогулка", "сходить погулять", Status.NEW);
        taskManager.createTask(strollTask);
        Task strollTask2 = new Task("прогулка", "сходить погулять", Status.NEW);
        taskManager.createTask(strollTask2);

        Task trashTask = new Task("мусор", "выкинуть мусор", Status.NEW);
        taskManager.createTask(trashTask);


        Epic productsEpic = new Epic("купить продукты", "сходить в ближайший магазин во время прогулки");
        taskManager.createEpic(productsEpic);
        Subtask milkSubtask = new Subtask("купить молоко","", Status.NEW, productsEpic.getId());
        taskManager.createSubtask(milkSubtask);
        Subtask breadSubtask = new Subtask("купить хлеб","", Status.NEW, productsEpic.getId());
        taskManager.createSubtask(breadSubtask);

        Epic pigeonsEpic = new Epic("покормить голубей", "покормить голубей купленным в магазине хлебом");
        taskManager.createEpic(pigeonsEpic);
        Subtask feedSubtask = new Subtask("раскрошить хлеб","что бы голуби смогли его съесть",
                              Status.NEW, pigeonsEpic.getId());
        taskManager.createSubtask(feedSubtask);

        HashMap<Integer, Task> tasks = taskManager.getTasks();
        System.out.println(tasks);
        HashMap<Integer, Epic> epics = taskManager.getEpics();
        System.out.println(epics);
        HashMap<Integer, Subtask> subtasks = taskManager.getSubtasks();
        System.out.println(subtasks);
        System.out.println();

        System.out.println("Тест получения задачи по id");

        Task taskById = taskManager.getTaskById(1059876834);
        System.out.println(taskById);
        Epic epicById = taskManager.getEpicById(1007592185);
        System.out.println(epicById);
        Subtask subtaskById = taskManager.getSubtaskById(-1736589052);
        System.out.println(subtaskById);
        System.out.println();

        System.out.println("Тест обновления задач и управления статуса эпика");

        strollTask.setStatus(Status.DONE);
        taskManager.updateTask(strollTask);
        System.out.println(taskManager.getTaskById(strollTask.getId()));

        milkSubtask.setStatus(Status.DONE);
        taskManager.updateSubTask(milkSubtask);
        System.out.println(taskManager.getSubtaskById(milkSubtask.getId()));
        System.out.println(taskManager.getEpicById(milkSubtask.getEpicId()));

        breadSubtask.setStatus(Status.DONE);
        taskManager.updateSubTask(breadSubtask);
        System.out.println(taskManager.getSubtaskById(breadSubtask.getId()));
        System.out.println(taskManager.getEpicById(milkSubtask.getEpicId()));
        System.out.println();

        System.out.println("Тест получения списка всех подзадач определеноого эпика");

        HashMap<Integer, Subtask> subtasksByEpicId = taskManager.getSubtasksOfEpic(1007592185);
        System.out.println(subtasksByEpicId);
        System.out.println();

        System.out.println("Тест удаления задачи по id");

        taskManager.removeTaskById(strollTask.getId());
        taskManager.removeEpicById(pigeonsEpic.getId());
        taskManager.removeSubtaskById(breadSubtask.getId());

        tasks = taskManager.getTasks();
        System.out.println(tasks);
        epics = taskManager.getEpics();
        System.out.println(epics);
        subtasks = taskManager.getSubtasks();
        System.out.println(subtasks);
        System.out.println();

        System.out.println("Тест удаления всех задач");

        taskManager.deleteAllTasks();

        tasks = taskManager.getTasks();
        System.out.println(tasks);
        epics = taskManager.getEpics();
        System.out.println(epics);
        subtasks = taskManager.getSubtasks();
        System.out.println(subtasks);
    }
}
