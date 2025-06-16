package com.procure.repository;

import com.procure.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByUserId(Long userId);
    List<Vendor> findByIsApproved(Boolean isApproved);
    Optional<Vendor> findByCompanyName(String companyName);
}
