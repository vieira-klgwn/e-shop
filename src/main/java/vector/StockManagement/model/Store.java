package vector.StockManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "stores", indexes = {
        @Index(name = "idx_store_code", columnList = "code", unique = true),
        @Index(name = "idx_store_distributor", columnList = "distributor_id")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Store extends BaseEntity {



    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Column(name = "code", nullable = false, unique = true)
    private String code;

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

    @Size(max = 20)
    @Column(name = "phone")
    private String phone;

    @Email
    @Size(max = 100)
    @Column(name = "email")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(name = "sales_teams")
    private List<SalesTeam> salesTeams;


    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "attributes", columnDefinition = "json")
    private Map<String, Object> attributes = new HashMap<>();

    @OneToOne(mappedBy = "store", cascade = CascadeType.ALL)
    private Distributor distributor;


    // --- toString ---
    @Override
    public String toString() {
        return "Store{" +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
