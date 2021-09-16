package com.samarthanam.digitallibrary.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.samarthanam.digitallibrary.dto.EmailSenderDto;
import com.samarthanam.digitallibrary.enums.EmailTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmailSenderService {

    // Replace sender@example.com with your "From" address.
    // This address must be verified with Amazon SES.
    //TODO: move to application properties
    private final String FROM = "samarthanam.digilibrary@gmail.com";

    /**
     * To email will only be sent to the verified emails on aws console if the account is in sandbox.
     *
     * @param emailSenderDto
     */
    public void sendEmailToUser(EmailSenderDto emailSenderDto) {
        if (StringUtils.isBlank(emailSenderDto.getToEmail())) {
            log.error("Cannot send email to no one!");
            return;
        }
        String htmlBody;
        try {
            htmlBody = getHtmlbody(emailSenderDto.getEmailTemplate());
            htmlBody = replaceTemplateVariables(emailSenderDto.getTemplateData(), htmlBody);
        } catch (IOException e) {
            log.error("Error reading HTML Template: {}", emailSenderDto.getEmailTemplate());
            e.printStackTrace();
            //throw EmailNotSentExpection;
            return;
        }
        final String subject = emailSenderDto.getEmailTemplate().getSubject();
        sendEmailViaSES(emailSenderDto.getToEmail(), htmlBody, subject);
    }

    private String getHtmlbody(EmailTemplate emailTemplate) throws IOException {
        String fileName = emailTemplate.getTemplatePath();
//        ClassLoader classLoader = getClass().getClassLoader();
//        File file = new File(classLoader.getResource(fileName).getFile());
        //Read File Content
        return EmailSenderService.getResourceFileAsString(fileName);
    }

    private static String getResourceFileAsString(String fileName) {
        InputStream is = getResourceFileAsInputStream(fileName);
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return (String)reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } else {
            throw new RuntimeException("resource not found");
        }
    }

    private static InputStream getResourceFileAsInputStream(String fileName) {
        ClassLoader classLoader = EmailSenderService.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }

    private String replaceTemplateVariables(Map<String, String> dataSet, String htmlBody) {
        for (Map.Entry<String, String> env : dataSet.entrySet()) {
            htmlBody = htmlBody.replace(String.format("{{%s}}", env.getKey()), env.getValue());
        }
        return htmlBody;
    }

    private void sendEmailViaSES(String toEmail, String htmlBody, String subject) {
        try {
            AmazonSimpleEmailService client =
                    AmazonSimpleEmailServiceClientBuilder.standard()
                            // Replace US_WEST_2 with the AWS Region you're using for
                            // Amazon SES.
                            .withRegion(Regions.AP_SOUTH_1).build();
            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(toEmail))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withHtml(new Content()
                                            .withCharset("UTF-8").withData(htmlBody)))
                            .withSubject(new Content()
                                    .withCharset("UTF-8").withData(subject)))
                    .withSource(FROM);
            client.sendEmail(request);
            log.info("Email was sent to " + toEmail);
        } catch (Exception ex) {
            log.error("The email was not sent. Error message: {}"
                    , ex.getMessage(), ex);
            throw new ValidationException("Couldn't sent email to " + toEmail);
        }
    }
}
