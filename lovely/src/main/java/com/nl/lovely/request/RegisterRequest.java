package com.nl.lovely.request;

import com.nl.lovely.entity.UserProfile;
import com.nl.lovely.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
	
    private String name;
    private String lastname;
    private String username;
    private String password;
    private RoleType role;
    
    // Datos del perfil
    private UserProfile profile;
}
