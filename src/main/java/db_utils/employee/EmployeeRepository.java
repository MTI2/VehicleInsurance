package db_utils.employee;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>
{
     boolean existsByLoginAndHash(String login, String hash);
     boolean existsByLogin(String login);

     Employee getByName(String name);
}
