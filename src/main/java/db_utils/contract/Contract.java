package db_utils.contract;




import javax.persistence.*;
import java.sql.Date;



@Entity
@Table(name="WaitingContracts")
public class Contract
{
    @Id
    @Column(name="ID", updatable=false, nullable=false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;


    @Column(nullable=false)
    private long vehicleYear;

    @Column(nullable=false)
    private String clientToken;

    @Column(nullable=false)
    private String requestToken;

    @Column
    private String vin;

    @Column(nullable=false)
    private Date date;

    @Column(nullable=false)
    private String model;

    @Column
    private String brand;

    @Column
    private String plateNumber;

    @Column
    private String insurancePcg;

    @Column
    private String storageName;


    public long getId()
    {
        return id;
    }

    public long getVehicleYear()
    {
        return vehicleYear;
    }

    public void setVehicleYear(long vehicleYear)
    {
        this.vehicleYear = vehicleYear;
    }

    public String getVin()
    {
        return vin;
    }

    public void setVin(String vin)
    {
        this.vin = vin;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getBrand()
    {
        return brand;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }


    public String getStorageName()
    {
        return storageName;
    }

    public void setStorageName(String storageName)
    {
        this.storageName = storageName;
    }

    public String getInsurancePcg()
    {
        return insurancePcg;
    }

    public void setInsurancePcg(String insurancePcg)
    {
        this.insurancePcg = insurancePcg;
    }

    public String getPlateNumber()
    {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber)
    {
        this.plateNumber = plateNumber;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getClientToken()
    {
        return clientToken;
    }

    public void setClientToken(String clientToken)
    {
        this.clientToken = clientToken;
    }

    public String getRequestToken()
    {
        return requestToken;
    }

    public void setRequestToken(String requestToken)
    {
        this.requestToken = requestToken;
    }


}
