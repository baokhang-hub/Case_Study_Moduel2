package service;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import entity.Tenant;
import entity.User;
import entity.UserRole;
import repository.UserRepository;

public class UserService {
    private static final int PASSWORD_HASH_ITERATIONS = 65_536;
    private final UserRepository userRepository = new UserRepository();
    private final List<User> users = userRepository.read();
    private static final UserService INSTANCE = new UserService();

    public static UserService getObject() {
        return INSTANCE;
    }

    public UserService() {
    }

    public boolean hasManagerAccount() {
        for (User user : users) {
            if (user.getRole() == UserRole.MANAGER) {
                return true;
            }
        }
        return false;
    }

    public User createManagerAccount(String username, String password) {
        validateCredentials(username, password);
        ensureUsernameAvailable(username);
        User manager = new User(System.currentTimeMillis(), username.trim(), hashPassword(password),
                UserRole.MANAGER, 0, false);
        users.add(manager);
        userRepository.write(users);
        return manager;
    }

    public User createTenantAccount(Tenant tenant) {
        String username = tenant.getIdentityNumber();
        String temporaryPassword = tenant.getPhoneNumber();
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("CCCD khong duoc de trong vi duoc dung lam ten dang nhap.");
        }
        if (temporaryPassword == null || temporaryPassword.isEmpty()) {
            throw new IllegalArgumentException("Khach thue can co so dien thoai de dang nhap ban dau.");
        }
        ensureUsernameAvailable(username);
        User account = new User(System.currentTimeMillis(), username.trim(), hashPassword(temporaryPassword),
                UserRole.TENANT, tenant.getId(), false);
        users.add(account);
        userRepository.write(users);
        return account;
    }

    public boolean hasTenantAccount(long tenantId) {
        return findByTenantId(tenantId) != null;
    }

    public void ensureTenantAccount(Tenant tenant) {
        if (!hasTenantAccount(tenant.getId())) {
            createTenantAccount(tenant);
        }
    }

    public User authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        for (User user : users) {
            if (user.getUsername().equals(username.trim()) && passwordMatches(password, user.getPasswordHash())) {
                if (!user.getPasswordHash().startsWith("pbkdf2$")) {
                    user.setPasswordHash(hashPassword(password));
                    userRepository.write(users);
                }
                return user;
            }
        }
        return null;
    }

    public void changePassword(User user, String newPassword) {
        if (user == null) {
            throw new IllegalArgumentException("Tai khoan khong hop le.");
        }
        String passwordError = getPasswordChangeError(user, newPassword);
        if (passwordError != null) {
            throw new IllegalArgumentException(passwordError);
        }
        user.setPasswordHash(hashPassword(newPassword));
        user.setMustChangePassword(false);
        userRepository.write(users);
    }

    public String getPasswordValidationError(String password) {
        if (password == null || password.length() < 7) {
            return "Mat khau phai dai hon 6 ky tu (toi thieu 7 ky tu).";
        }
        if (!password.matches("(?s).*\\p{Lu}.*")) {
            return "Mat khau phai co it nhat 1 chu cai viet hoa.";
        }
        if (!password.matches("(?s).*[0-9].*")) {
            return "Mat khau phai co it nhat 1 chu so.";
        }
        boolean hasSpecialCharacter = false;
        for (char character : password.toCharArray()) {
            if (!Character.isLetterOrDigit(character) && !Character.isWhitespace(character)) {
                hasSpecialCharacter = true;
                break;
            }
        }
        if (!hasSpecialCharacter) {
            return "Mat khau phai co it nhat 1 ky tu dac biet.";
        }
        return null;
    }

    public String getPasswordChangeError(User user, String password) {
        String validationError = getPasswordValidationError(password);
        if (validationError != null) {
            return validationError;
        }
        if (user != null && passwordMatches(password, user.getPasswordHash())) {
            return "Mat khau moi phai khac mat khau hien tai.";
        }
        return null;
    }

    public void updateTenantCredentials(long tenantId, String newUsername, String newPhoneNumber) {
        User tenantAccount = findByTenantId(tenantId);
        if (tenantAccount == null) {
            return;
        }
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("CCCD khong duoc de trong vi duoc dung lam ten dang nhap.");
        }
        String normalizedUsername = newUsername.trim();
        for (User user : users) {
            if (user != tenantAccount && user.getUsername().equals(normalizedUsername)) {
                throw new IllegalArgumentException("CCCD nay da duoc dung lam ten dang nhap.");
            }
        }
        if (tenantAccount.mustChangePassword()
                && (newPhoneNumber == null || newPhoneNumber.isEmpty())) {
            throw new IllegalArgumentException("Khach thue can co so dien thoai de dang nhap ban dau.");
        }
        tenantAccount.setUsername(normalizedUsername);
        if (tenantAccount.mustChangePassword()) {
            tenantAccount.setPasswordHash(hashPassword(newPhoneNumber));
            tenantAccount.setMustChangePassword(false);
        }
        userRepository.write(users);
    }

    public void deleteTenantAccount(long tenantId) {
        User tenantAccount = findByTenantId(tenantId);
        if (tenantAccount != null) {
            users.remove(tenantAccount);
            userRepository.write(users);
        }
    }

    private User findByTenantId(long tenantId) {
        for (User user : users) {
            if (user.getRole() == UserRole.TENANT && user.getTenantId() == tenantId) {
                return user;
            }
        }
        return null;
    }

    private void ensureUsernameAvailable(String username) {
        String normalizedUsername = username.trim();
        for (User user : users) {
            if (user.getUsername().equals(normalizedUsername)) {
                throw new IllegalArgumentException("Ten dang nhap da ton tai: " + normalizedUsername);
            }
        }
    }

    private void validateCredentials(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Ten dang nhap khong duoc de trong.");
        }
        String passwordError = getPasswordValidationError(password);
        if (passwordError != null) {
            throw new IllegalArgumentException(passwordError);
        }
    }

    private String hashPassword(String password) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            byte[] hash = derivePassword(password, salt);
            return "pbkdf2$" + PASSWORD_HASH_ITERATIONS + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Khong the ma hoa mat khau.", e);
        }
    }

    private boolean passwordMatches(String password, String savedPasswordHash) {
        try {
            String[] parts;
            byte[] salt;
            byte[] expectedHash;
            byte[] actualHash;
            if (savedPasswordHash.startsWith("pbkdf2$")) {
                parts = savedPasswordHash.split("\\$", 4);
                if (parts.length != 4) {
                    return false;
                }
                int iterations = Integer.parseInt(parts[1]);
                salt = Base64.getDecoder().decode(parts[2]);
                expectedHash = Base64.getDecoder().decode(parts[3]);
                actualHash = derivePassword(password, salt, iterations);
            } else {
                // Support and transparently upgrade accounts saved by the earlier SHA-256 format.
                parts = savedPasswordHash.split(":", 2);
                if (parts.length != 2) {
                    return false;
                }
                salt = Base64.getDecoder().decode(parts[0]);
                expectedHash = Base64.getDecoder().decode(parts[1]);
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.update(salt);
                actualHash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (IllegalArgumentException | GeneralSecurityException e) {
            return false;
        }
    }

    private byte[] derivePassword(String password, byte[] salt) throws GeneralSecurityException {
        return derivePassword(password, salt, PASSWORD_HASH_ITERATIONS);
    }

    private byte[] derivePassword(String password, byte[] salt, int iterations) throws GeneralSecurityException {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                iterations, 256);
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return keyFactory.generateSecret(keySpec).getEncoded();
        } finally {
            keySpec.clearPassword();
        }
    }
}
