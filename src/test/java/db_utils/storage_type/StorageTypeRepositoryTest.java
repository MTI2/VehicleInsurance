package db_utils.storage_type;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import vehicleinsurance.VehicleInsuranceApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {VehicleInsuranceApplication.class})
public class StorageTypeRepositoryTest
{

    @Autowired
    StorageTypeRepository sr;

    @Test
    public void When_ListAvaliableStoragesTypes_Except_NotEmptyListReturned()
    {
        Assert.assertFalse(sr.findAll().isEmpty());
    }
}