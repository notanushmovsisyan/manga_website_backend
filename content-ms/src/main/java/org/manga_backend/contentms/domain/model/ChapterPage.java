package org.manga_backend.contentms.domain.model;

public class ChapterPage {
    private Long id;
    private Long chapterId;
    private Integer pageNumber;
    private String s3Url;

    public ChapterPage(Long chapterId, Integer pageNumber, String s3Url) {
        this.chapterId = chapterId;
        this.pageNumber = pageNumber;
        this.s3Url = s3Url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getS3Url() {
        return s3Url;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }
}
