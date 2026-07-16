package com.dalal.identityservicepfe.controllers;

import com.dalal.identityservicepfe.dtos.*;
import com.dalal.identityservicepfe.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    /*
    * Gestion d'authentication
    * */
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) throws Exception {
        AuthResponseDto registerResponseDto = userService.register(registerRequestDto);
        return new ResponseEntity<>(registerResponseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) throws Exception {
        AuthResponseDto response = userService.login(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-password")
    public ResponseEntity<Map<String,String>> updatePassword(@RequestBody @Valid UpdatePwdRequestDto updatePwdRequestDto, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        userService.updatePassword(updatePwdRequestDto,username);
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès !"));
    }

    @PutMapping("/change-email")
    public ResponseEntity<AuthResponseDto> changeEmail(@RequestBody @Valid ChangeEmailRequestDto  changeEmailRequestDto, @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        String email = userDetails.getUsername(); // the username = email in my case
        AuthResponseDto response = userService.changeEmail(changeEmailRequestDto, email);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccountByEmail(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        userService.deleteAccount(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/add-administrator")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponseDto> addAdministrator(@Valid @RequestBody RegisterRequestDto registerRequestDto) throws Exception {
        AuthResponseDto registerResponseDto = userService.addAdministrator(registerRequestDto);
        return new ResponseEntity<>(registerResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserProfileMinDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UserProfileMinDto> usersPage = userService.getAllUsers(page, size);
        return ResponseEntity.ok(usersPage);
    }

    /*
     * gestion de profil
     */

    /******************day1 5 apis (we do not do the tests) ********************/

    @GetMapping("/search")
    public ResponseEntity<Page<PrestataireMinResponseDto>> searchByName(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.searchPrestatairesByName(query, page, size));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<PrestataireMinResponseDto>> filterByCity(
            @RequestParam String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.filterPrestatairesByCity(city, page, size));
    }

    @GetMapping("/{id}/public-profile")
    public ResponseEntity<PrestatairePublicDetailDto> getPublicProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getPrestatairePublicWithoutContact(id));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(userService.getAuthenticatedUserProfile(email));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateProfileRequestDto dto) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(userService.updateAuthenticatedUserProfile(email, dto));
    }

    @GetMapping("/{id}/prestataire-info")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<PrestataireAuthResponseDto> getPrestataireInfo(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getPrestataireDetailForClient(id));
    }

    /********************day 2 : 5 api************************/

    @PostMapping("/become-prestataire")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<BecomePrestataireRespDto> becomePrestataire(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BecomePrestataireDto becomePrestataireDto) throws Exception {

        BecomePrestataireRespDto becomePrestataireRespDto = userService.becomePrestataire(
                userDetails.getUsername(),
                becomePrestataireDto
        );

        return ResponseEntity.ok(becomePrestataireRespDto);
    }

    @PostMapping("/switch-to-client")
    @PreAuthorize("hasRole('PRESTATAIRE')")
    //n'oublie pas de changer le token par ce que a été changé
    public ResponseEntity<Map<String, String>> switchToClient(
            @AuthenticationPrincipal UserDetails userDetails) {

        userService.switchToClient(userDetails.getUsername());

        return ResponseEntity.ok(
                Map.of("message", "Vous êtes maintenant un client.")
        );
    }

//    @GetMapping("/admin/users-by-role")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<UserProfileMinDto>> getUsersByRole(
//            @RequestParam(value = "role", required = true) String role) {
//
//        List<UserProfileMinDto> users = userService.getProfilesByRole(role);
//        return ResponseEntity.ok(users);
//    }


}




