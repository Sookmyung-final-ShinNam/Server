package com.example.demo.image.s3;

import com.example.demo.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 번호

    @Column(nullable = false)
    private String folderName; // 폴더명

    @Column(nullable = false)
    private String fileName; // 파일명

    @Column(nullable = false)
    private String s3Url; // S3에 저장된 파일 URL

    @Column(nullable = false)
    private String contentType; // 파일 타입

    @Column(nullable = false)
    private Long size; // 파일 크기 (바이트 단위)

    // 파일 업데이트 메서드
    public void update(FileEntity updatedFile) {
        this.folderName = updatedFile.getFolderName();
        this.fileName = updatedFile.getFileName();
        this.s3Url = updatedFile.getS3Url();
        this.contentType = updatedFile.getContentType();
        this.size = updatedFile.getSize();
    }

}