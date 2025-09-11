package vector.StockManagement.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import vector.StockManagement.model.enums.DistributorStatus;
import vector.StockManagement.model.enums.LocationType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Distributor Entity
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "distributors", indexes = {
        @Index(name = "idx_distributor_email_tenant", columnList = "email, tenant_id", unique = true)
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    //this is a big problem , i must correct it.....I mean having warehouse and store fields in distributor class
    @OneToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;



    @Size(max = 50)
    @Column(name = "tax_number")
    private String taxNumber;

    @Column(name = "distributor_level")
    private LocationType distributorLevel;

    @Size(max = 50)
    @Column(name = "business_license")
    private String businessLicense;




    public Distributor(String companyName, String contactName, Tenant tenant) {
        this.companyName = companyName;
        this.contactName = contactName;
        this.tenant = tenant;
    }


    // Helper methods
    public BigDecimal getAvailableCredit() {
        return creditLimit.subtract(creditBalance);
    }

    public boolean hasAvailableCredit(BigDecimal amount) {
        return getAvailableCredit().compareTo(amount) >= 0;
    }

//    public void addStore(Store store) {
//        stores.add(store);
//        store.setDistributor(this);
//    }
//
//    public void removeStore(Store store) {
//        stores.remove(store);
//        store.setDistributor(null);
//    }

    @Override
    public String toString() {
        return "Distributor{" +
                ", companyName='" + companyName + '\'' +
                ", contactName='" + contactName + '\'' +
                ", status=" + status +
                '}';
    }
}
