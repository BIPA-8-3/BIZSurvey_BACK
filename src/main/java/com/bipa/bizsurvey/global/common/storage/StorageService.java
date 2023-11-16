package com.bipa.bizsurvey.global.common.storage;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

public interface StorageService {
    Domain[] resizingexclusiondomains = { Domain.SURVEY };

    String uploadFile(MultipartFile file, Domain domain, String path);

    byte[] downloadFile(String fileName) throws IOException;

    void downloadZip(ZipOutputStream zipOut, String fileUrl) throws IOException;

    void deleteFile(String filePath);

    void deleteFolder(String folderPath);

    default String getOriginName(String saveName) {
        int uuidIndex = saveName.indexOf("_") + 1;
        String originName = saveName.substring(uuidIndex);
        return originName;
    }

    default String createFileName(String path, String fileName) {
        String baseName = fileName.substring(0, fileName.lastIndexOf("."));
        return path.concat(UUID.randomUUID().toString())
                .concat("_" + baseName)
                .concat(getFileExtension(fileName));
    }

    default boolean isImageFile(String fileName) {
        String fileExtension = getFileExtension(fileName);
        String[] imageExtensions = { ".jpeg", ".png", ".gif" };

        for (String extension : imageExtensions) {
            if (extension.equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    default String createPath(Domain domain, String basePath, String originName) {
        StringBuilder path = new StringBuilder();
        Folder folder = Folder.FILE;

        if (isImageFile(originName)) {
            if (getResizingCheck(domain)) {
                path.append("temp/");
            }
            folder = Folder.IMAGES;
        }
        path.append(domain.getDomainName()).append(basePath).append(folder.getFolderName());
        return path.toString();
    }

    default boolean getResizingCheck(Domain domain) {
        for (Domain item : resizingexclusiondomains) {
            if (item == domain) {
                return false;
            }
        }
        return true;
    }

    default MediaType getContentType(String keyName) {
        String type = getFileExtension(keyName);
        switch (type) {
            case ".txt":
                return MediaType.TEXT_PLAIN;
            case ".png":
                return MediaType.IMAGE_PNG;
            case ".jpeg":
                return MediaType.IMAGE_JPEG;
            case ".gif":
                return MediaType.IMAGE_GIF;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    default String getFileExtension(String fileName) {
        try {
            String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            return extension.equals(".jpg") ? ".jpeg" : extension;
        } catch (StringIndexOutOfBoundsException se) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }

    default String extractFileName(String uri, String regex) {
        String[] split = uri.split(regex);
        return split.length < 2 ? "" : URLDecoder.decode(split[1], StandardCharsets.UTF_8);
    }
}