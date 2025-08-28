package vector.StockManagement.model;

import lombok.*;
import vector.StockManagement.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tenants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Size(max = 50)
    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "description", length = 500)
    private String description;

    @ElementCollection
    @CollectionTable(name = "tenant_settings", joinColumns = @JoinColumn(name = "tenant_id"))
    @MapKeyColumn(name = "setting_key")
    @Column(name = "setting_value", length = 1000)
    private Map<String, String> settings = new HashMap<>();

    @Column(name = "active", nullable = false)
    private Boolean active = true;


    public Tenant(String name, String code) {
        this.name = name;
        this.code = code;
    }

   @OneToMany(mappedBy = "tenant")
   private List<User> users;



    @Override
    public String toString() {
        return "Tenant{" +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", active=" + active +
                '}';
    }
}