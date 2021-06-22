package com.samarthanam.digitallibrary.entity;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class UserActivityId implements Serializable {

    private Integer userId;
    private Integer book;

}
