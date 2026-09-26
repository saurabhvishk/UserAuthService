package com.example.userauthservice.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Session extends BaseModel{

    @Column(length = 1000)
    private String token;

    @ManyToOne
    private User user;
}
