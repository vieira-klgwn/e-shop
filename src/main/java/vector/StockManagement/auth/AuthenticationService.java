package vector.StockManagement.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import vector.StockManagement.config.JwtService;
import vector.StockManagement.config.TenantContext;
import vector.StockManagement.model.Tenant;
import vector.StockManagement.model.Token;
import vector.StockManagement.model.enums.Role;
import vector.StockManagement.model.enums.TokenType;
import vector.StockManagement.model.User;
import vector.StockManagement.model.enums.Gender;
import vector.StockManagement.repositories.TenantRepository;
import vector.StockManagement.repositories.TokenRepository;
import vector.StockManagement.repositories.UserRepository;
import vector.StockManagement.services.TenantService;
import vector.StockManagement.services.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;
    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());
    private final TenantRepository tenantRepository;

    @Value("${spring.mail.from}")
    private String fromEmail;


    public AuthenticationResponse register(RegisterRequest request) {
        // Validate input
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already taken: " + request.getEmail());
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalStateException("Passwords do not match");
        }
        if (request.getGender() == null || request.getGender().isEmpty()) {
            throw new IllegalStateException("Gender is required");
        }
        Gender gender;
        try {
            gender = Gender.valueOf(request.getGender().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid gender value: " + request.getGender());
        }

        Tenant tenant = tenantRepository.findById(15L).orElseThrow(() -> new IllegalStateException("Tenant not found"));

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .gender(gender)
                .tenant(tenant)
                .phone(request.getPhone())
                .nationality(request.getNationality())
                .build();
        user.setCreatedAt(LocalDateTime.now());
        var savedUser = userRepository.save(user);

        // Generate and save tokens
        var token = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, token);
        saveUserToken(savedUser, refreshToken);
//        logger.info("Access token: {}", token);
//        logger.info("Refresh token: {}", refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse registerAdmin(RegisterRequest request) {
        // Validate input
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already taken: " + request.getEmail());
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalStateException("Passwords do not match");
        }
        if (request.getGender() == null || request.getGender().isEmpty()) {
            throw new IllegalStateException("Gender is required");
        }
        Gender gender;
        try {
            gender = Gender.valueOf(request.getGender().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid gender value: " + request.getGender());
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .gender(gender)
                .tenant(request.getTenant())
                .phone(request.getPhone())
                .nationality(request.getNationality())
                .build();
        user.setCreatedAt(LocalDateTime.now());
        var savedUser = userRepository.save(user);

        // Generate and save tokens
        var token = jwtService.generateTokenOnSignUp(user ,request.getTenant().getId());
        var refreshToken = jwtService.generateRefreshToken(user); // adding tenant id to the tenantContext not implemented
        saveUserToken(savedUser, token);
        saveUserToken(savedUser, refreshToken);
//        logger.info("Access token: {}", token);
//        logger.info("Refresh token: {}", refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
    }


    public AuthenticationResponse createManagingDirector (RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already taken: " + request.getEmail());
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalStateException("Passwords do not match");
        }
        if (request.getGender() == null || request.getGender().isEmpty()) {
            throw new IllegalStateException("Gender is required");
        }
        Gender gender;
        try {
            gender = Gender.valueOf(request.getGender().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid gender value: " + request.getGender());
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.MANAGING_DIRECTOR)
                .gender(gender)
                .tenant(tenantRepository.findById(TenantContext.getTenantId()).orElseThrow(() -> new IllegalStateException("Tenant not found")))
                .phone(request.getPhone())
                .nationality(request.getNationality())
                .build();
        user.setCreatedAt(LocalDateTime.now());
        var savedUser = userRepository.save(user);

        // Generate and save tokens
        var token = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user); // Functionality to add tenant to tenant Context not implemented
        saveUserToken(savedUser, token);
        saveUserToken(savedUser, refreshToken);
//        logger.info("Access token: {}", token);
//        logger.info("Refresh token: {}", refreshToken);


        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();

    }



    @Transactional
    public AuthenticationResponse createSuperAdmin (RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already taken: " + request.getEmail());
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalStateException("Passwords do not match");
        }
        if (request.getGender() == null || request.getGender().isEmpty()) {
            throw new IllegalStateException("Gender is required");
        }
        Gender gender;
        try {
            gender = Gender.valueOf(request.getGender().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid gender value: " + request.getGender());
        }
        Tenant tenant = new Tenant();
        tenant.setName("Super Admin1");
        tenant.setDescription("Super Admin Description");
        tenant.setCode("0001");
        tenantRepository.saveAndFlush(tenant);
        TenantContext.setTenantId(tenant.getId());

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.SUPER_ADMIN)
                .gender(gender)
                .tenant(tenant)
                .phone(request.getPhone())
                .nationality(request.getNationality())
                .build();
        user.setCreatedAt(LocalDateTime.now());
        var savedUser = userRepository.save(user);

        // Generate and save tokens
        var token = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user); // Functionality to add tenant to tenant Context not implemented
        saveUserToken(savedUser, token);
        saveUserToken(savedUser, refreshToken);
//        logger.info("Access token: {}", token);
//        logger.info("Refresh token: {}", refreshToken);


        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();

    }



    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        var user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow(() -> new IllegalStateException("User not found: " + authenticationRequest.getEmail()));
        var token = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, refreshToken);
        saveUserToken(user, token);
        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
    }

    private void revokeAllUserTokens(User user) {
        var validTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }
        validTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(User savedUser, String jwtToken) {
        var token = Token.builder()
                .token(jwtToken)
                .user(savedUser)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .createdDate(LocalDateTime.now())
                .build();
        tokenRepository.save(token);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader("Authorization");
        final String refreshToken;
        final String email;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or Invalid Authorization header");
            return;
        }

        refreshToken = authHeader.substring(7);
        email = jwtService.extractUsername(refreshToken);

        if (email == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Refresh Token");
            return;
        }

        var user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("User not found: " + email));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or Expired Refresh Token");
            return;
        }
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);

        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }

    public String requestPasswordReset(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found: " + email));
        String resetToken = UUID.randomUUID().toString();
        var token = Token.builder()
                .token(resetToken)
                .user(user)
                .tokenType(TokenType.PASSWORD_RESET)
                .expired(false)
                .revoked(false)
                .createdDate(LocalDateTime.now())
                .build();
        tokenRepository.save(token);

        // Send email with reset link via JavaMailSender
        String resetUrl = "http://localhost:8081/reset-password?token=" + resetToken;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Hello,\n\nTo reset your password, click the following link:\n" + resetUrl +
                "\n\nThis link will expire in 1 hour.\n\nIf you did not request a password reset, please ignore this email.\n\nBest regards,\nTaskSync Team");
        message.setFrom(fromEmail);

        try {
            mailSender.send(message);
            logger.info("Password reset email sent to: " + email + ", Token: " + resetToken);
        } catch (MailException e) {
            logger.severe("Failed to send password reset email to: " + email + ", Error: " + e.getMessage());
            throw new IllegalStateException("Failed to send password reset email: " + e.getMessage());
        }

        return resetToken;
    }

    public void resetPassword(String resetToken, String newPassword) {
        var token = tokenRepository.findByToken(resetToken)
                .orElseThrow(() -> new IllegalStateException("Invalid reset token"));
        if (token.isExpired() || token.isRevoked() || token.getTokenType() != TokenType.PASSWORD_RESET || token.getCreatedDate().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new IllegalStateException("Invalid or expired reset token");
        }
        var user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        token.setExpired(true);
        token.setRevoked(true);
        tokenRepository.save(token);
        logger.info("Password reset successfully for user: " + user.getEmail());
    }
}