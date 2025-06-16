package zama.learning.procureai.api.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password";
        String hashedPassword = encoder.encode(password);
        System.out.println("Plain password: " + password);
        System.out.println("BCrypt hash: " + hashedPassword);
        
        // Verify the hash
        boolean matches = encoder.matches(password, hashedPassword);
        System.out.println("Hash verification: " + matches);
    }
}
