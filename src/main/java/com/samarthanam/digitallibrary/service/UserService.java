package com.samarthanam.digitallibrary.service;

import com.samarthanam.digitallibrary.dto.response.UserSignupResponseDto;
import com.samarthanam.digitallibrary.model.User;
import com.samarthanam.digitallibrary.repository.UserRepository;
import com.samarthanam.digitallibrary.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Value("${password.salt}")
    private String salt;

    @Autowired
    UserRepository userRepository;

    public User signUp(User user) throws Exception {

       User dbUser= userRepository.findByEmailAddress(user.getEmailAddress());
        if(dbUser != null){
            throw new Exception("User is already present with Same email!");
        }
         dbUser= userRepository.findByMobileNumber(user.getMobileNumber());
        if(dbUser != null){
            throw new Exception("User is already present with Same mobile number!");
        }


        //Todo  this will be changed later
        user.setEmailVerified(true);
        user.setAdminApproved(true);
        user.setCreateDate(LocalDateTime.now());
        // Set user id as email
        if(StringUtils.isBlank(user.getUserId())){
            user.setUserId(user.getEmailAddress());

        }
        dbUser= userRepository.saveAndFlush(user);

         return  dbUser;






//        //TODO: check if user exsists in dbb: query with email, query with mobilenumber
//        //User userExsists = dao.findByEmail(user.getEmailAddress());
//        User userExsists = null;
//        if(userExsists == null){
//            String encryptedPassword = UserUtil.encryptPassword(user.getUserPassword(), salt);
//            user.setUserPassword(encryptedPassword);
//            //dao.save(user);
//            //TODO: service to send email
//            return new UserSignupResponseDto("Email has been sent to your registered email id");
//        } else {
//            //TODO: throw user already exsist
//        }
        //return new UserSignupResponseDto("Email has been sent to your registered email id");

    }


    private void  sendVerificationCode(User user){


    }
}
