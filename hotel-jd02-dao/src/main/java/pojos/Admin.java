package pojos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;

import static javax.persistence.AccessType.PROPERTY;

@Entity
@Table(name = "ADMIN")
@OptimisticLocking(type = OptimisticLockType.VERSION)
@org.hibernate.annotations.Cache(
//       usage = CacheConcurrencyStrategy.READ_ONLY,
        usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        include = "non-lazy")

@Data
@NoArgsConstructor
public class Admin {
    private static final long serialVersionUID = 1L;

    @Id
    //for auto_increment id - use IDENTITY
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Access(PROPERTY)
    // admin_id 			INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    private  Long id;

    //admin_name 			VARCHAR(30) NOT NULL,
    @Column(nullable = false, length = 30)
    @Access(PROPERTY)
    private String name;

    //admin_password		VARCHAR(70) NOT NULL,
    @Column(nullable = false, length = 70)
    @Access(PROPERTY)
    private String password;

    //admin_hotel_id		INT(11) NOT NULL,
    @ManyToOne
    @Access(PROPERTY)
    private  Hotel hotel;

    @Version
    @Access(PROPERTY)
    private Long version;

    public Admin(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
