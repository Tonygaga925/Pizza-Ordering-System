package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T readFromFile(String filePath, Class<T> classOfT) throws IOException {
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, classOfT);
        }
    }

    public static <T> T readFromFile(String filePath, Type typeOfT) throws IOException {
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, typeOfT);
        }
    }

    public static void writeToFile(String filePath, Object obj) throws IOException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(obj, writer);
        }
    }
}