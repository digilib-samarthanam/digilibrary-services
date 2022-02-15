package com.samarthanam.digitallibrary.service;

import com.samarthanam.digitallibrary.constant.ServiceConstants;
import com.samarthanam.digitallibrary.dto.EmailSenderDto;
import com.samarthanam.digitallibrary.dto.VerifySignUpDto;
import com.samarthanam.digitallibrary.dto.request.ForgotPasswordRequestDto;
import com.samarthanam.digitallibrary.dto.request.UpdatePasswordRequestDto;
import com.samarthanam.digitallibrary.dto.request.UserLoginRequestDto;
import com.samarthanam.digitallibrary.dto.request.UserSignupRequestDto;
import com.samarthanam.digitallibrary.dto.response.*;
import com.samarthanam.digitallibrary.entity.User;
import com.samarthanam.digitallibrary.enums.EmailTemplate;
import com.samarthanam.digitallibrary.exception.*;
import com.samarthanam.digitallibrary.model.EmailVerificationToken;
import com.samarthanam.digitallibrary.model.ForgotPasswordToken;
import com.samarthanam.digitallibrary.model.UserLoginToken;
import com.samarthanam.digitallibrary.repository.UserRepository;
import com.samarthanam.digitallibrary.util.UserUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.samarthanam.digitallibrary.constant.RequestConstants.RESET_PASSWORD_LINK;
import static com.samarthanam.digitallibrary.constant.RequestConstants.SIGNUP_VERIFY_UI_LINK;
import static com.samarthanam.digitallibrary.constant.ServiceConstants.FORGOT_PASSWORD_LINK;
import static com.samarthanam.digitallibrary.constant.ServiceConstants.SIGNUP_VERIFY_LINK;
import static com.samarthanam.digitallibrary.enums.ServiceError.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final EmailSenderService emailSenderService;
    private final String salt;

    public UserService(final UserRepository userRepository,
                       final TokenService tokenService,
                       final EmailSenderService emailSenderService,
                       @Value("${password.salt}") final String salt) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.emailSenderService = emailSenderService;
        this.salt = salt;
//        this.hostName = hostName;
    }

    public UserSignupResponseDto signUp(@Valid UserSignupRequestDto userSignupRequestDto, String origin) throws ConflictException, TokenCreationException, IOException {

        User user = findByEmailAddress(userSignupRequestDto.getEmailAddress().toLowerCase());
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
        Map<String, String> templateData = getEmailTemplateData(token, EmailTemplate.SIGNUP_VERIFY, origin);
        EmailSenderDto emailSenderDto = new EmailSenderDto(user.getEmailAddress(), EmailTemplate.SIGNUP_VERIFY, templateData);
        emailSenderService.sendEmailToUser(emailSenderDto);
        return new UserSignupResponseDto("Email has been sent to your registered email id");

    }

    public VerifySignUpResponseDto verifySignUp(@Valid VerifySignUpDto verifySignUpDto) throws TokenTemperedException, TokenExpiredException, UnauthorizedException {
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

    public UserLoginResponseDto login(@Valid UserLoginRequestDto userLoginRequestDto) throws TokenCreationException, UnauthorizedException {
        String encryptedPassword = UserUtil.encryptPassword(userLoginRequestDto.getPassword(), salt);
        User dbUser = userRepository.findByEmailAddress(userLoginRequestDto.getEmail().toLowerCase());
        if (dbUser != null && encryptedPassword.equals(dbUser.getUserPassword())) {
            if (dbUser.isEmailVerified()) {
                UserLoginToken userLoginToken = new UserLoginToken(dbUser.getFirstName(), dbUser.getLastName(),
                        dbUser.getGender(), dbUser.getEmailAddress().toLowerCase(), dbUser.getUserSeqId(), dbUser.isAdmin());
                String token = tokenService.createJwtToken(userLoginToken);
                return new UserLoginResponseDto(token);
            } else {
                throw new UnauthorizedException(USER_NOT_VERIFIED);
            }
        } else {
            throw new UnauthorizedException(CREDENTIAL_MISMATCH);
        }
    }

    public ForgotPasswordResponseDto forgotPassword(@Valid ForgotPasswordRequestDto forgotPasswordRequestDto, String origin) throws TokenCreationException, UserNotFoundException {
        User dbUser = userRepository.findByEmailAddress(forgotPasswordRequestDto.getEmail().toLowerCase());
        if (dbUser != null && dbUser.isEmailVerified()) {
            ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken(forgotPasswordRequestDto.getEmail().toLowerCase());
            String token = tokenService.createJwtToken(forgotPasswordToken);
            Map<String, String> templateData = getEmailTemplateData(token, EmailTemplate.FORGOT_PASSWORD, origin);
            EmailSenderDto emailSenderDto = new EmailSenderDto(forgotPasswordRequestDto.getEmail().toLowerCase(), EmailTemplate.FORGOT_PASSWORD, templateData);
            emailSenderService.sendEmailToUser(emailSenderDto);
            return new ForgotPasswordResponseDto("Password reset email has been sent to your registered email id");

        } else {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
    }

    public UpdatePasswordResponseDto updatePassword(@Valid UpdatePasswordRequestDto updatePasswordRequestDto, String token)
            throws Exception {
        //validate token
        ForgotPasswordToken forgotPasswordToken = (ForgotPasswordToken) tokenService.decodeJwtToken(token, ForgotPasswordToken.class);
        String encryptedPassword = UserUtil.encryptPassword(updatePasswordRequestDto.getPassword(), salt);
        //try to update password
        User dbUser = userRepository.findByEmailAddress(forgotPasswordToken.getEmail().toLowerCase());
        if (dbUser != null && dbUser.isEmailVerified()) {
            userRepository.updateUserPassword(forgotPasswordToken.getEmail().toLowerCase(), encryptedPassword);
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
        existingUser.setEmailAddress(userSignupRequestDto.getEmailAddress().toLowerCase());
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
                .emailAddress(userSignupRequestDto.getEmailAddress().toLowerCase())
                .gender(userSignupRequestDto.getGender())
                .emailVerified(false)
                .isAdmin(false)
                .createDate(createDate)
                .build();
    }

    private Map<String, String> getEmailTemplateData(String token, EmailTemplate emailTemplate, String origin) {
        Map<String, String> templateData = new HashMap<>();
        if (emailTemplate.equals(EmailTemplate.SIGNUP_VERIFY)) {
            final String verifySignupLink = String.format("%s%s?token=%s", origin, SIGNUP_VERIFY_UI_LINK, token);
            templateData.put(SIGNUP_VERIFY_LINK, verifySignupLink);
            return templateData;
        } else if (emailTemplate.equals(EmailTemplate.FORGOT_PASSWORD)) {
            final String forgotPasswordLink = String.format("%s%s?token=%s", origin, RESET_PASSWORD_LINK, token);
            templateData.put(FORGOT_PASSWORD_LINK, forgotPasswordLink);
            return templateData;
        }
        return templateData;
    }

    public UserProfileDto getUserBySeqId(Integer seqId){
        User userProfile= userRepository.getById(seqId);
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setFirstName(userProfile.getFirstName());
        userProfileDto.setLastName(userProfile.getLastName());
        userProfileDto.setEmailAddress(userProfile.getEmailAddress());
        userProfileDto.setGender(userProfile.getGender());
        userProfileDto.setCreateDate(userProfile.getCreateDate());
        userProfileDto.setUpdateDate(userProfile.getUpdateDate());
        return userProfileDto;
    }

}
