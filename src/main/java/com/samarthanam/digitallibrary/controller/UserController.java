package com.samarthanam.digitallibrary.controller;

import com.samarthanam.digitallibrary.dto.response.UserSignupResponseDto;
import com.samarthanam.digitallibrary.model.ErrorDetails;
import com.samarthanam.digitallibrary.model.User;
import com.samarthanam.digitallibrary.service.UserService;
import com.samarthanam.digitallibrary.util.UserValidation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


@RequestMapping(value = "digital_library/v1")
@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @Autowired
    UserValidation userValidation;

    @RequestMapping(path = "/signup", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity<UserSignupResponseDto> signUpUser(@RequestBody User userDetails) throws Exception {

        ErrorDetails errorDetails = new ErrorDetails();

        if(Objects.isNull(userDetails)){
            errorDetails=  new ErrorDetails("User Details are not found!", "USER_201");
            return new ResponseEntity(errorDetails, HttpStatus.NO_CONTENT);
        }

      errorDetails = userValidation.userPostValidation(userDetails);

        if(!StringUtils.isBlank(errorDetails.getMessage())){
            return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);

        }

            try {
                userService.signUp(userDetails);
            }catch (Exception ex)
            {
                return new ResponseEntity(ex.getMessage(), HttpStatus.CREATED);

            }

//        if (StringUtils.isBlank(userSignupRequestDto.getFirstName()) || StringUtils.isBlank(userSignupRequestDto.getEmailAddress())
//        || StringUtils.isBlank(userSignupRequestDto.getPassword())) {
//            //TODO: throw bad request exception
//        }
//        User user = new User(userSignupRequestDto.getFirstName(), userSignupRequestDto.getLastName(),
//                userSignupRequestDto.getMobileNumber(), userSignupRequestDto.getEmailAddress(), userSignupRequestDto.getPassword());
//        UserSignupResponseDto userSignupResponseDto = userService.signUp(user);
        return new ResponseEntity(" user account created", HttpStatus.CREATED);
    }


}
