import com.practicum.TaskManager.model.Epic;
import com.practicum.TaskManager.model.Status;
import com.practicum.TaskManager.model.Subtask;
import com.practicum.TaskManager.model.Task;
import com.practicum.TaskManager.service.InMemoryTaskManager;
import com.practicum.TaskManager.service.Managers;
import com.practicum.TaskManager.service.TaskManager;

import javax.xml.transform.Source;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        System.out.println("Тест создания и получения списка всех задач");

        Task strollTask = new Task("прогулка", "сходить погулять", Status.NEW);
        taskManager.createTask(strollTask);
        Task strollTask2 = new Task("прогулка", "сходить погулять", Status.NEW);
        taskManager.createTask(strollTask2);

        Task trashTask = new Task("мусор", "выкинуть мусор", Status.NEW);
        taskManager.createTask(trashTask);


        Epic productsEpic = new Epic("купить продукты", "сходить в ближайший магазин во время прогулки");
        taskManager.createEpic(productsEpic);
        Subtask milkSubtask = new Subtask("купить молоко", "", Status.NEW, productsEpic.getId());
        taskManager.createSubtask(milkSubtask);
        Subtask breadSubtask = new Subtask("купить хлеб", "", Status.NEW, productsEpic.getId());
        taskManager.createSubtask(breadSubtask);

        Epic pigeonsEpic = new Epic("покормить голубей", "покормить голубей купленным в магазине хлебом");
        taskManager.createEpic(pigeonsEpic);
        Subtask feedSubtask = new Subtask("раскрошить хлеб", "что бы голуби смогли его съесть",
                Status.NEW, pigeonsEpic.getId());
        taskManager.createSubtask(feedSubtask);

        ArrayList<Task> tasks = taskManager.getTasks();
        System.out.println(tasks);
        ArrayList<Epic> epics = taskManager.getEpics();
        System.out.println(epics);
        ArrayList<Subtask> subtasks = taskManager.getSubtasks();
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

        Epic newProductsEpic = new Epic("купить продукты", "сходить в ближайший магазин во время прогулки");
        Subtask newbreadSubtask = new Subtask("купить хлеб", "", Status.NEW, productsEpic.getId());
        newProductsEpic.addSubtask(newbreadSubtask);
        Subtask cheapsSubtask = new Subtask("купить чипсы", "", Status.NEW, productsEpic.getId());
        newProductsEpic.addSubtask(cheapsSubtask);
        taskManager.updateEpic(newProductsEpic);
        System.out.println(taskManager.getEpicById(newProductsEpic.getId()));
        System.out.println(taskManager.getSubtaskById(breadSubtask.getId()));
        System.out.println(taskManager.getSubtaskById(cheapsSubtask.getId()));
        System.out.println(taskManager.getSubtaskById(milkSubtask.getId()));
        System.out.println();

        System.out.println("Тест получения списка всех подзадач определеноого эпика");

        ArrayList<Subtask> subtasksByEpicId = taskManager.getSubtasksOfEpic(1007592185);
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
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();

        tasks = taskManager.getTasks();
        System.out.println(tasks);
        epics = taskManager.getEpics();
        System.out.println(epics);
        subtasks = taskManager.getSubtasks();
        System.out.println(subtasks);
    }
}
