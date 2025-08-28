package goma.gorilla.backend.model;

import goma.gorilla.backend.model.enums.PriceListLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

// Price List Entity
@Entity
@Table(name = "price_lists")
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
    @Size(max = 3)
    @Column(name = "currency", nullable = false)
    private String currency; // ISO 4217 currency code

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "description", length = 500)
    private String description;

    // Constructors
    public PriceList() {}

    public PriceList(String name, PriceListLevel level, String currency, Tenant tenant) {
        this.name = name;
        this.level = level;
        this.currency = currency;
        this.tenant = tenant;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PriceListLevel getLevel() {
        return level;
    }

    public void setLevel(PriceListLevel level) {
        this.level = level;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", currency='" + currency + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
