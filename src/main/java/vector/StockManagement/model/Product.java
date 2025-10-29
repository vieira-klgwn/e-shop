package vector.StockManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import vector.StockManagement.model.enums.Location;
import vector.StockManagement.model.enums.ProductCategory;
import vector.StockManagement.model.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Product Entity
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(name = "products")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @Size(max = 100)
    @Column(name = "sku", nullable = false)
    private String sku;


    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    private String unitOfMeasurement;

    @NotBlank
    @Size(max = 200)
    @Column(name = "name", nullable = false)
    private String name;




    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<OrderLine> orderLines;

    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Long price;


    @Column(name = "unit")
    private int unit; // e.g., "kg", "liter", "piece"
//
//    @Size(max = 100)
//    @Column(name = "size")
//    private String size; // e.g., "500ml", "1kg"


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductSize> sizes = new ArrayList<>();

    @Size(max = 50)
    @Column(name = "code")
    private String code;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "jsonb")
    private Map<String, Object> attributes = new HashMap<>();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "factory_price")
    private Long factoryPrice;

    @Column(name = "distributor_price")
    private Long distributorPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id") // removed , nullable = false
    @JsonIgnore
    private Tenant tenant;

    @Column(name = "barcode")
    private String barcode;

    @DecimalMin("0.0")
    @Column(name = "weight", precision = 10, scale = 3)
    private Long weight;

    @Size(max = 500)
    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    @JsonIgnore
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    private Location location;


    public Product(String sku, String name, Tenant tenant,ProductCategory category) {
        this.sku = sku;
        this.name = name;
//        this.tenant = tenant;
        this.category = category;
    }


    @Override
    public String toString() {
        return "Product{" +
                ", sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
