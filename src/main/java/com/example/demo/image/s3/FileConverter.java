package com.example.demo.image.s3;

import org.springframework.stereotype.Component;

@Component
public class FileConverter {

    // 엔티티 -> DTO 변환
    public FileDTO toDTO(FileEntity entity) {
        return FileDTO.builder()
                .folderName(entity.getFolderName())
                .fileName(entity.getFileName())
                .s3Url(entity.getS3Url())
                .contentType(entity.getContentType())
                .size(entity.getSize())
                .build();
    }

}