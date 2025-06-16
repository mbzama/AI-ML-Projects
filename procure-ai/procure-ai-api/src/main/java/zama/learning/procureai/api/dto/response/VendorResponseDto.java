package zama.learning.procureai.api.dto.response;

import zama.learning.procureai.api.model.Vendor;
import java.time.LocalDateTime;

public class VendorResponseDto {
    private Long id;
    private String companyName;
    private String registrationNumber;
    private String taxId;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String contactPerson;
    private Boolean isApproved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User information
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    
    public VendorResponseDto() {}
    
    public VendorResponseDto(Vendor vendor) {
        this.id = vendor.getId();
        this.companyName = vendor.getCompanyName();
        this.registrationNumber = vendor.getRegistrationNumber();
        this.taxId = vendor.getTaxId();
        this.address = vendor.getAddress();
        this.city = vendor.getCity();
        this.state = vendor.getState();
        this.postalCode = vendor.getPostalCode();
        this.country = vendor.getCountry();
        this.contactPerson = vendor.getContactPerson();
        this.isApproved = vendor.getIsApproved();
        this.createdAt = vendor.getCreatedAt();
        this.updatedAt = vendor.getUpdatedAt();
        
        if (vendor.getUser() != null) {
            this.userId = vendor.getUser().getId();
            this.username = vendor.getUser().getUsername();
            this.email = vendor.getUser().getEmail();
            this.firstName = vendor.getUser().getFirstName();
            this.lastName = vendor.getUser().getLastName();
            this.phone = vendor.getUser().getPhone();
        }
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    
    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    
    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
