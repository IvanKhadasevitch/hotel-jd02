package pojos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;

import java.sql.Date;

import static javax.persistence.AccessType.PROPERTY;

@Entity
//@Table (name = "Persons", schema="test")
@Table(name = "`USER`")
@OptimisticLocking(type = OptimisticLockType.VERSION)
@org.hibernate.annotations.Cache(
        usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        region = "pojos.User",
        include = "non-lazy")

@Data
@NoArgsConstructor
public class User {
    private static final long serialVersionUID = 1L;

    @Id
    //for auto_increment id - use IDENTITY
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @Access(PROPERTY)
    //id	int(11) auto_increment primary key not null,
    private Long id;

    //`name`  varchar(20) not null,
    @Column(nullable = false, length = 20)
    @Access(PROPERTY)
    private String name;

    //surname	varchar(30) not null ,
    @Column(nullable = false, length = 30)
    @Access(value = AccessType.PROPERTY)
    private String surname;

    //don't save in DB as field of Table USER
    @Formula(value = "CONCAT_WS(' ',name,surname)")
    @Access(PROPERTY)
    private String fullName;

    //birthDate     date not null,
    @Column(nullable = false)
    @Access(PROPERTY)
    private Date birthDate;

    //email	 varchar(64) not null,
    @Column(unique = true, nullable = false, length = 64)
    @Access(PROPERTY)
    private String email;

    //`password` varchar(70) not null
    @Column(nullable = false,length = 70)
    @Access(PROPERTY)
    private String password;

    @Version
    @Access(PROPERTY)
    private Long version;

    public User(String name, String surname, Date birthDate, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.email = email;
        this.password = password;
    }
}
