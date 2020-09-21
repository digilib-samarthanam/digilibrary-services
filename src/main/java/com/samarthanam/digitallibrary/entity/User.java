package com.samarthanam.digitallibrary.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "user_seq_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer userSeqId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "gender")
    private String gender;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "admin_approved")
    private boolean adminApproved;

    @Column(name = "created_date")
    private Long createDate;

    @Column(name = "updated_date")
    private Long updateDate;


}