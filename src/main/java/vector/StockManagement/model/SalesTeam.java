package vector.StockManagement.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SalesTeam extends BaseEntity{
    private String name;
    private List<Activity> activities;
    private List <User> members;


}
