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





}
