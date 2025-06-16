package com.procure.config;

import com.procure.model.User;
import com.procure.model.Vendor;
import com.procure.model.enums.UserRole;
import com.procure.repository.UserRepository;
import com.procure.repository.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataLoader implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Loading initial data...");

        // Check if users already exist
        if (userRepository.count() > 0) {
            logger.info("Users already exist, skipping data initialization.");
            return;
        }

        // Create Admin User
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRoles(Set.of(UserRole.CREATOR));
        admin.setIsActive(true);
        userRepository.save(admin);
        logger.info("Created admin user: admin/password");

        // Create Approver User
        User approver = new User();
        approver.setUsername("approver1");
        approver.setEmail("approver1@example.com");
        approver.setPassword(passwordEncoder.encode("password"));
        approver.setFirstName("John");
        approver.setLastName("Approver");
        approver.setRoles(Set.of(UserRole.APPROVER));
        approver.setIsActive(true);
        userRepository.save(approver);
        logger.info("Created approver user: approver1/password");

        // Create Vendor User
        User vendorUser = new User();
        vendorUser.setUsername("vendor1");
        vendorUser.setEmail("vendor1@example.com");
        vendorUser.setPassword(passwordEncoder.encode("password"));
        vendorUser.setFirstName("Jane");
        vendorUser.setLastName("Vendor");
        vendorUser.setRoles(Set.of(UserRole.VENDOR));
        vendorUser.setIsActive(true);
        userRepository.save(vendorUser);

        // Create Vendor Profile
        Vendor vendor = new Vendor();
        vendor.setUser(vendorUser);
        vendor.setCompanyName("ABC Supplies Inc.");
        vendor.setRegistrationNumber("REG123456");
        vendor.setTaxId("TAX789012");
        vendor.setAddress("123 Business Street");
        vendor.setCity("Business City");
        vendor.setState("BC");
        vendor.setPostalCode("12345");
        vendor.setCountry("USA");
        vendor.setContactPerson("Jane Vendor");
        vendor.setIsApproved(true);
        vendorRepository.save(vendor);
        logger.info("Created vendor user: vendor1/password with company profile");

        logger.info("Initial data loading completed successfully!");
    }
}
