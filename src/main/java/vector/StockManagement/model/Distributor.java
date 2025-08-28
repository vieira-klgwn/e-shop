package goma.gorilla.backend.model;

import goma.gorilla.backend.model.enums.AdjustmentReason;
import goma.gorilla.backend.model.enums.DistributorStatus;
import goma.gorilla.backend.model.enums.LocationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Distributor Entity
@Entity
@Table(name = "distributors", indexes = {
        @Index(name = "idx_distributor_email_tenant", columnList = "email, tenant_id", unique = true)
})
public class Distributor extends BaseEntity {

    @NotBlank
    @Size(max = 200)
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Size(max = 100)
    @Column(name = "contact_name")
    private String contactName;

    @Size(max = 20)
    @Column(name = "mobile")
    private String mobile;

    @Size(max = 20)
    @Column(name = "phone")
    private String phone;

    @Email
    @Size(max = 100)
    @Column(name = "email")
    private String email;

    @Column(name = "address", length = 500)
    private String address;

    @Size(max = 100)
    @Column(name = "city")
    private String city;

    @Size(max = 100)
    @Column(name = "region")
    private String region;

    @Size(max = 20)
    @Column(name = "postal_code")
    private String postalCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "bank_accounts", columnDefinition = "json")
    private List<Map<String, String>> bankAccounts = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "momo_accounts", columnDefinition = "json")
    private List<Map<String, String>> momoAccounts = new ArrayList<>();

    @DecimalMin("0.0")
    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @Column(name = "credit_balance", precision = 15, scale = 2)
    private BigDecimal creditBalance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DistributorStatus status = DistributorStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Size(max = 50)
    @Column(name = "tax_number")
    private String taxNumber;

    @Size(max = 50)
    @Column(name = "business_license")
    private String businessLicense;

    @OneToMany(mappedBy = "distributor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Store> stores = new ArrayList<>();

    // Constructors
    public Distributor() {}

    public Distributor(String companyName, String contactName, Tenant tenant) {
        this.companyName = companyName;
        this.contactName = contactName;
        this.tenant = tenant;
    }

    // Getters and Setters
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public List<Map<String, String>> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<Map<String, String>> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public List<Map<String, String>> getMomoAccounts() {
        return momoAccounts;
    }

    public void setMomoAccounts(List<Map<String, String>> momoAccounts) {
        this.momoAccounts = momoAccounts;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(BigDecimal creditBalance) {
        this.creditBalance = creditBalance;
    }

    public DistributorStatus getStatus() {
        return status;
    }

    public void setStatus(DistributorStatus status) {
        this.status = status;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

    // Helper methods
    public BigDecimal getAvailableCredit() {
        return creditLimit.subtract(creditBalance);
    }

    public boolean hasAvailableCredit(BigDecimal amount) {
        return getAvailableCredit().compareTo(amount) >= 0;
    }

    public void addStore(Store store) {
        stores.add(store);
        store.setDistributor(this);
    }

    public void removeStore(Store store) {
        stores.remove(store);
        store.setDistributor(null);
    }

    @Override
    public String toString() {
        return "Distributor{" +
                "id=" + getId() +
                ", companyName='" + companyName + '\'' +
                ", contactName='" + contactName + '\'' +
                ", status=" + status +
                '}';
    }
}
