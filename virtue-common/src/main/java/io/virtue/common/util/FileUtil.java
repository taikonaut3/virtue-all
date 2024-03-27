package io.virtue.common.util;

import io.virtue.common.exception.CommonException;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import static java.lang.String.format;

/**
 * Utility class for file operations.
 */
public final class FileUtil {

    /**
     * Write the content to the file.
     *
     * @param content    The content to write to the file.
     * @param targetFile The file to write the content to.
     */
    public static void writeLineFile(String content, File targetFile) {
        try {
            if (content.isEmpty()) {
                return;
            }
            if (!targetFile.exists()) {
                createFileWithDir(targetFile.getAbsolutePath());
            }
            try (RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
                 FileChannel channel = file.getChannel(); FileLock ignored = channel.lock()) {
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
            }
        } catch (Exception e) {
            throw new CommonException("Write file is failed", e);
        }
    }

    /**
     * Create File with dir.
     *
     * @param filePath The path of the file to create.
     */
    public static void createFileWithDir(String filePath) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        mkdir(parentDir);
        try {
            boolean success = file.createNewFile();
            if (!success) {
                throw new CommonException(format("Create file: %s is not success", filePath));
            }
        } catch (Exception e) {
            throw new CommonException(format("Create file: %s is failed", filePath), e);
        }
    }

    /**
     * Create dir.
     *
     * @param directory The directory to create.
     */
    private static void mkdir(File directory) {
        if (!directory.exists()) {
            boolean success = directory.mkdirs();
            if (!success) {
                throw new CommonException(format("Create dir: %s is not success", directory.getPath()));
            }
        }
    }

    private FileUtil() {
    }
}

