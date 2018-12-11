package db_utils.employee;



import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import vehicleinsurance.VehicleInsuranceApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {VehicleInsuranceApplication.class})
public class EmployeeRepositoryTest
{

    private Employee saved;

    @Autowired
    EmployeeRepository er;

    @Before
    public void init()
    {
        Employee e = new Employee();
        e.setHash("Test1");
        e.setLogin("Test1");
        e.setMail("Test1");
        e.setName("Test1");
        e.setPhoneNumber(0);
        e.setSalt("Test1");
        e.setSurname("Test1");

        saved = er.save(e);

    }


    @After
    public void cleanUp()
    {
        er.delete(saved);
    }

    @Test
    public void When_QueriedForTest1UserWithHashTest1_Except_TrueReturned()
    {
       Assert.assertTrue( er.existsByLoginAndHash("Test1", "Test1") );
    }

    @Test
    public void When_QueriedForUserTest1_Except_TrueReturned()
    {
        Assert.assertTrue( er.existsByLogin("Test1") );
    }

    @Test
    public void When_QueriedForUserTest1Salt_Except_Test1SaltReturned()
    {
        String salt = "";

        salt = er.getByName("Test1").getSalt();

        Assert.assertEquals("Salt does not match 'Test1'!",  "Test1", salt );
    }

}