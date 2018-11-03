package com.example.sayyad.exotest.Util;

import com.example.sayyad.exotest.Player.Model.Categories;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    public static void write(String path,String name,Categories categories) {
        FileWriter fileWriter = null;
        try {
            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            fileWriter = getFileWriter(path,name);
            gson.toJson(categories,fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Categories read(String path, String name) {
        Categories categories = null;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = getFileReader(path,name);

            if (fileReader == null)
                return null;

            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            bufferedReader = new BufferedReader(fileReader);
            categories = gson.fromJson(bufferedReader,Categories.class);

            bufferedReader.close();
            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return categories;
    }

    private static FileWriter getFileWriter(String basePath, String name) throws IOException {

        File baseDir = new File(basePath);

        File newDir = new File(baseDir,"MediaPlayerFiles");
        if (!newDir.exists()){
            newDir.mkdir();
        }
        File file = new File(newDir,name);
        return new FileWriter(file);
    }

    private static FileReader getFileReader(String basePath, String name) throws IOException {
        File baseDir = new File(basePath);
        File newDir = new File(baseDir,"MediaPlayerFiles");
        File file = new File(newDir,name);
        if (file.exists()){
            return new FileReader(file);
        }
        return null;
    }
}
