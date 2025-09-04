package vector.StockManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import vector.StockManagement.model.enums.DistributorStatus;
import vector.StockManagement.model.enums.LocationType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateStoreRequest {

    private Distributor distributor;

    private String name;

    private String code;

    private String address;

    private String city;

    private String region;

    private String postalCode;

    private String phone;

    private String email;

    private User manager;

    private Tenant tenant;

    private List<SalesTeam> salesTeams;

    private Map<String, Object> attributes = new HashMap<>();

    // distributor details

    private String distributorCompanyName;

    private String distributorContactName;

    private String distributorMobile;

    private String distributorPhone;

    private String distributorEmail;

    private String distributorAddress;

    private String distributorCity;

    private String distributorRegion;

    private String distributorPostalCode;

    //each distributor will be linked to one store

    private Store store;

    private String distributorTaxNumber;

    private String distributorBusinessLicense;

}
