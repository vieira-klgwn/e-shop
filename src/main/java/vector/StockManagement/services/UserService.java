package vector.StockManagement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.ChangePasswordRequest;
import vector.StockManagement.model.Tenant;
import vector.StockManagement.model.User;
import vector.StockManagement.repositories.TenantRepository;
import vector.StockManagement.repositories.UserRepository;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantRepository tenantRepository;

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Passwords do not match");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public User createUser(User newUser) {

        // user should belong to a particular tenant.
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User user) {
            Tenant currentTenant = user.getTenant();
            newUser.setTenant(currentTenant);
            userRepository.save(newUser);
            tenantRepository.save(currentTenant);
            currentTenant.getUsers().add(newUser);

            return newUser;
        }
        else {
            throw new IllegalStateException("Authenticated user is not of type CustomUserDetails");
        }

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setId(updatedUser.getId());
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setPassword(updatedUser.getPassword());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());
            user.setGender(updatedUser.getGender());
            return userRepository.save(user);
        }
        throw new RuntimeException("User with id " + id + " not found");
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}