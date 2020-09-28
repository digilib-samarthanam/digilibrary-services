package com.samarthanam.digitallibrary.repository;

import com.samarthanam.digitallibrary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmailAddress(String emailAddress);

    User findByMobileNumber(String mobileNumber);


}