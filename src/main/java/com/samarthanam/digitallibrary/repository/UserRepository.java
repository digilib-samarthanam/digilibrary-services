package com.samarthanam.digitallibrary.repository;

import com.samarthanam.digitallibrary.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    User findByEmailAddress(String emailaddress);
    User findByMobileNumber(Integer mobileNumber);



}
