package org.manga_backend.contentms.api.dto;

public class PresignedUrlResponse {
    private String originalFileName;
    private String s3Key;
    private String uploadUrl;

    public PresignedUrlResponse(String originalFileName, String s3Key, String uploadUrl) {
        this.originalFileName = originalFileName;
        this.s3Key = s3Key;
        this.uploadUrl = uploadUrl;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getS3Key() {
        return s3Key;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }
}
