package org.manga_backend.contentms.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name="pages")
public class ChapterPageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="chapter_id", nullable = false)
    private Long chapterId;

    @Column(name="page_number", nullable = false)
    private Integer pageNumber;

    @Column(name="s3_url", nullable = false)
    private String s3Url;

    public ChapterPageEntity() {}

    public ChapterPageEntity(Long chapterId, Integer pageNumber, String s3Url) {
        this.chapterId = chapterId;
        this.pageNumber = pageNumber;
        this.s3Url = s3Url;
    }

    public Long getId() {
        return id;
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
