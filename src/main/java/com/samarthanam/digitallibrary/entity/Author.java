package com.samarthanam.digitallibrary.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "author")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author implements Serializable {

    @Id
    @Column(name = "author_id")
    private Integer authorId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "country")
    private String country;

    @Column(name = "dob")
    private LocalDateTime dateOfBirth;

    @Column(name = "dod")
    private String dod;

    @Column(name = "update_ts")
    private LocalDateTime updatedTimestamp;

    @Column(name = "create_ts")
    private LocalDateTime createdTimestamp;

}