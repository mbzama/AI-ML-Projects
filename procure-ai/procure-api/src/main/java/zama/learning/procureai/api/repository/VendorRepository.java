package zama.learning.procureai.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zama.learning.procureai.api.model.Vendor;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByUserId(Long userId);
    List<Vendor> findByIsApproved(Boolean isApproved);
    Optional<Vendor> findByCompanyName(String companyName);
    
    // Additional methods for the improved vendor endpoints
    List<Vendor> findByCompanyNameContainingIgnoreCase(String companyName);
    List<Vendor> findByIsApprovedAndCompanyNameContainingIgnoreCase(Boolean isApproved, String companyName);
    long countByIsApproved(Boolean isApproved);
    
    // Methods for checking duplicates during vendor creation/update
    boolean existsByRegistrationNumber(String registrationNumber);
    boolean existsByTaxId(String taxId);
}
