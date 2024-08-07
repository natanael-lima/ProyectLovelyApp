package com.nl.lovely.dto;

import com.nl.lovely.entity.Preference;
import com.nl.lovely.entity.ProfileDetail;
import com.nl.lovely.enums.RoleType;
import com.nl.lovely.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
	 Long id;
     String username;
     String lastname;
	 String name;
	 RoleType role;
	 UserStatus state;
	 Boolean isVisible;
	 PreferenceDTO preference;
	 ProfileDetailDTO profileDetail;
}
