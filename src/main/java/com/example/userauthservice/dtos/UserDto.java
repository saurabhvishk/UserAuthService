package com.example.userauthservice.dtos;

import com.example.userauthservice.models.RoleType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserDto {

    private Long id;

    private String email;

    private Set<RoleType> roles = new HashSet<>();

}
