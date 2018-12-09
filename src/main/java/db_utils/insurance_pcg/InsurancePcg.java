package db_utils.insurance_pcg;

import db_utils.packages.Packages;

import javax.persistence.*;

@Entity
@Table
public class InsurancePcg
{
    @Id
    @Column(name="ID", updatable=false, nullable=false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String shortName;

    @Column(nullable = false)
    private String fullName;

    @Column
    private String description;

    @Column
    private double discount;


    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "id", insertable = false, updatable = false)
    private Packages pcg;

    public long getId()
    {
        return id;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public double getDiscount()
    {
        return discount;
    }

    public void setDiscount(double discount)
    {
        this.discount = discount;
    }

    public Packages getPcg()
    {
        return pcg;
    }

    public void setPcg(Packages pcg)
    {
        this.pcg = pcg;
    }
}
