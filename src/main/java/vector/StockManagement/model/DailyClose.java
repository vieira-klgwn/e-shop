package goma.gorilla.backend.model;

import goma.gorilla.backend.model.enums.*;
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
@Entity
@Table(name = "daily_closes", indexes = {
        @Index(name = "idx_daily_close_location_date", columnList = "level, location_id, date", unique = true),
        @Index(name = "idx_daily_close_status", columnList = "status"),
        @Index(name = "idx_daily_close_date", columnList = "date")
})
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


    // Notification Entity
    @Entity
    @Table(name = "notifications", indexes = {
            @Index(name = "idx_notification_type", columnList = "type"),
            @Index(name = "idx_notification_channel", columnList = "channel"),
            @Index(name = "idx_notification_status", columnList = "status"),
            @Index(name = "idx_notification_recipient", columnList = "recipient_id"),
            @Index(name = "idx_notification_created", columnList = "created_at")
    })
    public static class Notification extends BaseEntity {

        @NotNull
        @Enumerated(EnumType.STRING)
        @Column(name = "type", nullable = false)
        private NotificationType type;

        @NotNull
        @Enumerated(EnumType.STRING)
        @Column(name = "channel", nullable = false)
        private NotificationChannel channel;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "recipient_id")
        private User recipient;

        @Size(max = 200)
        @Column(name = "recipient_address")
        private String recipientAddress; // email or phone number

        @NotBlank
        @Size(max = 200)
        @Column(name = "subject")
        private String subject;

        @Column(name = "message", length = 1000)
        private String message;

        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "payload", columnDefinition = "json")
        private Map<String, Object> payload = new HashMap<>();

        @NotNull
        @Enumerated(EnumType.STRING)
        @Column(name = "status", nullable = false)
        private NotificationStatus status = NotificationStatus.PENDING;

        @Column(name = "sent_at")
        private LocalDateTime sentAt;

        @Column(name = "delivered_at")
        private LocalDateTime deliveredAt;

        @Column(name = "failed_at")
        private LocalDateTime failedAt;

        @Size(max = 500)
        @Column(name = "error_message")
        private String errorMessage;

        @Column(name = "retry_count")
        private Integer retryCount = 0;

        @Column(name = "next_retry_at")
        private LocalDateTime nextRetryAt;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "tenant_id", nullable = false)
        private Tenant tenant;

        @Size(max = 100)
        @Column(name = "reference_type")
        private String referenceType; // e.g., "ORDER", "INVOICE"

        @Column(name = "reference_id")
        private Long referenceId;

        // Constructors
        public Notification() {
        }

        public Notification(NotificationType type, NotificationChannel channel,
                            String subject, String message, Tenant tenant) {
            this.type = type;
            this.channel = channel;
            this.subject = subject;
            this.message = message;
            this.tenant = tenant;
        }

        // Getters and Setters
        public NotificationType getType() {
            return type;
        }

        public void setType(NotificationType type) {
            this.type = type;
        }

        public NotificationChannel getChannel() {
            return channel;
        }

        public void setChannel(NotificationChannel channel) {
            this.channel = channel;
        }

        public User getRecipient() {
            return recipient;
        }

        public void setRecipient(User recipient) {
            this.recipient = recipient;
        }

        public String getRecipientAddress() {
            return recipientAddress;
        }

        public void setRecipientAddress(String recipientAddress) {
            this.recipientAddress = recipientAddress;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, Object> getPayload() {
            return payload;
        }

        public void setPayload(Map<String, Object> payload) {
            this.payload = payload;
        }

        public NotificationStatus getStatus() {
            return status;
        }

        public void setStatus(NotificationStatus status) {
            this.status = status;
        }

        public LocalDateTime getSentAt() {
            return sentAt;
        }

        public void setSentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
        }

        public LocalDateTime getDeliveredAt() {
            return deliveredAt;
        }

        public void setDeliveredAt(LocalDateTime deliveredAt) {
            this.deliveredAt = deliveredAt;
        }

        public LocalDateTime getFailedAt() {
            return failedAt;
        }

        public void setFailedAt(LocalDateTime failedAt) {
            this.failedAt = failedAt;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Integer getRetryCount() {
            return retryCount;
        }

        public void setRetryCount(Integer retryCount) {
            this.retryCount = retryCount;
        }

        public LocalDateTime getNextRetryAt() {
            return nextRetryAt;
        }

        public void setNextRetryAt(LocalDateTime nextRetryAt) {
            this.nextRetryAt = nextRetryAt;
        }

        public Tenant getTenant() {
            return tenant;
        }

        public void setTenant(Tenant tenant) {
            this.tenant = tenant;
        }

        public String getReferenceType() {
            return referenceType;
        }

        public void setReferenceType(String referenceType) {
            this.referenceType = referenceType;
        }

        public Long getReferenceId() {
            return referenceId;
        }

        public void setReferenceId(Long referenceId) {
            this.referenceId = referenceId;
        }

        // Helper methods
        public void markAsSent() {
            this.status = NotificationStatus.SENT;
            this.sentAt = LocalDateTime.now();
        }

        public void markAsDelivered() {
            this.status = NotificationStatus.DELIVERED;
            this.deliveredAt = LocalDateTime.now();
        }

        public void markAsFailed(String errorMessage) {
            this.status = NotificationStatus.FAILED;
            this.failedAt = LocalDateTime.now();
            this.errorMessage = errorMessage;
            this.retryCount++;

            // Calculate next retry time (exponential backoff)
            long delayMinutes = Math.min(60, (long) Math.pow(2, retryCount - 1) * 5);
            this.nextRetryAt = LocalDateTime.now().plusMinutes(delayMinutes);
        }

        public boolean canRetry() {
            return status == NotificationStatus.FAILED &&
                    retryCount < 5 &&
                    LocalDateTime.now().isAfter(nextRetryAt);
        }

        public void setReference(String referenceType, Long referenceId) {
            this.referenceType = referenceType;
            this.referenceId = referenceId;
        }

        @Override
        public String toString() {
            return "Notification{" +
                    "id=" + getId() +
                    ", type=" + type +
                    ", channel=" + channel +
                    ", status=" + status +
                    ", subject='" + subject + '\'' +
                    ", retryCount=" + retryCount +
                    '}';
        }
    }}