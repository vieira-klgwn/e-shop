package vector.StockManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@EqualsAndHashCode(callSuper = true)
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@org.hibernate.annotations.Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sample extends BaseEntity {

    private Long quantity;


    @ManyToMany
    @JoinTable(
            name = "sample_product",
            joinColumns = @JoinColumn(name = "sample_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;



}
