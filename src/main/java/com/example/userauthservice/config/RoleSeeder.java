package com.example.userauthservice.config;

import com.example.userauthservice.models.Role;
import com.example.userauthservice.models.RoleType;
import com.example.userauthservice.models.State;
import com.example.userauthservice.repos.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public void run(String... args) {
        for (RoleType roleType : RoleType.values()) {
            if (roleRepo.findByValue(roleType).isEmpty()) {
                Role role = new Role();
                role.setValue(roleType);
                role.setState(State.ACTIVE);
                roleRepo.save(role);
            }
        }
    }
}
