import com.practicum.TaskManager.model.Epic;
import com.practicum.TaskManager.model.Status;
import com.practicum.TaskManager.model.Subtask;
import com.practicum.TaskManager.model.Task;
import com.practicum.TaskManager.service.Managers;
import com.practicum.TaskManager.service.TaskManager;

import java.util.List;

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

        List<Task> tasks = taskManager.getTasks();
        System.out.println(tasks);
        List<Epic> epics = taskManager.getEpics();
        System.out.println(epics);
        List<Subtask> subtasks = taskManager.getSubtasks();
        System.out.println(subtasks);
        System.out.println();

        System.out.println("Тест получения задачи по id");

        Task taskById = taskManager.getTaskById(1059876834);
        System.out.println(taskById);
        Epic epicById = taskManager.getEpicById(1007592185);
        System.out.println(epicById);
        Subtask subtaskById = taskManager.getSubtaskById(2000315197);
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

        List<Subtask> subtasksByEpicId = taskManager.getSubtasksOfEpic(1007592185);
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
        System.out.println();

        System.out.println("Дополнительное задание шестого спринта:");

        TaskManager newTaskManager = Managers.getDefault();

        Epic epicWithSubtasks = new Epic("Эпик с задачами", "");
        newTaskManager.createEpic(epicWithSubtasks);
        Subtask epicssubtask = new Subtask("Подзадача 1", "", Status.NEW, epicWithSubtasks.getId());
        newTaskManager.createSubtask(epicssubtask);
        Subtask epicssubtask2 = new Subtask("Подзадача 2", "", Status.NEW, epicWithSubtasks.getId());
        newTaskManager.createSubtask(epicssubtask2);
        Subtask epicssubtask3 = new Subtask("Подзадача 3", "", Status.NEW, epicWithSubtasks.getId());
        newTaskManager.createSubtask(epicssubtask3);

        Epic epicWithoutSubtasks = new Epic("Эпик без задач", "");
        newTaskManager.createEpic(epicWithoutSubtasks);

        System.out.println("1. Вызываем все задачи и выводим их");

        newTaskManager.getEpicById(epicWithSubtasks.getId());
        System.out.println(newTaskManager.getHistory());
        newTaskManager.getEpicById(epicWithoutSubtasks.getId());
        System.out.println(newTaskManager.getHistory());
        newTaskManager.getSubtaskById(epicssubtask.getId());
        System.out.println(newTaskManager.getHistory());
        newTaskManager.getSubtaskById(epicssubtask2.getId());
        System.out.println(newTaskManager.getHistory());
        newTaskManager.getSubtaskById(epicssubtask3.getId());
        System.out.println(newTaskManager.getHistory());
        System.out.println();

        System.out.println("2. Пробуем вызывать задачи повторно");

        newTaskManager.getEpicById(epicWithoutSubtasks.getId());
        System.out.println(newTaskManager.getHistory());
        newTaskManager.getSubtaskById(epicssubtask2.getId());
        System.out.println(newTaskManager.getHistory());
        newTaskManager.getEpicById(epicWithSubtasks.getId());
        System.out.println(newTaskManager.getHistory());
        newTaskManager.getEpicById(epicWithoutSubtasks.getId());
        System.out.println(newTaskManager.getHistory());
        System.out.println();

        System.out.println("3. Пробуем удалить эпик без задач");

        newTaskManager.removeEpicById(epicWithoutSubtasks.getId());
        System.out.println(newTaskManager.getHistory());
        System.out.println();

        System.out.println("4. Пробуем удалить эпик c подзадачами");

        newTaskManager.removeEpicById(epicWithSubtasks.getId());
        System.out.println(newTaskManager.getHistory());
    }
}
