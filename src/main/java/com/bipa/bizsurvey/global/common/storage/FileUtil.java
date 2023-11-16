package com.bipa.bizsurvey.global.common.storage;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Log4j2
public class FileUtil {
    public static void remove(File file) throws IOException {
        if (file.isDirectory()) {
            removeDirectory(file);
        } else {
            removeFile(file);
        }
    }

    private static void removeDirectory(File directory) throws IOException {
        File[] files = directory.listFiles();
        for (File file : files) {
            remove(file);
        }
        removeFile(directory);
    }

    private static void removeFile(File file) throws IOException {
        if (file.delete()) {
            log.info("File [{}] delete success", file.getName());
            return;
        }
        throw new FileNotFoundException("File [" + file.getName() + "] delete fail");
    }

    public static void rename(File file) {
        if(file.isDirectory()) {
            renameDirectory(file);
        } else {
            renameFile(file);
        }
    }

    private static void renameDirectory(File directory) {
        File[] files = directory.listFiles();
        for (File file : files) {
            rename(file);
        }
    }

    private static void renameFile(File file) {
        String localFileName = file.getName();

        int uuidIndex = localFileName.indexOf("_") + 1;
        int extensionIndex = localFileName.lastIndexOf(".");

        String baseName = localFileName.substring(uuidIndex, extensionIndex);
        String extension = localFileName.substring(extensionIndex);
        String changedName = baseName.concat(extension);
        String path = file.getParent();

        int counter = 1;
        File renamedFile = new File(path, changedName);

        while(true) {
            if(renamedFile.exists()) {
                String tempName = baseName + "_" + counter++;
                renamedFile = new File(path, tempName.concat(extension));
            } else {
                break;
            }
        }
        file.renameTo(renamedFile);
    }

    public static void moveFilesToTopLevel(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    moveFilesToTopLevel(file);
                } else {
                    File newLocation = new File(directory.getParent().split("/")[0] + "/", file.getName());
                    file.renameTo(newLocation);
                }
            }
        }
    }
}

