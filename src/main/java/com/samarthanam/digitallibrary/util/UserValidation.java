package com.samarthanam.digitallibrary.util;

import com.samarthanam.digitallibrary.model.ErrorDetails;
import com.samarthanam.digitallibrary.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class UserValidation {

    public ErrorDetails userPostValidation(User user) {
        ErrorDetails errorDetails = new ErrorDetails();

        if (StringUtils.isBlank(user.getUserPassword())){
            return prepareErrorMessage("user password cannot blank!", "USER_101");
        } else if (StringUtils.isBlank(user.getFirstName())) {
            return prepareErrorMessage("user first name cannot blank!", "USER_102");
        }else if (StringUtils.isBlank(user.getLastName())) {
            return prepareErrorMessage("user last name cannot blank!", "USER_103");
        } else if(StringUtils.isBlank(user.getEmailAddress()) && !isEmailValid(user.getEmailAddress())) {
            return prepareErrorMessage("user email address cannot be blank or not in email format", "USER_104");
        }

        return  errorDetails;
    }


    private boolean isEmailValid(String str) {
        String pattern = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
        return str.matches(pattern);

    }


    // PREPARE ERROR MESSAGE OBJECT
    private ErrorDetails prepareErrorMessage(String message, String statusCode) {
        return new ErrorDetails(message, statusCode);
    }

    }



