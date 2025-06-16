package zama.learning.procureai.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import zama.learning.procureai.api.dto.request.LoginRequest;
import zama.learning.procureai.api.dto.request.SignupRequest;
import zama.learning.procureai.api.dto.response.JwtResponse;
import zama.learning.procureai.api.dto.response.MessageResponse;
import zama.learning.procureai.api.model.User;
import zama.learning.procureai.api.model.Vendor;
import zama.learning.procureai.api.model.enums.UserRole;
import zama.learning.procureai.api.repository.UserRepository;
import zama.learning.procureai.api.repository.VendorRepository;
import zama.learning.procureai.api.security.JwtUtils;
import zama.learning.procureai.api.security.UserPrincipal;

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
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
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
    JwtUtils jwtUtils;    @Operation(summary = "User login", description = "Authenticate a user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid input")
    })
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

    @Operation(summary = "User registration", description = "Register a new user in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Username or email already exists"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid input")
    })
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
