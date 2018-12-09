package db_utils.storage_type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageTypeRepository extends JpaRepository<StorageType, Long>
{
}
