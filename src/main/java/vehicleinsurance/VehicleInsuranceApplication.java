package vehicleinsurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;



@SpringBootApplication
@EntityScan(basePackages = {"db_utils.employee",
                            "db_utils.contract",
                            "db_utils.insurance_pcg",
                            "db_utils.packages",
                            "db_utils.storage_type"})

@EnableJpaRepositories({"db_utils.employee",
        "db_utils.contract",
        "db_utils.insurance_pcg",
        "db_utils.packages",
        "db_utils.storage_type"})
@ComponentScan(basePackages = { "employee"} )
public class VehicleInsuranceApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(VehicleInsuranceApplication.class, args);
    }
}
