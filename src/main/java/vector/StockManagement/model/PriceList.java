package vector.StockManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Filter;
import vector.StockManagement.model.enums.PriceListLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Price List Entity
@EqualsAndHashCode(callSuper = true)
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(name = "price_lists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceList extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private PriceListLevel level;

    @NotBlank
    @Column(name = "currency", nullable = false)
    private String currency; // ISO 4217 currency code

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonIgnore
    private Tenant tenant;

    @Column(name = "description", length = 500)
    private String description;

    @OneToMany(mappedBy = "priceList")
    @JsonIgnore
    private List<PriceListItem> items = new ArrayList<>();

// I removed requirement of tenant in request body

    public PriceList(String name, PriceListLevel level, String currency) {
        this.name = name;
        this.level = level;
        this.currency = currency;


    }


    // Helper methods
    public boolean isValidForDate(LocalDate date) {
        LocalDate checkDate = (date != null) ? date : LocalDate.now();
        boolean afterValidFrom = validFrom == null || !checkDate.isBefore(validFrom);
        boolean beforeValidTo = validTo == null || !checkDate.isAfter(validTo);
        return afterValidFrom && beforeValidTo && isActive;
    }

    @Override
    public String toString() {
        return "PriceList{" +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", currency='" + currency + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
