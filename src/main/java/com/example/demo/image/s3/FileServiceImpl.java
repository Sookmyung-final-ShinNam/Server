package com.example.demo.image.s3;

import com.amazonaws.AmazonServiceException;
import com.example.demo.base.api.ApiResponse;
import com.example.demo.base.api.status.ErrorStatus;
import com.example.demo.base.api.status.SuccessStatus;
import com.example.demo.base.api.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private final S3Uploader s3Uploader;
    private final FileRepository fileRepository;
    private final FileConverter fileConverter;

    @Override
    public ApiResponse<FileDTO> uploadFile(String folderName, String fileName, MultipartFile file) {
        try {
            // 파일 업로드 및 엔티티 생성
            FileEntity fileEntity = s3Uploader.uploadFile(folderName, fileName, file, "upload");
            fileRepository.save(fileEntity);  // 데이터베이스에 파일 정보 저장

            return ApiResponse.of(SuccessStatus._OK, fileConverter.toDTO(fileEntity));  // DTO 반환
        } catch (Exception e) {
            log.error("파일 업로드 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.INVALID_REQUEST);
        }
    }

    @Override
    public ApiResponse<List<FileDTO>> uploadFiles(String folderName, List<MultipartFile> files) {
        try {
            // 다중 파일 업로드 처리
            List<FileEntity> fileEntities = files.stream()
                    .map(file -> s3Uploader.uploadFile(folderName, file.getOriginalFilename(), file, "upload"))
                    .peek(fileRepository::save)  // 파일 엔티티를 데이터베이스에 저장
                    .collect(Collectors.toList());

            List<FileDTO> fileDTOs = fileEntities.stream().map(fileConverter::toDTO).collect(Collectors.toList());
            return ApiResponse.of(SuccessStatus._OK, fileDTOs);  // DTO 리스트 반환
        } catch (Exception e) {
            log.error("다중 파일 업로드 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.INVALID_REQUEST);
        }
    }

    @Override
    public ApiResponse<String> deleteFile(String fileName) {
        FileEntity file = fileRepository.findByFileName(fileName)
                .orElseThrow(() -> new CustomException(ErrorStatus.FILE_NOT_FOUND));

        try {
            String s3FileName = fileName ;
            s3Uploader.deleteFile(s3FileName);  // S3에서 파일 삭제

            // 데이터베이스에서 파일 삭제
            fileRepository.delete(file);
            log.info("파일 삭제 완료 : ", fileName);

            return ApiResponse.of(SuccessStatus._OK, null);
        } catch (AmazonServiceException e) {
            log.error("S3 삭제 실패 : ", fileName, e.getMessage(), e);
            throw new CustomException(ErrorStatus.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("파일 삭제 실패 : ", fileName, e.getMessage(), e);
            throw new CustomException(ErrorStatus.INVALID_REQUEST);
        }
    }

    @Override
    public ApiResponse<String> deleteFolder(String folderName) {
        try {
            // 폴더 내 모든 파일 삭제
            List<String> filesInFolder = s3Uploader.getFilesInFolder(folderName);

            if (!filesInFolder.isEmpty()) {
                for (String fileName : filesInFolder) {
                    // 각 파일 삭제
                    s3Uploader.deleteFile(fileName);  // S3에서 파일 삭제
                    log.info("S3 파일 삭제 완료: {}", fileName);

                    // 데이터베이스에서 파일 삭제
                    FileEntity file = fileRepository.findByFileName(fileName)
                            .orElseThrow(() -> new CustomException(ErrorStatus.FILE_NOT_FOUND));
                    fileRepository.delete(file);  // 데이터베이스에서 파일 삭제
                    log.info("데이터베이스에서 파일 삭제 완료: {}", fileName);
                }
            }

            return ApiResponse.of(SuccessStatus._OK, null);
        } catch (AmazonServiceException e) {
            log.error("S3 폴더 삭제 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("폴더 삭제 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.INVALID_REQUEST);
        }
    }

    @Override
    public ApiResponse<FileDTO> updateFile(String fileName, MultipartFile file) {
        FileEntity existingFile = fileRepository.findByFileName(fileName)
                .orElseThrow(() -> new CustomException(ErrorStatus.FILE_NOT_FOUND));

        try {
            // 기존 파일 삭제
            String s3FileName = fileName;
            s3Uploader.deleteFile(s3FileName);  // S3에서 파일 삭제

            String folderName = fileName.split("/")[0];

            // 새로운 파일 업로드 및 엔티티 업데이트
            FileEntity updatedFile = s3Uploader.uploadFile(folderName, fileName, file, "update");
            existingFile.update(updatedFile);  // 엔티티 업데이트
            fileRepository.save(existingFile);  // 데이터베이스에 업데이트된 파일 저장

            return ApiResponse.of(SuccessStatus._OK, fileConverter.toDTO(existingFile));
        } catch (Exception e) {
            log.error("파일 업데이트 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorStatus.INVALID_REQUEST);
        }
    }

}