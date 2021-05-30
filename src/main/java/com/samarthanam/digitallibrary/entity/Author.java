package com.samarthanam.digitallibrary.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "author")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Author implements Serializable {

    @Id
    @Column(name = "author_id")
    @SequenceGenerator(name = "seq", sequenceName = "author_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private Integer authorId;

    @Column(name = "name")
    private String name;

    @Column(name = "create_ts")
    private LocalDateTime createdTimestamp;

}