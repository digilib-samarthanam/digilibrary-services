package com.samarthanam.digitallibrary.constant;

public enum BookType {
    PDF(".pdf"),
    AUDIO_BOOK(".mp3");

    private String fileExtension;

    BookType (String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String fileNameWithExtension(String fileName) {
        return fileName + fileExtension;
    }

}
