package com.example.demo.image.s3;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDTO {

    private String folderName; // 폴더명
    private String fileName; // 파일명
    private String s3Url; // S3 URL
    private String contentType; // 파일 타입
    private Long size; // 파일 크기
}