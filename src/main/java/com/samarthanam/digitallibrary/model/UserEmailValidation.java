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
@Table(name = "user_email_validation")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserEmailValidation {


    @Id
    @JsonProperty("user_email_seq_id")
    @Column(name = "user_email_seq_id")
    private Integer userEmailSeqId;

    @JsonProperty("user_seq_id")
    @Column(name = "user_seq_id")
    private Integer userSeqId;

    @JsonProperty("generated_otp_value")
    @Column(name = "generated_otp_value")
    private Integer generatedOTPValue;

    @JsonProperty("is_expired")
    @Column(name = "is_expired")
    private boolean isExpired;

    @JsonProperty("is_verified")
    @Column(name = "is_verified")
    private boolean isVerified;

    @JsonProperty("created_ts")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "created_ts")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime createdTs;



}
