package vector.StockManagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import vector.StockManagement.model.enums.ActivityCategory;

import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Activity extends BaseEntity {

    private String activityName;
    private String description;

    @Enumerated(EnumType.STRING)
    private ActivityCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;


    @CreatedDate
    private LocalDateTime dateTime;

}
