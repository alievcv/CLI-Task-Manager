
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;

public class App {
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        while (true) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter the command: ");
            String input = reader.readLine();
            if (input.startsWith("list")) {
                switch (input) {
                    case "list":
                        List<Task> tasks = parseJsonIntoObjecTasks();
                        for (Task task : tasks) {
                            if (task.getDescription().equals("sine")) {
                                task.setDescription("unsign");
                            }
                            System.out.println(task.toString());
                        }
                        break;

                    case "list done":
                        tasks = parseJsonIntoObjecTasks();
                        for (Task task : tasks) {
                            if (task.getStatus().equalsIgnoreCase("done")) {
                                System.out.println(task.toString());
                            }
                        }
                        break;

                    case "list todo":
                        tasks = parseJsonIntoObjecTasks();
                        for (Task task : tasks) {
                            if (task.getStatus().equalsIgnoreCase("todo")) {
                                System.out.println(task.toString());
                            }
                        }
                        break;

                    case "list in-progress":
                        tasks = parseJsonIntoObjecTasks();
                        for (Task task : tasks) {
                            if (task.getStatus().equalsIgnoreCase("in-progress")) {
                                System.out.println(task.toString());
                            }
                        }

                    default:
                        break;
                }

            } else {

                String[] command = input.split(" ");

                switch (command[0]) {
                    case "add":
                        String[] splittedInput = input.split(" ", 2);
                        Task task = new Task();
                        task.setId(Task.counterForTask);
                        task.setDescription(splittedInput[1]);
                        task.setStatus("todo");
                        task.setCreatedAt(LocalTime.now());
                        task.setUpdatedAt(LocalTime.now());
                        jsonWriter(task);
                        break;

                    case "update":
                        splittedInput = input.split(" ", 3);
                        List<Task> tasks = parseJsonIntoObjecTasks();
                        if (isInteger(splittedInput[1])) {
                            Integer id = Integer.valueOf(splittedInput[1]);
                            tasks.get(id).setDescription(splittedInput[2]);
                            tasks.get(id).setUpdatedAt(LocalTime.now());
                            jsonUpdater(tasks);

                        }
                        break;

                    case "delete":
                        tasks = parseJsonIntoObjecTasks();
                        splittedInput = input.split(" ", 2);
                        if (isInteger(splittedInput[1])) {
                            int id = Integer.parseInt(splittedInput[1]);
                            boolean removed = tasks.removeIf(t -> t.getId() == id);
                            if (removed) {
                                System.out.println("Task with id " + id + " was removed.");
                                jsonUpdater(tasks); // Save to file
                            } else {
                                System.out.println("No task with id " + id + " found.");
                            }
                        }
                        break;

                    case "mark-done":
                        splittedInput = input.split(" ", 3);
                        tasks = parseJsonIntoObjecTasks();
                        if (isInteger(splittedInput[1])) {
                            Integer id = Integer.valueOf(splittedInput[1]);
                            tasks.get(id).setStatus("done");
                            tasks.get(id).setUpdatedAt(LocalTime.now());
                            jsonUpdater(tasks);
                        }
                        break;

                    case "mark-in-progress":
                        splittedInput = input.split(" ", 3);
                        tasks = parseJsonIntoObjecTasks();
                        if (isInteger(splittedInput[1])) {
                            Integer id = Integer.valueOf(splittedInput[1]);
                            tasks.get(id).setStatus("in-progress");
                            tasks.get(id).setUpdatedAt(LocalTime.now());
                            jsonUpdater(tasks);
                        }
                        break;

                    default:
                        break;
                }

            }

        }

    }

    public static void jsonWriter(Task task) throws Exception {

        File file = new File("tasks.json");
        if (!file.exists()) {
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            fw.write("[]");
            fw.close();
        }

        Path path = Paths.get("tasks.json");
        String content = new String(Files.readAllBytes(path)).trim();
        int end = content.indexOf(']');
        content = content.substring(0, end);
        content += "\n" + task.toJson() + "\n]";
        Files.write(path, content.getBytes());

        System.out.println("Task added successfully (ID: " + task.getId() + ")");

    }

    // Rewrites tasks after upgrade - not efficient i know;((
    public static void jsonUpdater(List<Task> tasks) throws IOException {
        Path path = Paths.get("tasks.json");
        String content = "[";
        for (Task task : tasks) {
            content += task.toJson() + "\n";
        }
        content += "]";

        Files.write(path, content.getBytes());
        System.out.println("JSON UPDATER CONTENT:\n" + content);
    }

    // Assembles all objects after their converted from Json
    public static List<Task> parseJsonIntoObjecTasks() throws IOException {
        List<Task> tasks = loadTasksFromJsonFile("tasks.json");
        return tasks;
    }

    // Recieves path of Json file and converts them into big objects
    public static List<Task> loadTasksFromJsonFile(String path) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(path)));
        List<Task> tasks = new ArrayList<>();

        // Match each {...} block
        Pattern objectPattern = Pattern.compile("\\{.*?\\}", Pattern.DOTALL);
        Matcher matcher = objectPattern.matcher(json);

        while (matcher.find()) {
            String taskJson = matcher.group();
            Task task = parseJsonToTask(taskJson);
            if (task != null)
                tasks.add(task);
        }

        return tasks;
    }

    // Recievs big objects seperatly one-by-one and converts them from String to
    // actual Task objs
    public static Task parseJsonToTask(String json) {
        try {
            int id = Integer.parseInt(extractField(json, "id"));
            String description = extractField(json, "description");
            String status = extractField(json, "status");
            LocalTime createdAt = LocalTime.parse(extractField(json, "createdAt"));
            LocalTime updatedAt = LocalTime.parse(extractField(json, "updatedAt"));

            return new Task(id, description, status, createdAt, updatedAt);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to parse: " + json);
            return null;
        }
    }

    // Recieves obj in String format and extracts value from each field using regexp
    public static String extractField(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"?(.*?)\"?(,|})";
        Matcher matcher = Pattern.compile(pattern).matcher(json);
        if (matcher.find()) {
            return matcher.group(1).trim().replace("\"", "");
        }
        return null;
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
