package com.example.demo.image.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.demo.base.api.status.ErrorStatus;
import com.example.demo.base.api.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 파일 업로드 메서드
    public FileEntity uploadFile(String folderName, String originalFileName, MultipartFile file, String mode) {
        String uuid = UUID.randomUUID().toString();  // 고유 번호 생성

        String s3FileName = originalFileName;
        if (!mode.equals("update")) {
            s3FileName = folderName + "/" + originalFileName + "_" + uuid;  // 고유한 파일명 생성
        }

        try {
            // Content-Type을 자동으로 설정 (파일의 MIME 타입에 따라)
            String contentType = file.getContentType();

            // ObjectMetadata 생성 후 Content-Type 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);

            // S3에 파일 업로드 (Content-Type을 포함)
            amazonS3.putObject(new PutObjectRequest(bucket, s3FileName, file.getInputStream(), metadata));

            // S3에 업로드된 파일의 URL을 생성
            String s3Url = amazonS3.getUrl(bucket, s3FileName).toString();
            log.info("파일 업로드 성공: {}", s3Url);

            // 파일 엔티티 반환 (S3 URL, 원본 파일명, UUID 등 포함)
            return FileEntity.builder()
                    .folderName(folderName)
                    .fileName(s3FileName)
                    .s3Url(s3Url)
                    .contentType(contentType)
                    .size(file.getSize())
                    .build();

        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.INVALID_REQUEST);
        }
    }

    // S3에서 파일 삭제
    public void deleteFile(String fileName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
            log.info("파일 삭제 성공: {}", fileName);
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.INVALID_REQUEST);
        }
    }

    // S3에서 폴더 내 모든 파일 가져오기
    public List<String> getFilesInFolder(String folderName) {
        List<String> fileNames = new ArrayList<>();

        try {
            ObjectListing objectListing = amazonS3.listObjects(new ListObjectsRequest()
                    .withBucketName(bucket)
                    .withPrefix(folderName + "/"));  // 해당 폴더 내의 모든 파일 목록 가져오기

            for (S3ObjectSummary summary : objectListing.getObjectSummaries()) {
                fileNames.add(summary.getKey());  // 파일 이름 리스트에 추가
            }
        } catch (AmazonServiceException e) {
            log.error("폴더 파일 목록 가져오기 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.FILE_NOT_FOUND);
        }
        return fileNames;
    }

}