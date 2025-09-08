package vector.StockManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import vector.StockManagement.model.enums.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Product Entity
@Entity
@Table(name = "products")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "sku", nullable = false)
    private String sku;

    @NotBlank
    @Size(max = 200)
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "product")
    private List<OrderLine> orderLines;

    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Long price;


    @Column(name = "unit")
    private int unit; // e.g., "kg", "liter", "piece"

    @Size(max = 100)
    @Column(name = "size")
    private String size; // e.g., "500ml", "1kg"

    @Size(max = 50)
    @Column(name = "code")
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "jsonb")
    private Map<String, Object> attributes = new HashMap<>();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id") // removed , nullable = false
    private Tenant tenant;

    @Column(name = "barcode")
    private String barcode;

    @DecimalMin("0.0")
    @Column(name = "weight", precision = 10, scale = 3)
    private Long weight;

    @Size(max = 500)
    @Column(name = "image_url")
    private String imageUrl;


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
