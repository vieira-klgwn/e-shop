package vector.StockManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Audit Log Entity
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @NotBlank
    @Size(max = 100)
    @Column(name = "action", nullable = false)
    private String action; // e.g., "CREATE", "UPDATE", "DELETE", "APPROVE"

    @NotBlank
    @Size(max = 100)
    @Column(name = "entity", nullable = false)
    private String entity; // e.g., "Order", "Product", "Invoice"

    @Column(name = "entity_id")
    private Long entityId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "before_data", columnDefinition = "json")
    private Map<String, Object> beforeData = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "after_data", columnDefinition = "json")
    private Map<String, Object> afterData = new HashMap<>();

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Size(max = 45)
    @Column(name = "ip_address")
    private String ipAddress;

    @Size(max = 500)
    @Column(name = "user_agent")
    private String userAgent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Size(max = 500)
    @Column(name = "description")
    private String description;

    @Size(max = 100)
    @Column(name = "entity_type")
    private String entityType;

    @Size(max = 100)
    @Column(name = "operation")
    private String operation;

    @Column(name = "old_values", columnDefinition = "text")
    private String oldValues;

    @Column(name = "new_values", columnDefinition = "text")
    private String newValues;

    @Column(name = "user_id")
    private Long userId;

    @Size(max = 200)
    @Column(name = "user_name")
    private String userName;

    public AuditLog(User actor, String action, String entity, Long entityId, Tenant tenant) {
        this();
        this.actor = actor;
        this.action = action;
        this.entity = entity;
        this.entityId = entityId;
        this.tenant = tenant;
    }


    @Override
    public String toString() {
        return "AuditLog{" +
                ", actor=" + (actor != null ? actor.getUsername() : null) +
                ", action='" + action + '\'' +
                ", entity='" + entity + '\'' +
                ", entityId=" + entityId +
                ", timestamp=" + timestamp +
                '}';
    }
}