package db_utils.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>
{
    Optional<Contract> findOneByClientToken(String tokenId);
    List<Contract> findAllByClientToken(String tokenId);

    List<Contract> findAllByDateBetween(Date first, Date second);
}
