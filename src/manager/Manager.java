package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import manager.historyManager.HistoryManager;
import manager.historyManager.InMemoryHistoryManager;
import manager.taskManagers.FileBackedTasksManager;
import manager.taskManagers.HttpTaskManager;
import manager.taskManagers.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Manager {
    public static TaskManager getDefault() {
        String url = "http://localhost:8078";
        return new HttpTaskManager(url);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefaultFileBacked(String path) {
        return new FileBackedTasksManager(path);
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy/HH:mm");

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.value("");
                return;
            }
            jsonWriter.value(localDateTime.format(formatter));
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            String localDateTimeString = jsonReader.nextString();
            if(localDateTimeString == null || localDateTimeString.equals("")) {
                return null;
            }
            return LocalDateTime.parse(localDateTimeString, formatter);
        }
    }
}