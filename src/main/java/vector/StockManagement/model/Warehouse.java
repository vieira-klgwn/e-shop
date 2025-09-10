package vector.StockManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "warehouses", indexes = {
        @Index(name = "idx_warehouse_code_tenant", columnList = "code, tenant_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Column(name = "code", nullable = false)
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

    @OneToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    @OneToOne(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private Distributor distributor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "json")
    private Map<String, Object> attributes = new HashMap<>();

    @OneToMany(mappedBy = "warehouse")
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "warehouse")
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "warehouse")
    private List<Order> orders = new ArrayList<>();



    public Warehouse(Distributor distributor, String name, String code, Tenant tenant) {
        this.distributor = distributor;
        this.name = name;
        this.code = code;
        this.tenant = tenant;
    }

    @Override
    public String toString() {
        return "User{" +
                ", username='" + name + '\'' +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                '}';
    }


}
