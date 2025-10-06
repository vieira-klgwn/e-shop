package vector.StockManagement.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vector.StockManagement.config.JwtService;
import vector.StockManagement.model.Distributor;
import vector.StockManagement.model.Tenant;
import vector.StockManagement.model.Token;
import vector.StockManagement.model.enums.Role;
import vector.StockManagement.model.enums.TokenType;
import vector.StockManagement.model.User;
import vector.StockManagement.model.enums.Gender;
import vector.StockManagement.repositories.TenantRepository;
import vector.StockManagement.repositories.TokenRepository;
import vector.StockManagement.repositories.UserRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
 

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;
    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());
    private final TenantRepository tenantRepository;

    @Value("${spring.mail.from}")
    private String fromEmail;


    public AuthenticationResponse register(RegisterRequest request) {
        logger.info("Registering user email=" + request.getEmail() + ", tenantId=" + request.getTenantId() + ", role=" + request.getRole());
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

        User distributor = null;

        if (request.getRole() == Role.RETAILER || request.getRole() == Role.STORE_MANAGER || request.getRole() == Role.ACCOUNTANT_AT_STORE) {
            distributor = userRepository.findById(request.getDistributor_id()).orElseThrow(()-> new IllegalStateException("Distributor not found"));
        }

        if(request.getPassword().length() < 8) {
            throw new IllegalStateException("Password must be 8 characters long");
        }





        Gender gender;
        try {
            gender = Gender.valueOf(request.getGender().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid gender value: " + request.getGender());
        }

        Tenant tenant = tenantRepository.findById(request.getTenantId()).orElseThrow(() -> new IllegalStateException("Tenant not found"));

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .gender(gender)
                .tenant(tenant)
                .birthDate(request.getBirthDate())
                .phone(request.getPhone())
                .distributor(distributor)
                .nationality(request.getNationality())
                .build();
        user.setCreatedAt(LocalDateTime.now());
        var savedUser = userRepository.save(user);
        logger.info("User saved with id=" + savedUser.getId() + ", tenantId=" + (savedUser.getTenant()!=null ? savedUser.getTenant().getId() : null));

        // Generate and save tokens
        var token = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        logger.info("Generated tokens for user=" + savedUser.getEmail());
        saveUserToken(savedUser, token);
        saveUserToken(savedUser, refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
    }





    public AuthenticationResponse registerAdmin(RegisterRequest request) {
        logger.info("Registering tenant admin email=" + request.getEmail() + ", tenantId=" + (request.getTenant()!=null?request.getTenant().getId():null));
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
        logger.info("Admin saved with id=" + savedUser.getId());

        // Generate and save tokens
        var token = jwtService.generateTokenOnSignUp(user ,request.getTenant().getId());
        var refreshToken = jwtService.generateRefreshToken(user); // adding tenant id to the tenantContext not implemented
        logger.info("Generated tokens for admin=" + savedUser.getEmail());
        saveUserToken(savedUser, token);
        saveUserToken(savedUser, refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
    }


    public AuthenticationResponse createManagingDirector (RegisterRequest request) {
        logger.info("Creating Managing Director email=" + request.getEmail());
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
                .tenant(tenantRepository.findById(request.getTenantId()).orElseThrow(() -> new IllegalStateException("Tenant not found")))
                .phone(request.getPhone())
                .nationality(request.getNationality())
                .build();
        user.setCreatedAt(LocalDateTime.now());
        var savedUser = userRepository.save(user);
        logger.info("Managing Director saved with id=" + savedUser.getId());

        // Generate and save tokens
        var token = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user); // Functionality to add tenant to tenant Context not implemented
        logger.info("Generated tokens for MD=" + savedUser.getEmail());
        saveUserToken(savedUser, token);
        saveUserToken(savedUser, refreshToken);


        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();

    }



    @Transactional
    public AuthenticationResponse createSuperAdmin (RegisterRequest request) {
        logger.info("Creating Super Admin email=" + request.getEmail());
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
        logger.info("Created tenant for super admin id=" + tenant.getId());

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
        logger.info("Super Admin saved with id=" + savedUser.getId());

        // Generate and save tokens
        var token = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user); // Functionality to add tenant to tenant Context not implemented

        logger.info("Generated tokens for Super Admin=" + savedUser.getEmail());
        saveUserToken(savedUser, token);
        saveUserToken(savedUser, refreshToken);


        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();

    }



    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        logger.info("Authenticating email=" + authenticationRequest.getEmail());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        var user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow(() -> new IllegalStateException("User not found: " + authenticationRequest.getEmail()));
        var token = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, refreshToken);
        saveUserToken(user, token);
        logger.info("Authenticated email=" + authenticationRequest.getEmail() + ", tenantId=" + (user.getTenant()!=null?user.getTenant().getId():null));
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

        for (Token token : tokenRepository.findAllValidTokenByUser(user.getId())) {
            token.setExpired(true);
            token.setRevoked(true);
        }

        String resetToken =UUID.randomUUID().toString();

        var token = Token.builder()
                .token(resetToken)
                .user(user)
                .tokenType(TokenType.PASSWORD_RESET)
                .expired(false)
                .revoked(false)
                .createdDate(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
        tokenRepository.save(token);

        // Send email with reset link via JavaMailSender
        String resetUrl = "https://stockscout-yqt4.onrender.com/api/auth/reset-password?te=" + token.getToken();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Hello,\n\nTo reset your password, click the following link:\n" + token.getToken() +
                "\n\nThis link will expire in 1 hour.\n\nIf you did not request a password reset, please ignore this email.\n\nBest regards,\nTaskSync Team");
        message.setFrom(fromEmail);


