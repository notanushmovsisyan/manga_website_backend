package org.manga_backend.catalogms.api.exception;

public class MangaNotFoundException extends RuntimeException {
    public MangaNotFoundException(String message) {
        super(message);
    }
}