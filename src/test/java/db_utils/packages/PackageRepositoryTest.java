package db_utils.packages;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import vehicleinsurance.VehicleInsuranceApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {VehicleInsuranceApplication.class})
public class PackageRepositoryTest
{
    @Autowired
    PackagesRepository pr;

    @Test
    public void When_ListAvaliablePackageTypes_Except_NotEmptyListReturned()
    {
        Assert.assertFalse(pr.findAll().isEmpty());
    }

}