package com.example.demo.services;

import com.example.demo.authentication.JwtUserDetails;
import com.example.demo.enums.ERole;
import com.example.demo.models.*;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.SignupRequest;
import com.example.demo.payload.response.JwtResponse;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder encoder;
  private final JwtUtil jwtUtil;

  public ResponseEntity<?> logIn(LoginRequest loginRequest) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtil.generateJwtToken(authentication);
    JwtUserDetails customUserDetails = (JwtUserDetails) authentication.getPrincipal();
    List<String> roles =
        customUserDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

    return ResponseEntity.ok(
        new JwtResponse(
            jwt,
            customUserDetails.getId(),
            customUserDetails.getUsername(),
            customUserDetails.getEmail(),
            roles));
  }

  public ResponseEntity<?> signUp(SignupRequest signupRequest) {
    if (userRepository.existsByUsername(signupRequest.getUsername())) {
      return ResponseEntity.badRequest().body("Error: Username is already taken!");
    }

    if (userRepository.existsByEmail(signupRequest.getEmail())) {
      return ResponseEntity.badRequest().body("Error: Email is already in use!");
    }

    User user =
        new User(
            signupRequest.getUsername(),
            signupRequest.getEmail(),
            encoder.encode(signupRequest.getPassword()));

    Set<String> stringRoles = signupRequest.getRoles();

    Set<Role> roles = new HashSet<>();

    if (stringRoles == null) {
      Role userRole =
          roleRepository
              .findByName(ERole.USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      stringRoles.forEach(
          role -> {
            switch (role) {
              case "ADMIN":
                Role adminRole =
                    roleRepository
                        .findByName(ERole.ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(adminRole);
                break;
              case "MODERATOR":
                Role moderatorERole =
                    roleRepository
                        .findByName(ERole.MODERATOR)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(moderatorERole);
                break;
              default:
                Role userRole =
                    roleRepository
                        .findByName(ERole.USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            }
          });
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok("User registered successfully!");
  }
}
