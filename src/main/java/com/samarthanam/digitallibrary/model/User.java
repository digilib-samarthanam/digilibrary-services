package com.samarthanam.digitallibrary.model;

import lombok.*;

import javax.persistence.*;


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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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