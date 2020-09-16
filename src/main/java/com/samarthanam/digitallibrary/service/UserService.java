package com.samarthanam.digitallibrary.service;

import com.samarthanam.digitallibrary.dto.response.UserSignupResponseDto;
import com.samarthanam.digitallibrary.model.User;
import com.samarthanam.digitallibrary.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Value("${password.salt}")
    private String salt;

    public UserSignupResponseDto signUp(User user){

        //TODO: check if user exsists in dbb: query with email, query with mobilenumber
        //User userExsists = dao.findByEmail(user.getEmailAddress());
        User userExsists = null;
        if(userExsists == null){
            String encryptedPassword = UserUtil.encryptPassword(user.getPassword(), salt);
            user.setPassword(encryptedPassword);
            //dao.save(user);
            //TODO: service to send email
            return new UserSignupResponseDto("Email has been sent to your registered email id");
        } else {
            //TODO: throw user already exsist
        }
        return new UserSignupResponseDto("Email has been sent to your registered email id");

    }
}
