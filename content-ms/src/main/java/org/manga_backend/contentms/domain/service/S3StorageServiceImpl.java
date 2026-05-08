package org.manga_backend.contentms.domain.service;

import org.manga_backend.contentms.api.dto.PresignedUrlRequest;
import org.manga_backend.contentms.api.dto.PresignedUrlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3StorageServiceImpl {
    private final S3Presigner s3Presigner;
    private final S3Client s3Client; // Added for actual S3 operations

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private static final Logger log = LoggerFactory.getLogger(S3StorageServiceImpl.class);

    public S3StorageServiceImpl(S3Presigner s3Presigner, S3Client s3Client) {
        this.s3Presigner = s3Presigner;
        this.s3Client = s3Client;
    }

    public void deleteFolder(String prefix) {
        String continuationToken = null;

        try {
            do {
                // 1. Build the list request with the continuation token for pagination
                ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .continuationToken(continuationToken)
                        .build();

                ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

                if (listResponse.contents().isEmpty()) {
                    log.debug("No S3 objects found for prefix={}", prefix);
                    break;
                }

                // 2. Prepare keys to delete
                List<ObjectIdentifier> keysToDelete = listResponse.contents().stream()
                        .map(s3Object -> ObjectIdentifier.builder().key(s3Object.key()).build())
                        .collect(Collectors.toList());

                // 3. Execute batch delete
                DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                        .bucket(bucketName)
                        .delete(Delete.builder().objects(keysToDelete).build())
                        .build();

                s3Client.deleteObjects(deleteRequest);

                // 4. Check if there are more pages of results
                continuationToken = listResponse.nextContinuationToken();

            } while (continuationToken != null);

            log.info("S3 folder deletion completed prefix={}", prefix);

        } catch (S3Exception e) {
            // necessarily crash your whole DB transaction unless required
            log.error("S3 folder deletion failed prefix={} bucket={}", prefix, bucketName, e);
        }
    }

    public List<PresignedUrlResponse> generateUploadUrls(Long mangaId,
                                                         Long chapterId,
                                                         PresignedUrlRequest request) {

        if (request.getOriginalFileNames() == null) {
            log.warn("No file names provided for presigned URL generation");
            return Collections.emptyList();
        }

        List<PresignedUrlResponse> responses = new ArrayList<>();
        List<String> fileNames = request.getOriginalFileNames();

        for (int i = 0; i < fileNames.size(); i++) {
            String name = fileNames.get(i);
            String extension = "";
            int lastDotIndex = name.lastIndexOf(".");
            if (lastDotIndex != -1) {
                extension = name.substring(lastDotIndex);
            }

            int pageNumber = i + 1;
            String paddedNum = String.format("%04d", pageNumber);
            String s3Key = String.format("mangas/%d/chapters/%d/%s%s",
                    mangaId, chapterId, paddedNum, extension);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .putObjectRequest(putObjectRequest)
                    .build();

            String uploadUrl = s3Presigner.presignPutObject(presignRequest).url().toString();
            responses.add(new PresignedUrlResponse(name, s3Key, uploadUrl));
        }

        log.info("Presigned URL generation completed mangaId={} chapterId={} count={}",
                mangaId,
                chapterId,
                responses.size()
        );
        return responses;
    }
}