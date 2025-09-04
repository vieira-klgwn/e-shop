package vector.StockManagement.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import vector.StockManagement.model.enums.DistributorStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateWareHouseRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String code;

    private String address;

    private String city;

    private String region;

    private String postalCode;

    private String phone;

    private String email;

    private User manager;

    private Distributor distributor;

    private Tenant tenant;

    private Map<String, Object> attributes = new HashMap<>();

    //Distributor details

    private String companyName;

    private String distributorContactName;

    private String distributorMobile;

    private String distributorPhone;

    private String distributorEmail;

    private String distributorAddress;

    private String distributorCity;

    private String distributorRegion;

    private String distributorPostalCode;

    private List<Map<String, String>> distributorBankAccounts = new ArrayList<>();

    private List<Map<String, String>> distributorMomoAccounts = new ArrayList<>();

    private String distributorTaxNumber;

    private String distributorBusinessLicense;
}
