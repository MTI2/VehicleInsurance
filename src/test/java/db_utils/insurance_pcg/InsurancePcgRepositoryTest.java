package db_utils.insurance_pcg;

import db_utils.contract.Contract;
import db_utils.packages.PackagesRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import vehicleinsurance.VehicleInsuranceApplication;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {VehicleInsuranceApplication.class})
public class InsurancePcgRepositoryTest
{
    private InsurancePcg saved;

    @Autowired
    InsurancePcgRepository ir;
    @Autowired
    PackagesRepository pr;

    @Before
    public void init()
    {
        InsurancePcg p = new InsurancePcg();
        p.setDescription("Test1");
        p.setDiscount(0);
        p.setFullName("Test1");
        p.setPcg(pr.findAll().get(0));
        p.setShortName("Test1");

        saved = ir.save(p);
    }

    @After
    public void cleanUp()
    {
        ir.delete(saved);
    }


    @Test
    public void When_ListedAllAvaliablePackages_Except_AtLeastOneReturned()
    {
        Assert.assertFalse(ir.findAll().isEmpty());
    }

    @Test
    public void When_SelectedByShortNameTest1_Exept_NotNullReturned()
    {
        Optional<InsurancePcg> ret = null;
        ret = ir.findByShortName("Test1");

        //to by sprawdzilo czy optional jest nullem
        //Assert.assertNotNull(ir.findByShortName("Test1"));


        //TODO:<-- Wstawić kod wybierania pakietu po krótkiej nazwie Test1 -->
        //to sprawdza czy w optionalu cos jest
        Assert.assertTrue(ret.isPresent());
    }

}