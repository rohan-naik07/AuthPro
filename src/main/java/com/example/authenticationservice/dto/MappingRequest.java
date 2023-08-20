package com.example.authenticationservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MappingRequest {
    private String location;
    private Long mappingId;
    private Long parentId;
}
