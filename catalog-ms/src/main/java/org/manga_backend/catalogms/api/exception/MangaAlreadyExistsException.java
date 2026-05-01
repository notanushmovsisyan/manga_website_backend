package org.manga_backend.catalogms.api.exception;

public class MangaAlreadyExistsException extends RuntimeException {
    public MangaAlreadyExistsException(String message) {
        super(message);
    }
}