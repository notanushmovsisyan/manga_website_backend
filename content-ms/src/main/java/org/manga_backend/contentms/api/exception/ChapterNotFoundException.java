package org.manga_backend.contentms.api.exception;

public class ChapterNotFoundException extends RuntimeException {
    public ChapterNotFoundException(String message) {
        super(message);
    }
}
