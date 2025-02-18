package com.ej.hgj.entity.file;

import lombok.Data;

@Data
public class FileMessage {

    private String filePath;
    private String fileName;
    private byte[] fileBytes;

    public FileMessage(String filePath, String fileName, byte[] fileBytes) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileBytes = fileBytes;
    }
}
