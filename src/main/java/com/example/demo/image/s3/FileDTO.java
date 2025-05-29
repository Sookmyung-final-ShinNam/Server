package com.example.demo.image.s3;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDTO {

    private String folderName;
    private String fileName;
    private String s3Url;
    private String contentType;
    private Long size;
}