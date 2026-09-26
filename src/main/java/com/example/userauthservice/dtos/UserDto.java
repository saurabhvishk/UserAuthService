package com.example.userauthservice.dtos;

import com.example.userauthservice.models.Role;
import com.example.userauthservice.models.RoleType;
import com.example.userauthservice.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDto {

    private Long id;

    private String email;

    private String name;

    private String phone;

    private Set<RoleType> roles = new HashSet<>();

    public static UserDto from(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setPhone(user.getPhone());
        userDto.setRoles(user.getRoles().stream()
                .map(Role::getValue)
                .collect(Collectors.toSet()));
        return userDto;
    }

}
