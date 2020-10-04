package com.samarthanam.digitallibrary.repository;

import com.samarthanam.digitallibrary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmailAddress(String emailAddress);

    @Transactional
    @Modifying
    @Query(value = "update User u set u.userPassword =:newPassword where u.emailAddress =:email")
    void updateUserPassword(@Param("email") String email, @Param("newPassword") String newPassword);


}