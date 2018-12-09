package db_utils.employee;


import javax.persistence.*;


@Entity
@Table(name = "Employees")
public class Employee
{
    @Id
    @Column(name="ID", updatable=false, nullable=false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column
    private String mail;

    @Column(length=50)
    private String login;

    @Column
    private long PhoneNumber;


    @Column(nullable = false)
    private String hash;

    @Column(nullable = false)
    private String salt;

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getMail()
    {
        return mail;
    }

    public void setMail(String mail)
    {
        this.mail = mail;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public long getPhoneNumber()
    {
        return PhoneNumber;
    }

    public void setPhoneNumber(long phoneNumber)
    {
        PhoneNumber = phoneNumber;
    }

    public String getHash()
    {
        return hash;
    }

    public void setHash(String hash)
    {
        this.hash = hash;
    }

    public String getSalt()
    {
        return salt;
    }

    public void setSalt(String salt)
    {
        this.salt = salt;
    }
}
