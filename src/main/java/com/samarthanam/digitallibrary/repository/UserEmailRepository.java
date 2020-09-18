package com.samarthanam.digitallibrary.repository;

 import com.samarthanam.digitallibrary.model.UserEmailValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEmailRepository extends JpaRepository<UserEmailValidation,Integer> {


}
