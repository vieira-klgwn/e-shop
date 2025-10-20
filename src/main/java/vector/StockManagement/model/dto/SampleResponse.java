package vector.StockManagement.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SampleResponse {
    private Long id;
    private Integer totalItems;
    private LocalDateTime createdAt;
}