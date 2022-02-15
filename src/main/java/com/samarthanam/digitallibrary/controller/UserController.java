package com.samarthanam.digitallibrary.controller;

import com.samarthanam.digitallibrary.constant.RequestConstants;
import com.samarthanam.digitallibrary.dto.VerifySignUpDto;
import com.samarthanam.digitallibrary.dto.request.ForgotPasswordRequestDto;
import com.samarthanam.digitallibrary.dto.request.UpdatePasswordRequestDto;
import com.samarthanam.digitallibrary.dto.request.UserLoginRequestDto;
import com.samarthanam.digitallibrary.dto.request.UserSignupRequestDto;
import com.samarthanam.digitallibrary.dto.response.*;
import com.samarthanam.digitallibrary.exception.*;
import com.samarthanam.digitallibrary.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@ApiOperation("User management")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(path = RequestConstants.SIGNUP_PATH, consumes = RequestConstants.APPLICATION_JSON, method = RequestMethod.POST)
    public ResponseEntity<UserSignupResponseDto> signUpUser(@RequestBody @Valid UserSignupRequestDto userSignupRequestDto,
                                                            @RequestHeader(name = "origin", required = false, defaultValue = "http://localhost:3000") String origin)
            throws ConflictException, TokenCreationException, IOException {

        UserSignupResponseDto userSignupResponseDto = userService.signUp(userSignupRequestDto, origin);
        return new ResponseEntity<>(userSignupResponseDto, HttpStatus.CREATED);
    }

    @RequestMapping(path = RequestConstants.SIGNUP_VERIFY_PATH, method = RequestMethod.GET)
    public ResponseEntity<VerifySignUpResponseDto> verifySignUp(@RequestParam("token") String token)
            throws TokenExpiredException, TokenTemperedException, UnauthorizedException {
        VerifySignUpDto verifySignUpDto = new VerifySignUpDto(token);
        VerifySignUpResponseDto verifySignUpResponseDto = userService.verifySignUp(verifySignUpDto);
        return new ResponseEntity<>(verifySignUpResponseDto, HttpStatus.OK);
    }

    @RequestMapping(path = RequestConstants.LOGIN_PATH, consumes = RequestConstants.APPLICATION_JSON, method = RequestMethod.POST)
    public ResponseEntity<UserLoginResponseDto> loginUser(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto)
            throws UnauthorizedException, TokenCreationException {
        UserLoginResponseDto userLoginResponseDto = userService.login(userLoginRequestDto);
        return new ResponseEntity<>(userLoginResponseDto, HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = RequestConstants.FORGOT_PASSWORD_PATH, consumes = RequestConstants.APPLICATION_JSON, method = RequestMethod.POST)
    public ResponseEntity<ForgotPasswordResponseDto> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDto forgotPasswordRequestDto,
                                                                    @RequestHeader(name = "origin", required = false, defaultValue = "http://localhost:3000") String origin)
    throws TokenCreationException, UserNotFoundException {
        ForgotPasswordResponseDto forgotPasswordResponseDto = userService.forgotPassword(forgotPasswordRequestDto, origin);
        return new ResponseEntity<>(forgotPasswordResponseDto, HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = RequestConstants.UPDATE_PASSWORD_PATH, consumes = RequestConstants.APPLICATION_JSON, method = RequestMethod.POST)
    public ResponseEntity<UpdatePasswordResponseDto> updatePassword(@RequestBody @Valid UpdatePasswordRequestDto updatePasswordRequestDto,
                                                                    @RequestHeader(name = "token", required = true) String token)
            throws Exception {
        UpdatePasswordResponseDto updatePasswordResponseDto = userService.updatePassword(updatePasswordRequestDto, token);
        return new ResponseEntity<>(updatePasswordResponseDto, HttpStatus.OK);
    }
    @RequestMapping(path = RequestConstants.PROFILE_PATH, method = RequestMethod.GET)
    public ResponseEntity<UserProfileDto> userProfile(@PathVariable( "userId") Integer id)
            throws Exception{
        UserProfileDto userProfileDto = userService.getUserBySeqId(id);
        return new ResponseEntity<>(userProfileDto,HttpStatus.OK);
    }


}
