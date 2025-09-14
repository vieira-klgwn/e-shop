package vector.StockManagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@AllArgsConstructor
@NoArgsConstructor
public class SalesTeamsMember extends User {

    @ManyToOne
    @JoinColumn(name = "team_id")
    private SalesTeam team;



}
