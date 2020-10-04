package com.samarthanam.digitallibrary.service;

import com.samarthanam.digitallibrary.constant.ServiceConstants;
import com.samarthanam.digitallibrary.dto.VerifySignUpDto;
import com.samarthanam.digitallibrary.dto.request.ForgotPasswordRequestDto;
import com.samarthanam.digitallibrary.dto.request.UpdatePasswordRequestDto;
import com.samarthanam.digitallibrary.dto.request.UserLoginRequestDto;
import com.samarthanam.digitallibrary.dto.request.UserSignupRequestDto;
import com.samarthanam.digitallibrary.dto.response.*;
import com.samarthanam.digitallibrary.entity.User;
import com.samarthanam.digitallibrary.exception.*;
import com.samarthanam.digitallibrary.model.EmailVerificationToken;
import com.samarthanam.digitallibrary.model.ForgotPasswordToken;
import com.samarthanam.digitallibrary.model.UserLoginToken;
import com.samarthanam.digitallibrary.repository.UserRepository;
import com.samarthanam.digitallibrary.util.UserUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.samarthanam.digitallibrary.enums.ServiceError.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    private final String salt;

    public UserService(final UserRepository userRepository,
                       final TokenService tokenService,
                       @Value("${password.salt}") final String salt) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.salt = salt;
    }

    public UserSignupResponseDto signUp(UserSignupRequestDto userSignupRequestDto) throws ConflictException, TokenCreationException {

        User user = findByEmailAddress(userSignupRequestDto.getEmailAddress());
        if (user != null && user.isEmailVerified()) {
            throw new ConflictException(USER_ALREADY_EXIST);
        } else if (user != null && !user.isEmailVerified()) {
            updateExistingUser(userSignupRequestDto, user);
            userRepository.save(user);
        } else {
            user = createNewUser(userSignupRequestDto);
            userRepository.save(user);
        }
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken(user.getUserSeqId());
        String token = tokenService.createJwtToken(emailVerificationToken);
        //TODO: service to send email sendEmailToUser(token)
        return new UserSignupResponseDto("Email has been sent to your registered email id, token: " + token);

    }

    public VerifySignUpResponseDto verifySignUp(VerifySignUpDto verifySignUpDto) throws TokenTemperedException, TokenExpiredException, UnauthorizedException {
        EmailVerificationToken emailVerificationToken = (EmailVerificationToken) tokenService.decodeJwtToken(verifySignUpDto.getToken(),
                EmailVerificationToken.class);
        Optional<User> optionalUser = userRepository.findById(emailVerificationToken.getUserSequenceId());
        if (!optionalUser.isPresent()) {
            throw new UnauthorizedException(RESOURCE_NOT_FOUND);
        }
        User user = optionalUser.get();
        user.setEmailVerified(true);
        userRepository.save(user);
        return new VerifySignUpResponseDto(ServiceConstants.VERIFICATION_STATUS_SUCCESS);
    }

    public UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto) throws TokenCreationException, UnauthorizedException {
        String encryptedPassword = UserUtil.encryptPassword(userLoginRequestDto.getPassword(), salt);
        User dbUser = userRepository.findByEmailAddress(userLoginRequestDto.getEmail());
        if (dbUser != null && encryptedPassword.equals(dbUser.getUserPassword())) {
            if (dbUser.isEmailVerified()) {
                UserLoginToken userLoginToken = new UserLoginToken(dbUser.getFirstName(), dbUser.getLastName(),
                        dbUser.getGender(), dbUser.getEmailAddress(), dbUser.getUserSeqId());
                String token = tokenService.createJwtToken(userLoginToken);
                return new UserLoginResponseDto(token);
            } else {
                throw new UnauthorizedException(USER_NOT_VERIFIED);
            }
        } else {
            throw new UnauthorizedException(CREDENTIAL_MISMATCH);
        }
    }

    public ForgotPasswordResponseDto forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto) throws TokenCreationException {
        ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken(forgotPasswordRequestDto.getEmail());
        String token = tokenService.createJwtToken(forgotPasswordToken);
        //TODO: service to send email sendEmailToUser(token)
        return new ForgotPasswordResponseDto("Password reset email has been sent to your registered email id: " + token);
    }

    public UpdatePasswordResponseDto updatePassword(UpdatePasswordRequestDto updatePasswordRequestDto, String token)
            throws Exception {
        //validate token
        ForgotPasswordToken forgotPasswordToken = (ForgotPasswordToken) tokenService.decodeJwtToken(token, ForgotPasswordToken.class);
        String encryptedPassword = UserUtil.encryptPassword(updatePasswordRequestDto.getPassword(), salt);
        //try to update password
        User dbUser = userRepository.findByEmailAddress(forgotPasswordToken.getEmail());
        if(dbUser != null){
            userRepository.updateUserPassword(forgotPasswordToken.getEmail(), encryptedPassword);
            return new UpdatePasswordResponseDto("Password has been reset, please login via app");
        } else {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
    }

    private User findByEmailAddress(String emailAddress) {
        return userRepository.findByEmailAddress(emailAddress);
    }

    private void updateExistingUser(UserSignupRequestDto userSignupRequestDto, User existingUser) {
        String encryptedPassword = UserUtil.encryptPassword(userSignupRequestDto.getPassword(), salt);
        existingUser.setUserPassword(encryptedPassword);
        existingUser.setFirstName(userSignupRequestDto.getFirstName());
        existingUser.setLastName(userSignupRequestDto.getLastName());
        existingUser.setEmailAddress(userSignupRequestDto.getEmailAddress());
        existingUser.setGender(userSignupRequestDto.getGender());
        existingUser.setUpdateDate(System.currentTimeMillis());
    }

    private User createNewUser(UserSignupRequestDto userSignupRequestDto) {
        String encryptedPassword = UserUtil.encryptPassword(userSignupRequestDto.getPassword(), salt);
        return buildUserFromRequestDto(userSignupRequestDto, encryptedPassword, System.currentTimeMillis());
    }

    private User buildUserFromRequestDto(UserSignupRequestDto userSignupRequestDto,
                                         String encryptedPassword,
                                         long createDate) {
        return User.builder()
                .userPassword(encryptedPassword)
                .firstName(userSignupRequestDto.getFirstName())
                .lastName(userSignupRequestDto.getLastName())
                .emailAddress(userSignupRequestDto.getEmailAddress())
                .gender(userSignupRequestDto.getGender())
                .emailVerified(false)
                .adminApproved(false)
                .createDate(createDate)
                .build();
    }
}
