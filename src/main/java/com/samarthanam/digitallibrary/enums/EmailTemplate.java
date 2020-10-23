package com.samarthanam.digitallibrary.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EmailTemplate {

    SIGNUP_VERIFY("templates/email/signup_verify.html", "Please verify your email"),
    FORGOT_PASSWORD("templates/email/forgot_password.html", "A link to change your password!");

    private final String templatePath;
    private final String subject;
}

