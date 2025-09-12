package me.solarbam.apollo.apolloCore.utils;

import java.io.*;

public final class FileUtils {

    private FileUtils() {}

    public static void copyFile(File source, File target) {
        try (InputStream input = new FileInputStream(source);
             OutputStream output = new FileOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyDirectory(File source, File target) {
        if (!target.exists()) target.mkdirs();
        File[] files = source.listFiles();
        if (files != null) {
            for (File file : files) {
                File targetFile = new File(target, file.getName());
                if (file.isDirectory()) {
                    copyDirectory(file, targetFile);
                } else {
                    copyFile(file, targetFile);
                }
            }
        }
    }

    public static void deleteFile(File file) {
        file.delete();
    }

    public static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    deleteFile(file);
                }
            }
        }
        directory.delete();
    }
}

