package com.samarthanam.digitallibrary.dto;

import com.samarthanam.digitallibrary.enums.EmailTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class EmailSenderDto {

    private String toEmail;
    private EmailTemplate emailTemplate;
    private Map<String, String> templateData;

}
