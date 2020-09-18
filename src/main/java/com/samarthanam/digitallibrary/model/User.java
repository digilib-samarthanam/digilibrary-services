package com.samarthanam.digitallibrary.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    @Id
    @JsonProperty("user_seq_id")
    @Column(name = "user_seq_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer userSeqId;


    @JsonProperty("first_name")
    @Column(name = "first_name")
    private String firstName;


    @JsonProperty("last_name")
    @Column(name = "last_name")
    private String lastName;

    @JsonProperty("mobile_number")
    @Column(name = "mobile_number")
    private Integer mobileNumber;


    @JsonProperty("address")
    @Column(name = "address")
    private String address;

    @JsonProperty("user_id")
    @Column(name = "user_id")
    private String userId;


    @JsonProperty("city")
    @Column(name = "city")
    private String city;

    @JsonProperty("state")
    @Column(name = "state")
    private String state;

    @JsonProperty("country")
    @Column(name = "country")
    private String country;

    @JsonProperty("pin_code")
    @Column(name = "pin_code")
    private Integer pinCode;

    @JsonProperty("email_address")
    @Column(name = "email_address")
    private String emailAddress;

    @JsonProperty("email_verified")
    @Column(name = "email_verified")
    private boolean emailVerified;


    @JsonProperty("admin_approved")
    @Column(name = "admin_approved")
    private boolean adminApproved;

    @JsonProperty("user_password")
    @Column(name = "user_password")
    private String userPassword;

    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "created_date")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime createDate;



    @JsonProperty("updated_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "updated_date")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime updateDate;







}
