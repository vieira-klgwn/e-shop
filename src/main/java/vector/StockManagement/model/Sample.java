package vector.StockManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.w3c.dom.stylesheets.LinkStyle;
import vector.StockManagement.model.enums.SampleStatus;

import java.time.LocalDateTime;
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

    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SampleItem> items; // Line items: product + quantity

    @Column(name = "tenant_id") // For multi-tenancy
    private Long tenantId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "notes") // Optional: reason for samples (e.g., "Product effectiveness test")
    private String notes;

    @Enumerated(EnumType.STRING)
    private SampleStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "distributor_id")
    private User distributor;



}
