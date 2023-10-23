package com.konstde00.todo_app.service;

import static com.amazonaws.services.s3.model.CannedAccessControlList.Private;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.FileInputStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileService {

  @Value("${aws.s3.bucket}")
  private String bucketName;

  @Value("${aws.s3.endpoint}")
  private String endpoint;

  private final AmazonS3 s3Client;

  @SneakyThrows
  public String updateUsersAvatar(String userId, MultipartFile photo) {

    String filePath = "users/" + userId + "/avatar";

    return upload(filePath, photo);
  }

  @SneakyThrows
  public String upload(String path, MultipartFile file) {

    log.info("'upload' invoked with path - {} and file - {} ", path, file);

    var metadata = createMetadata(file);

    var outerUrl = "https://" + bucketName + "." + endpoint + "/";
    var url = outerUrl + path;

    s3Client.putObject(
        new PutObjectRequest(bucketName, path, file.getInputStream(), metadata)
            .withCannedAcl(Private));

    return url;
  }

  @SneakyThrows
  private ObjectMetadata createMetadata(File file) {

    log.info("'createMetadata' invoked");

    var metadata = new ObjectMetadata();
    metadata.setContentLength(new FileInputStream(file).available());
    metadata.setContentType("image/x-png");
    log.info("'createMetadata' returned 'Success'");

    return metadata;
  }

  @SneakyThrows
  private ObjectMetadata createMetadata(MultipartFile multipartFile) {

    log.info("'createMetadata' invoked");

    var metadata = new ObjectMetadata();
    metadata.setContentLength(multipartFile.getInputStream().available());

    if (multipartFile.getContentType() != null && !"".equals(multipartFile.getContentType())) {
      metadata.setContentType(multipartFile.getContentType());
    }

    log.info("'createMetadata' returned 'Success'");

    return metadata;
  }
}
