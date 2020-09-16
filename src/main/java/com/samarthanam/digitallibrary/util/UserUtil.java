package com.samarthanam.digitallibrary.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.Base64;

public class UserUtil {


    public static String encryptPassword(String data, String salt){
        if(StringUtils.isBlank(data)){
            return "";
        }
        String encodedSalt = Base64.getEncoder().encodeToString(salt.getBytes(Charset.forName("UTF-8")));
        return DigestUtils.sha256Hex(data + encodedSalt);
    }
}
