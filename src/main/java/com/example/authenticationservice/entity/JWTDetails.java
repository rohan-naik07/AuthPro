package com.example.authenticationservice.entity;

import java.sql.Date;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jwt_details")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JWTDetails {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "id_token")
    private String idToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "issued_at")
    private Date issuedAt;

    @Column(name = "expire_time")
    private Long expireTime;

    @Column(name = "role")
    private String role;
}
