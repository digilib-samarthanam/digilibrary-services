package com.samarthanam.digitallibrary.controller;

import com.samarthanam.digitallibrary.dto.request.UserSignupRequestDto;
import com.samarthanam.digitallibrary.dto.response.UserSignupResponseDto;
import com.samarthanam.digitallibrary.model.User;
import com.samarthanam.digitallibrary.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/signup", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity<UserSignupResponseDto> signUpUser(@RequestBody UserSignupRequestDto userSignupRequestDto) {

        if (StringUtils.isBlank(userSignupRequestDto.getFirstName()) || StringUtils.isBlank(userSignupRequestDto.getEmailAddress())
        || StringUtils.isBlank(userSignupRequestDto.getPassword())) {
            //TODO: throw bad request exception
        }
        User user = new User(userSignupRequestDto.getFirstName(), userSignupRequestDto.getLastName(),
                userSignupRequestDto.getMobileNumber(), userSignupRequestDto.getEmailAddress(), userSignupRequestDto.getPassword());
        UserSignupResponseDto userSignupResponseDto = userService.signUp(user);
        return new ResponseEntity(userSignupResponseDto, HttpStatus.OK);
    }


}
