package db_utils.insurance_pcg;


import db_utils.contract.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InsurancePcgRepository extends JpaRepository<InsurancePcg, Long>
{
    Optional<InsurancePcg> findByShortName(String shortName);

}
