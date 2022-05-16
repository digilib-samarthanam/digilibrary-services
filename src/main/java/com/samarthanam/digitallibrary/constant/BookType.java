package com.samarthanam.digitallibrary.constant;

public enum BookType {
    BOOK("BOOK", ".pdf"),
    AUDIBLE("AUDIBLE", ".mp3");

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
