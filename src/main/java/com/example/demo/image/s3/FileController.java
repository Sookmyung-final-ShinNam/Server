package com.example.demo.image.s3;

import com.example.demo.base.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // 단일 파일 업로드
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "단일 파일 업로드")
    public ApiResponse<FileDTO> uploadFile(
            @RequestParam String folderName,
            @RequestParam String fileName,
            @RequestParam MultipartFile file) {
        ApiResponse<FileDTO> response = fileService.uploadFile(folderName, fileName, file);
        return response;
    }

    // 다중 파일 업로드
    @PostMapping(value = "/upload/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "다중 파일 업로드")
    public ApiResponse<List<FileDTO>> uploadFiles(
            @RequestParam String folderName,
            @RequestParam List<MultipartFile> files) {
        ApiResponse<List<FileDTO>> response = fileService.uploadFiles(folderName, files);
        return response;
    }

    // 파일 삭제
    @DeleteMapping("/delete/file")
    @Operation(summary = "파일 삭제")
    public ApiResponse<String> deleteFile(
            @RequestParam String fileName) {
        ApiResponse<String> response = fileService.deleteFile(fileName);
        return response;
    }

    // 폴더 삭제
    @DeleteMapping("/delete/folder")
    @Operation(summary = "폴더 삭제")
    public ApiResponse<String> deleteFolder(
            @RequestParam String folderName) {
        ApiResponse<String> response = fileService.deleteFolder(folderName);
        return response;
    }

    // 파일 업데이트
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "파일 업데이트")
    public ApiResponse<FileDTO> updateFile(
            @RequestParam String fileName,
            @RequestParam MultipartFile file) {
        ApiResponse<FileDTO> response = fileService.updateFile(fileName, file);
        return response;
    }

}