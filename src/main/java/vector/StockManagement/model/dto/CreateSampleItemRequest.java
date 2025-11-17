package vector.StockManagement.model.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class CreateSampleItemRequest {
    @NotNull
    private Map<String, Integer> sizes;

    @Column(nullable = false)
    private Long productId;
}
