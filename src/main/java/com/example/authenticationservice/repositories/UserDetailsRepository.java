package com.example.authenticationservice.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.authenticationservice.entity.User;
import com.example.authenticationservice.entity.UserDetails;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails,String>{
    Optional<UserDetails> findById(Long userId);
    Optional<UserDetails> findByEmail(String email);
    Optional<UserDetails> findByDisplayName(String displayName);
    Optional<UserDetails> findByProfilePicUrl(String profilePicUrl);

    @Query(
        "SELECT userDetails FROM UserDetails userDetails inner join userDetails.user user WHERE" +
        " userDetails.email like :query OR" + 
        " userDetails.number like :query OR" + 
        " userDetails.displayName like :query OR" + 
        " userDetails.birthDate like :query OR" + 
        " user.userName in ( :filters )"
    )
    List<UserDetails> findByFilters( @Param("query") String query);


    @Query("select user from UserDetails userDetails inner join userDetails.user user where userDetails.email = :email")
    Optional<User> getUserByEmail(@Param("email")String email);
}

