package db_utils.packages;

import javax.persistence.*;

@Entity
@Table(name="PackageTypes")
public class Packages
{
    @Id
    @Column(name="ID", updatable=false, nullable=false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String typeName;

    public long getId()
    {
        return id;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }
}
