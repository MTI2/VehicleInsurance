package db_utils.packages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackagesRepository extends JpaRepository<Packages, Long>
{
}
