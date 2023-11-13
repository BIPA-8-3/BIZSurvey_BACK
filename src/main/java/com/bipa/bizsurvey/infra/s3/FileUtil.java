package com.bipa.bizsurvey.infra.s3;

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

    public static void removeDirectory(File directory) throws IOException {
        File[] files = directory.listFiles();
        for (File file : files) {
            remove(file);
        }
        removeFile(directory);
    }

    public static void removeFile(File file) throws IOException {
        if (file.delete()) {
            log.info("File [" + file.getName() + "] delete success");
            return;
        }
        throw new FileNotFoundException("File [" + file.getName() + "] delete fail");
    }
}

