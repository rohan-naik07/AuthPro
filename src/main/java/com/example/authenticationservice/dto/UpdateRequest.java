package com.example.authenticationservice.dto;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateRequest {
    private String userId;
    private String email;
    private String profilePicUrl;
    private String displayName;
    private Date birthDate;
}