//
//




        try {



            String timestamp = LocalDateTime.now().toString();

            // HTML email content
            String htmlBody = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; color: #333; background-color: #f4f4f4; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .header { text-align: center; background-color: #007bff; color: #fff; padding: 10px; border-radius: 8px 8px 0 0; }
                    .header h1 { margin: 0; font-size: 24px; }
                    .content { padding: 20px; }
                    .quote { font-style: italic; font-size: 18px; color: #007bff; border-left: 4px solid #007bff; padding-left: 15px; margin: 20px 0; }
                    .greeting { font-size: 16px; line-height: 1.6; }
                    .signature { font-size: 14px; color: #555; margin-top: 20px; }
                    .footer { text-align: center; font-size: 12px; color: #777; margin-top: 20px; border-top: 1px solid #eee; padding-top: 10px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>GGM-FMS</h1>
                    </div>
                    <div class="content">
                        <p class="greeting">Dear Customer,</p>
                        <p class="greeting">Welcome to GGM.</p>
                        <p class="quote">You can reset your password via this link: </p>
                        <button><a href="%s">Reset Password</a></button>
                        <p class="signature">Warm regards,<br>The GGM Team</p>
                    </div>
                    <div class="footer">
                        <p>Sent at: %s | <a href="#">Unsubscribe</a></p>
                        <p>Contact us at: <a href="mailto:support@ggm.com">support@ggm.com</a></p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(resetUrl,timestamp);

            sendEmail("Password reset for GGM", htmlBody, email);
            logger.info("Password reset email sent to: " + email + ", Token: " + token.getToken());
        } catch (MailException e) {
            logger.severe("Failed to send password reset email to: " + email + ", Error: " + e.getMessage());
            throw new IllegalStateException("Failed to send password reset email: " + e.getMessage());
        }

        return token.getToken();
    }


    private void validateResetToken(Token token) {
        if (token.isExpired() || token.isRevoked() || token.getTokenType() != TokenType.PASSWORD_RESET) {
            throw new IllegalStateException("Invalid reset token");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {  // Use explicit expiry
            throw new IllegalStateException("Expired reset token");
        }
    }

    public void verifyResetToken(String resetToken) {
        var token = tokenRepository.findByToken(resetToken)
                .orElseThrow(() -> new IllegalStateException("Invalid reset token"));
        validateResetToken(token);
        logger.info("Token verified: " + token);
    }

    public void resetPassword(String resetToken, String newPassword) {
        var token = tokenRepository.findByToken(resetToken)
                .orElseThrow(() -> new IllegalStateException("Invalid reset token"));
        if (token.isExpired() || token.isRevoked() || token.getTokenType() != TokenType.PASSWORD_RESET || token.getCreatedDate().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new IllegalStateException("Invalid or expired reset token");
        }
        var user = token.getUser();
        if (!Objects.equals(newPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        else {
            throw new IllegalStateException("Password the same as the old one");
        }
        userRepository.save(user);
        token.setExpired(true);
        token.setRevoked(true);
        tokenRepository.save(token);
        logger.info("Password reset successfully for user: " + user.getEmail());
    }

    private void sendEmail(String subject, String htmlBody, String toEmail) {
        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setFrom(fromEmail);
            helper.setText(htmlBody, true); // true indicates HTML content
            mailSender.send(message);
            System.out.println("Email sent for subject: " + subject);
        } catch (MessagingException e) {
            System.err.println("Failed to send email for " + subject + ": " + e.getMessage());

        }
    }
}