package com.samarthanam.digitallibrary.constant;

public enum BookType {
    PDF("PDF", ".pdf"),
    AUDIO_BOOK("Audio Book", ".mp3");

    private String value;
    private String fileExtension;

    BookType (String value, String fileExtension) {
        this.value = value;
        this.fileExtension = fileExtension;
    }

    public String fileNameWithExtension(String fileName) {
        return fileName + fileExtension;
    }

    @Override
    public String toString() {
        return value;
    }

}
