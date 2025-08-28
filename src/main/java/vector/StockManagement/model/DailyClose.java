package vector.StockManagement.model;

import lombok.*;
import vector.StockManagement.model.enums.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Daily Close Entity
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "daily_closes", indexes = {
        @Index(name = "idx_daily_close_location_date", columnList = "level, location_id, date", unique = true),
        @Index(name = "idx_daily_close_status", columnList = "status"),
        @Index(name = "idx_daily_close_date", columnList = "date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyClose extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private LocationType level;

    @NotNull
    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @DecimalMin("0.0")
    @Column(name = "reported_sales", precision = 15, scale = 2)
    private BigDecimal reportedSales = BigDecimal.ZERO;

    @DecimalMin("0.0")
    @Column(name = "cash_total", precision = 15, scale = 2)
    private BigDecimal cashTotal = BigDecimal.ZERO;

    @Column(name = "discrepancies", length = 1000)
    private String discrepancies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by", nullable = false)
    private User closedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DailyCloseStatus status = DailyCloseStatus.SUBMITTED;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;


   }