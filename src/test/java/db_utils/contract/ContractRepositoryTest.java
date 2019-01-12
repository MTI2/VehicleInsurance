package db_utils.contract;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import vehicleinsurance.VehicleInsuranceApplication;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {VehicleInsuranceApplication.class})
public class ContractRepositoryTest
{

    private Contract saved = null;

    @Autowired
    ContractRepository cr;


    @Before
    public void init()
    {
        Contract c = new Contract();
        c.setBrand("Test1");
        c.setModel("Test1");
        c.setStorageName("Test1");
        c.setVehicleYear(0);
        c.setClientToken("Test1");
        c.setVin("Test1");
        c.setRequestToken("Test1");
        c.setPlateNumber("Test1");
        c.setDate(Date.valueOf(LocalDate.now()));
        saved = cr.save(c);
    }
    @After
    public void cleanUp()
    {
        cr.delete(saved);
    }

    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^//

    @Test
    public void When_ListedByNowDate_Except_AtLeastOneContractReturned()
    {
        List<Contract> ret = null;




        ret = cr.findAllByDateBetween(saved.getDate(),saved.getDate());

        //TODO:<------ tu wstawic kod listowania po zakresie daty od-do --------->
        //TODO:<------ jezeli wylistuje się od wczoraj do jutra, baza zwróci przynajmniej jeden rekord --------->
        //TODO:<------ bo dodawany jest jeden rekord z dzisiejszą datą przy inicjalizacji tego testu --------->

        Assert.assertFalse(ret.isEmpty());
    }


    @Test
    public void When_ListedAll_Except_AtLeastOneContractReturned()
    {
        List<Contract> ret = null;

        ret = cr.findAll();

        Assert.assertFalse(ret.isEmpty());
    }

    @Test
    public void When_FindingByClientTokenTest1_Except_OneContractReturned()
    {
        Optional<Contract> ret = null;


        ret = cr.findOneByClientToken(saved.getClientToken());
        //TODO:<------ tu wstawic kod wybierania po tokenie klienta, token klienta wstawionego do bazy dla testu to Test1 --------->

        Assert.assertTrue(ret.isPresent());
    }


    @Test
    public void When_ListingByClientTokenTest1_Except_AtLeastOneContractReturned()
    {
        List<Contract> ret = null;
        ret = cr.findAllByClientToken(saved.getClientToken());

        //TODO:<------ tu wstawic kod szukania po tokenie klienta, token klienta wstawionego do bazy dla testu to Test1 --------->


        Assert.assertFalse(ret.isEmpty());
    }

    @Test
    public void When_FindingById_Except_OneContractReturned()
    {
        Optional<Contract> ret = null;

        ret = cr.findById(saved.getId());

        Assert.assertTrue(ret.isPresent());
    }

}