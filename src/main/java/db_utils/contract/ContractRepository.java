package db_utils.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>
{
    Optional<Contract> findOneByClientToken(String tokenId);

    List<Contract> findAllByClientToken(String tokenId);
    List<Contract> findAllByDateBetween(Date first, Date second);

    List<Contract> findByDateAfter(Date after);
    List<Contract> findByDateBefore(Date before);

    List<Contract> findAllByClientTokenAndDateBefore(String tokenId, Date before);
    List<Contract> findAllByClientTokenAndDateAfter(String tokenId, Date after);

    List<Contract> findAllByClientTokenAndDateBetween(String tokenId, Date first, Date second);


    Optional<Contract> findOneByRequestToken(String reqestToken);

    @Transactional
    int deleteContractByRequestToken(String requestToken);


}
