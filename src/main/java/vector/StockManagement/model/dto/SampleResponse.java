package vector.StockManagement.model.dto;

import lombok.Data;
import vector.StockManagement.model.SampleItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SampleResponse {
    private Long id;
    private Integer totalItems;
    private LocalDateTime createdAt;
    private String sampleStatus;
    private String productName;
    private List<SampleItem> sampleItems = new ArrayList<>();
    private String distributorFirstName;
    private String distributorLastName;
}