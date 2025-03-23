package com.ssginc.showping.entity;

import lombok.Getter;

@Getter
public enum MemberRole {

    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER");

    private final String role;

    MemberRole(String role) {
        this.role = role;
    }

}
