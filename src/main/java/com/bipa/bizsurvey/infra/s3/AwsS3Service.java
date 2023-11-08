package com.bipa.bizsurvey.infra.s3;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AwsS3Service {
    /*
        이미지, 파일 폴더 구별하기
        썸네일 및 리사이징 처리
        https://wonkang.tistory.com/162
        이미지면 리사이징 썸네일 분기 타기
     */
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile multipartFile) {
        String fileName = createFilename(multipartFile.getOriginalFilename());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
        }

        return amazonS3Client.getUrl(bucket, fileName).toString();

        // 이미지 저장, postImages 테이블 in, fk post 값을 가지고 있다. S3컨트롤러
        // 리턴 값은 https://"bucket-name"."region".amazonaws.com/"파일 이름.확장자" 형식으로 저장
        //https://bizsurvey-bucket.s3.ap-northeast-2.amazonaws.com/README.md
        //"파일 이름.확장자"만 필요할 경우 return fileName; 으로 수정
    }

    public String uploadFile(InputStream is, ObjectMetadata ob, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, is, ob));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void deleteImage(String fileUrl) {
        // 이미지 경로가 URL 이기에 .com/ 기준 자르는 과정 추가
        String splitStr = ".com/";
        String fileName = fileUrl.substring(fileUrl.lastIndexOf(splitStr) + splitStr.length());

        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }


    //중복 방지를 위해 UUID 클래스를 이용하여 파일명 앞 부분을 고유 값으로 넣어줌
    private String createFilename(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // 확장자 가져오는 메소드
    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException se) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }
}


