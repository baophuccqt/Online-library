package org.pio.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ADMIN(0, "this mf is an abusive admin"),
    USER(1, "this is an ordinary user")
    ;

    private int id;
    private String description;
}
