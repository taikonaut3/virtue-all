package io.virtue.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public final class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {
    }

    public static void writeLineFile(String content, File targetFile) {
        try {
            if (content.isEmpty()) {
                return;
            }
            if (!targetFile.exists()) {
                createFileWithParentDirectory(targetFile.getAbsolutePath());
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
            }
        } catch (Exception e) {
            logger.error("Write file is failed", e);
        }
    }

    public static void createFileWithParentDirectory(String filePath) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        createParentDirectories(parentDir);
        try {
            boolean success = file.createNewFile();
            if (!success) {
                logger.warn("Create file is not success");
            }
        } catch (Exception e) {
            logger.error("Create file is failed", e);
        }
    }

    private static void createParentDirectories(File directory) {
        if (!directory.exists()) {
            boolean success = directory.mkdirs();
            if (!success) {
                logger.warn("Create Parent Directories is not success");
            }
        }
        File parentDir = directory.getParentFile();
        if (parentDir != null) {
            createParentDirectories(parentDir);
        }
    }

}
