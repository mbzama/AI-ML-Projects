package com.procure.controller;

import com.procure.dto.request.LoginRequest;
import com.procure.dto.request.SignupRequest;
import com.procure.dto.response.JwtResponse;
import com.procure.dto.response.MessageResponse;
import com.procure.model.User;
import com.procure.model.Vendor;
import com.procure.model.enums.UserRole;
import com.procure.repository.UserRepository;
import com.procure.repository.VendorRepository;
import com.procure.security.JwtUtils;
import com.procure.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VendorRepository vendorRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), 
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName());

        user.setPhone(signUpRequest.getPhone());

        Set<String> strRoles = signUpRequest.getRole();
        Set<UserRole> roles = new HashSet<>();

        if (strRoles == null) {
            roles.add(UserRole.VENDOR);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "creator":
                        roles.add(UserRole.CREATOR);
                        break;
                    case "approver":
                        roles.add(UserRole.APPROVER);
                        break;
                    default:
                        roles.add(UserRole.VENDOR);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        // If user is a vendor, create vendor profile
        if (roles.contains(UserRole.VENDOR)) {
            Vendor vendor = new Vendor(user, signUpRequest.getCompanyName());
            vendor.setRegistrationNumber(signUpRequest.getRegistrationNumber());
            vendor.setTaxId(signUpRequest.getTaxId());
            vendor.setAddress(signUpRequest.getAddress());
            vendor.setCity(signUpRequest.getCity());
            vendor.setState(signUpRequest.getState());
            vendor.setPostalCode(signUpRequest.getPostalCode());
            vendor.setCountry(signUpRequest.getCountry());
            vendor.setContactPerson(signUpRequest.getContactPerson());
            vendorRepository.save(vendor);
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
