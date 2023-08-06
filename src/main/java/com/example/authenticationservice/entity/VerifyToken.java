package com.example.authenticationservice.entity;

import java.sql.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "verify_token")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VerifyToken {
    @Id
    @Column(name = "email")
    private String email;

    @Column(name = "verification_token")
    private String token;

    @Column(name = "verification_otp")
    private String otp;

    @Column(name = "verification_type")
    private String type;

    @Column(name = "created_at")
    private Date createdAt;
}
