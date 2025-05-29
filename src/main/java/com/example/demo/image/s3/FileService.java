package com.example.demo.image.s3;

import com.example.demo.base.api.ApiResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface FileService {

    // 단일 파일 업로드
    ApiResponse<FileDTO> uploadFile(String folderName, String fileName, MultipartFile file);

    // 다중 파일 업로드
    ApiResponse<List<FileDTO>> uploadFiles(String folderName, List<MultipartFile> files);

    // 파일 삭제
    ApiResponse<String> deleteFile(String fileName);

    // 폴더 삭제
    ApiResponse<String> deleteFolder(String folderName);

    // 파일 업데이트
    ApiResponse<FileDTO> updateFile(String fileName, MultipartFile file);
}