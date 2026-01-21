package hytale.doryanbessiere.villager.utils;

import com.google.gson.*;
import hytale.doryanbessiere.villager.HytaleVillager;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FileStorageManager<T> {

    private final File storageDir;
    private final Gson gson;

    public FileStorageManager(String storageDir) {
        this.storageDir = new File(storageDir);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (src, type, ctx) ->
                                new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, ctx) ->
                                LocalDateTime.parse(json.getAsString()))
                .create();

        if (!this.storageDir.exists()) {
            HytaleVillager.logger().atInfo().log("Creating storage directory at: " + this.storageDir.getAbsolutePath());
            if (!this.storageDir.mkdirs())
                throw new RuntimeException("Failed to create storage directory: " + this.storageDir.getAbsolutePath());
        } else if (!this.storageDir.isDirectory()) {
            throw new RuntimeException("Storage path is not a directory: " + this.storageDir.getAbsolutePath());
        } else {
            HytaleVillager.logger().atInfo().log("Storage directory exists at: " + this.storageDir.getAbsolutePath());
        }
    }

    public void save(T data, String filename) {
        File file = new File(storageDir, filename);
        File parentDirectory = file.getParentFile();
        if (!parentDirectory.exists())
            if (!parentDirectory.mkdirs())
                throw new RuntimeException("Failed to create directories: " + parentDirectory.getAbsolutePath());

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(data, writer);
            HytaleVillager.logger().atInfo().log("Saved data to " + file.getAbsolutePath());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            HytaleVillager.logger().atSevere().log("Failed to save data to " + file.getAbsolutePath(), e);
        }
    }

    public void deleteStorageDir() {
        if (this.storageDir.exists() && this.storageDir.isDirectory()) {
            if (this.storageDir.renameTo(new File(this.storageDir.getAbsolutePath() + "_deleted"))) {
                HytaleVillager.logger().atInfo().log("Renamed storage directory for deletion: " + this.storageDir.getAbsolutePath());
            } else {
                HytaleVillager.logger().atSevere().log("Failed to rename storage directory for deletion: " + this.storageDir.getAbsolutePath());
            }
        }
    }

    public void delete(String filename) {
        File file = new File(storageDir, filename);
        if (file.exists()) {
            // Rename the file before deletion to avoid potential issues
            File tempFile = new File(file.getAbsolutePath() + ".deleted");
            if (file.renameTo(tempFile)) {
                HytaleVillager.logger().atInfo().log("Deleted file: " + tempFile.getAbsolutePath());
            } else {
                HytaleVillager.logger().atSevere().log("Failed to rename file for deletion: " + file.getAbsolutePath());
            }
        } else {
            HytaleVillager.logger().atWarning().log("File not found for deletion: " + file.getAbsolutePath());
        }
    }

    public T load(String filename, Class<T> clazz) {
        File file = new File(storageDir, filename);
        if (!file.exists()) {
            HytaleVillager.logger().atWarning().log("File not found: " + file.getAbsolutePath());
            return null;
        }

        try (Reader reader = new FileReader(file)) {
            T data = gson.fromJson(reader, clazz);
            HytaleVillager.logger().atInfo().log("Loaded data from " + file.getAbsolutePath());
            reader.close();
            return data;
        } catch (IOException e) {
            HytaleVillager.logger().atSevere().log("Failed to load data from " + file.getAbsolutePath(), e);
            return null;
        }
    }

    public ArrayList<T> loadAll(Class<T> clazz) {
        ArrayList<T> dataList = new ArrayList<>();
        File[] files = storageDir.listFiles();
        if (files == null) {
            HytaleVillager.logger().atWarning().log("No files found in storage directory: " + storageDir.getAbsolutePath());
            return dataList;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                File metadataFile = new File(file, "metadata.json");
                T data = load(file.getName() + "/metadata.json", clazz);
                if (data != null) {
                    dataList.add(data);
                }
            } else if (file.isFile() && file.getName().endsWith(".json")) {
                T data = load(file.getName(), clazz);
                if (data != null) {
                    dataList.add(data);
                }
            }
        }

        return dataList;
    }
}
