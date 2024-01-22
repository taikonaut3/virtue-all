package io.github.astro.virtue.common.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public interface FileUtil {

    static void writeLineFile(String content, File targetFile) {
        try {
            if (content.isEmpty()) {
                return;
            }
            if (!targetFile.exists()) {
                FileUtil.createFileWithParentDirectory(targetFile.getAbsolutePath());
            }
            try (RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
                 FileChannel channel = file.getChannel(); FileLock lock = channel.lock()) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = file.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                String fileContents = sb.toString();

                if (!fileContents.contains(content)) {
                    fileContents += content + "\n";
                }
                file.setLength(0);
                file.write(fileContents.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void createFileWithParentDirectory(String filePath) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        createParentDirectories(parentDir);
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createParentDirectories(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File parentDir = directory.getParentFile();
        if (parentDir != null) {
            createParentDirectories(parentDir);
        }
    }

}
