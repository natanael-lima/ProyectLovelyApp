package com.nl.lovely.controller;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.nl.lovely.dto.ChangePasswordDTO;
import com.nl.lovely.dto.NotificationDTO;
import com.nl.lovely.dto.PreferenceDTO;
import com.nl.lovely.dto.ProfileDTO;
import com.nl.lovely.dto.ProfileDetailDTO;
import com.nl.lovely.dto.UserDTO;
import com.nl.lovely.entity.Preference;
import com.nl.lovely.entity.ProfileDetail;
import com.nl.lovely.entity.User;
import com.nl.lovely.enums.UserStatus;
import com.nl.lovely.repository.UserRepository;
import com.nl.lovely.response.ApiResponse;
import com.nl.lovely.response.AuthResponse;
import com.nl.lovely.response.UserProfileResponse;
import com.nl.lovely.response.UserResponse;
import com.nl.lovely.service.NotificationService;
import com.nl.lovely.service.UserService;


@CrossOrigin(origins = "http://localhost:4200") // Reemplaza esto con el dominio de tu frontend
@RestController
@RequestMapping("/api/user")
public class UserController {
	@Autowired
    private UserService userService;
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
    private UserRepository userRepository;
	
    //************************** API para actualizar los datos del usuario. ************************** new
    @PutMapping("/update-user/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id,@RequestBody ProfileDTO userRequest)
    {
    	userRequest.setId(id); 
        return ResponseEntity.ok(userService.updateUserData(userRequest));
    }

    //************************** API para actualizar los datos del usuario visibilidad. ************************** new
    @PutMapping("/update-visiblity/{id}")
    public ResponseEntity<ApiResponse> updateUserVisibility(@PathVariable Long id,@RequestBody ProfileDTO userRequest)
    {
    	userRequest.setId(id); 
        return ResponseEntity.ok(userService.updateUserIsVisible(userRequest));
    }

    //************************** API para eliminar un usuario.**************************
    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
    	try {
    		System.out.print("entre eliminar");
            ApiResponse response = userService.deleteUserComplete(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al eliminar el usuario: " + e.getMessage()));
        }
    }
    //************************** API para obtener solo datos basico by ID. **************************
  	@GetMapping("/get-user/basic/{id}")
      public ResponseEntity<UserDTO> getUser(@PathVariable Long id)
      {
          UserDTO userDTO = userService.getUser(id);
          if (userDTO==null)
          {
             return ResponseEntity.notFound().build();
          }
          return ResponseEntity.ok(userDTO);
      }
    //************************** API para obtener el usuario completo by ID por paramaetro. ************************** new
    @GetMapping("/get-user/{id}")
    public ResponseEntity<ProfileDTO> findUserById(@PathVariable Long id) throws Exception {
        try {
        	ProfileDTO dto = userService.getProfileById(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.noContent().build(); 
        }
    }
    
  	//************************** API para obtener el usuario logueado actual. **************************
  	@GetMapping("/current-user-profile")
  	public ResponseEntity<ProfileDTO> getCurrentUserProfile() {
  	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
  	    if (!(authentication.getPrincipal() instanceof UserDetails)) {
  	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  	    }
  	    
  	    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
  	    User user = userRepository.findByUsername(userDetails.getUsername())
  	            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
  	    
  	    ProfileDTO profileDTO = ProfileDTO.builder()
  	            .id(user.getId())
  	            .username(user.getUsername())
  	            .lastname(user.getLastname())
  	            .name(user.getName())
  	            .role(user.getRole())
  	            .state(user.getState())
		        .isVisible(user.getIsVisible())
  	            .preference(mapToPreferenceDTO(user.getPreference()))
  	            .profileDetail(mapToProfileDetailDTO(user.getProfileDetail()))
  	            .build();
  	    
  	    return ResponseEntity.ok(profileDTO);
  	}
  	
  	private PreferenceDTO mapToPreferenceDTO(Preference preference) {
  	    if (preference == null) return null;
  	    return PreferenceDTO.builder()
  	            .id(preference.getId())
  	            .maxAge(preference.getMaxAge())
  	            .minAge(preference.getMinAge())
  	            .likeGender(preference.getLikeGender())
  	            .location(preference.getLocation())
  	            .distance(preference.getDistance())
  	            .interests(preference.getInterests())
  	            .build();
  	}

  	private ProfileDetailDTO mapToProfileDetailDTO(ProfileDetail profileDetail) {
  	    if (profileDetail == null) return null;
  	    return ProfileDetailDTO.builder()
  	            .id(profileDetail.getId())
  	            .phone(profileDetail.getPhone())
  	            .gender(profileDetail.getGender())
  	            .birthDate(profileDetail.getBirthDate())
  	            .description(profileDetail.getDescription())
  	            .work(profileDetail.getWork())
  	            .photo(profileDetail.getPhoto())
  	            .photoFileName(profileDetail.getPhotoFileName())
  	            .timestamp(profileDetail.getTimestamp())
  	            .build();
  	}
  	
    
	//************************** API para checkear si existe el username **************************
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
    	 System.out.println("Buscando usuario: '" + username + "'");
        boolean exists = userRepository.findByUsername(username).isPresent();
        System.out.println("Usuario encontrado: " + exists);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
    
	//************************** API para obtener todos los usuarios con los que hizo match por id de usuario especifico.**************************
	@GetMapping("/match-all/{id}")
    public ResponseEntity<List<ProfileDTO>> getUsersByMatch(@PathVariable Long id)
    {
		List<ProfileDTO> lista = userService.getUsersByMatch(id);
        if (lista.isEmpty())
        {
           return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(lista);
    }
	//************************** API para obtener perfil de usuario segun preferencias.**************************
    @GetMapping("/filter-users")
    public ResponseEntity<List<ProfileDTO>> getFilterProfileUsers() {
    	Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ProfileDTO> listProfiles = userService.getFilteredUserProfiles(userId);
        if (listProfiles != null) {
            return ResponseEntity.ok(listProfiles);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // API para cambiar password.
    @PutMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse> changePasswordDTO(@PathVariable Long id, @RequestBody ChangePasswordDTO changePasswordRequest) throws Exception {
    	
    	// Obtener el contexto de autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verificar si el usuario está autenticado
        if (authentication != null && authentication.isAuthenticated()) {
            		// Obtener los detalles del usuario autenticado
		        	String username = authentication.getName();
		            User userId = userService.findByUsername(username).orElse(null);
		            
		            System.out.println("id de user1: "+userId.getId());
		            System.out.println("id de user2: "+id);
		            
			        // Comparar el ID del usuario autenticado con el ID que se desea cambiar
			        if (!userId.getId().equals(id)) {
			            throw new AccessDeniedException("No tienes permiso para cambiar la contraseña de otro usuario.");
			        }
			        try {
		        		userService.changePassword(id, changePasswordRequest.getCurrentPassword(), changePasswordRequest.getNewPassword());

		                return ResponseEntity.ok(new ApiResponse( "La contraseña se cambió satisfactoriamente."));
		            } catch (RuntimeException e) {
		                return ResponseEntity.status(404).body(new ApiResponse("La contraseña no se pudo cambiar."));
		            }    
		 
        } else {
            // Si no está autenticado
        	return ResponseEntity.notFound().build();
        }
    }
    
    //************************** Metodo para obtener el id del user logueado.**************************
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElse(null);
            if (user != null) {
            	 System.out.println("id de user: "+user.getId());
                return user.getId();
               
            }
        }
        return null;
    }
    
    
	
    
    
	//************************** API para obtener un UserProfile by ID. **************************
	/*@GetMapping(value = "/profile/{id}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long id)
    {
        UserProfileDTO userProfileDTO = userService.getUserProfile(id);
        if (userProfileDTO==null)
        {
           return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userProfileDTO);
    }*/
	
	//************************** API para obtener un UserProfile by UserId logueado actualmente. **************************
	/*@GetMapping(value = "/currentProfile/{id}")
    public ResponseEntity<UserProfileDTO> getIdByProfile(@PathVariable Long id)
    {
		Long idUser = userService.getUserProfileByUserId(id);
		UserProfileDTO userProfileDTO = userProfileService.getUserProfile(idUser);
	    if (userProfileDTO==null)
	    {
	        return ResponseEntity.notFound().build();
	    }
	        return ResponseEntity.ok(userProfileDTO);
    }*/
	

    //************************** API para actualizar la foto de perfil del usuario + profile. **************************
    /*@PutMapping(value="/update-profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> updateProfileAndPhoto(@RequestPart(value = "photoFile", required = false) MultipartFile photoFile, @RequestPart(value = "req", required = true) UserProfileDTO req, @RequestParam(value = "keepCurrentImage", required = false) Boolean keepCurrentImage) throws Exception {
        // Tu lógica existente para cargar el archivo y actualizar el perfil
    	 if (req == null && photoFile == null ) {
    		 return ResponseEntity.badRequest().body(new UserProfileResponse("Error: el archivo de imagen o la solicitud están vacíos"));
    	 }
    		 try {
 	    	 		// Verificar si se debe mantener la imagen actual
 	    		   if (Boolean.TRUE.equals(keepCurrentImage)) {
	           		// Mantener la imagen actual
 	    			System.out.print("Mantener la imagen actual");
	    	   		req.setId(req.getId());
	    	   		userProfileService.updateProfileDate(req);}
	    	   		else { // Si keepCurrentImage es falso o nulo, y no se proporciona una nueva imagen, devolver un error
	    	            if (photoFile == null) {
	    	                return ResponseEntity.badRequest().body(new UserProfileResponse("Error: se esperaba una nueva imagen pero no se proporcionó"));
	    	            }
	    	            // Se proporciona una nueva imagen, actualizar la imagen
	    	            System.out.print("Actualizar con nueva imagen");
	    	            req.setId(req.getId());
	    	            userProfileService.updateProfileAndPhoto(req, photoFile);
	    	   		} 
		 	      
			        	 return ResponseEntity.ok(new UserProfileResponse("User actualizado con éxito"));
			        } catch (RuntimeException e) {
			            return ResponseEntity.noContent().build(); 
			        }
     }*/
    

    
    /*
      //************************** API que registra un nuevo profile. **************************
    @PostMapping(value="registration-profile/{userId}")
    public ResponseEntity<?> registerProfile(@RequestParam("photo") MultipartFile photo,
            @RequestParam("photoFileName") String photoFileName,
            @RequestParam("location") String location,
            @RequestParam("gender") String gender,
            @RequestParam("age") String age,
            @RequestParam("likeGender") String likeGender,
            @RequestParam("maxAge") Integer maxAge,
            @RequestParam("minAge") Integer minAge,
            @PathVariable Long userId) {
			UserProfileResponse response = userProfileService.saveProfile(photo, photoFileName, location, gender, age, likeGender, maxAge, minAge, userId);
			return ResponseEntity.ok(response);
    }
    */
}
