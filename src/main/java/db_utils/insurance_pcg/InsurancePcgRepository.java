package db_utils.insurance_pcg;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsurancePcgRepository extends JpaRepository<InsurancePcg, Long>
{
}
