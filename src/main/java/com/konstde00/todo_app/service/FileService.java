package com.konstde00.todo_app.service;

import static com.amazonaws.services.s3.model.CannedAccessControlList.Private;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.konstde00.todo_app.domain.File;
import com.konstde00.todo_app.domain.User;
import com.konstde00.todo_app.domain.enums.ProfileImageOrigin;
import com.konstde00.todo_app.repository.rds.FileRepository;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileService {

  @Value("${aws.s3.bucket}")
  private String bucketName;

  private final AmazonS3 s3Client;
  private final UserService userService;
  private final FileRepository fileRepository;

  public FileService(
      AmazonS3 s3Client, @Lazy UserService userService, FileRepository fileRepository) {
    this.s3Client = s3Client;
    this.userService = userService;
    this.fileRepository = fileRepository;
  }

  public String getUrl(File file) {

    if (file == null) {
      return null;
    }

    return Optional.ofNullable(file)
        .map(File::getOrigin)
        .map(
            origin ->
                switch (file.getOrigin()) {
                  case SSO_PROVIDER -> file.getUrl();
                  case CUSTOM_UPLOAD -> getPresignedUrl(file.getBucketName(), file.getKey());
                })
        .orElse(null);
  }

  public File saveAvatarFromIdp(String url) {

    File file = File.builder().origin(ProfileImageOrigin.SSO_PROVIDER).url(url).build();

    return fileRepository.saveAndFlush(file);
  }

  @SneakyThrows
  public String updateUsersAvatar(User user, MultipartFile photo) {

    String filePath = "users/" + user.getId() + "/avatar/" + photo.getOriginalFilename();

    File currentAvatar = user.getImage();

    if (currentAvatar == null || currentAvatar.getOrigin() == ProfileImageOrigin.SSO_PROVIDER) {

      com.konstde00.todo_app.domain.File file =
          com.konstde00.todo_app.domain.File.builder()
              .origin(ProfileImageOrigin.CUSTOM_UPLOAD)
              .bucketName(bucketName)
              .key(filePath)
              .contentType(photo.getContentType())
              .metadata(Map.of("user_id", user.getId()))
              .build();

      upload(filePath, photo);
      file = fileRepository.saveAndFlush(file);

      user.setImage(file);
      userService.saveAndFlush(user);

      return getPresignedUrl(bucketName, filePath);
    } else {

      currentAvatar.setOrigin(ProfileImageOrigin.CUSTOM_UPLOAD);
      currentAvatar.setBucketName(bucketName);
      currentAvatar.setKey(filePath);
      currentAvatar.setContentType(photo.getContentType());
      currentAvatar.setMetadata(Map.of("user_id", user.getId()));

      upload(filePath, photo);
      fileRepository.saveAndFlush(currentAvatar);
    }

    return getPresignedUrl(bucketName, filePath);
  }

  @SneakyThrows
  public void upload(String path, MultipartFile file) {

    log.info("'upload' invoked with path - {} and file - {} ", path, file);

    var metadata = createMetadata(file);

    s3Client.putObject(
        new PutObjectRequest(bucketName, path, file.getInputStream(), metadata)
            .withCannedAcl(Private));
  }

  @SneakyThrows
  private ObjectMetadata createMetadata(MultipartFile multipartFile) {

    var metadata = new ObjectMetadata();
    metadata.setContentLength(multipartFile.getInputStream().available());

    if (multipartFile.getContentType() != null && !"".equals(multipartFile.getContentType())) {
      metadata.setContentType(multipartFile.getContentType());
    }

    return metadata;
  }

  public String getPresignedUrl(String bucketName, String objectKey) {

    java.util.Date expiration = new java.util.Date();
    long expTimeMillis = expiration.getTime();
    expTimeMillis += 1000 * 60 * 60;
    expiration.setTime(expTimeMillis);

    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(bucketName, objectKey)
            .withMethod(HttpMethod.GET)
            .withExpiration(expiration);
    URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

    return url.toString();
  }
}
