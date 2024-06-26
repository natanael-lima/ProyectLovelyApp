package com.nl.lovely.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
	Long id; // ID del perfil del usuario
	Long userId; // ID del usuario logueado
    byte[] photo;
    String photoFileName;
    String location;
    String gender;
    String age;
    String likeGender;
    Integer maxAge;
    Integer minAge;
}
