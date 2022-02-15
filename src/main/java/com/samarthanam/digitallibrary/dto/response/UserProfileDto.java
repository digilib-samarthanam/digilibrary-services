package com.samarthanam.digitallibrary.dto.response;


import lombok.Data;


@Data
public class UserProfileDto {
    private String firstName;

    private String lastName;

    private String emailAddress;

    private Character gender;

    private Long createDate;

    private Long updateDate;

}
