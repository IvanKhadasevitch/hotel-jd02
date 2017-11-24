package pojos;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.AccessType.PROPERTY;

@Entity
//@Table (name = "Persons", schema="test")
@Table(name = "HOTEL")
@OptimisticLocking(type = OptimisticLockType.VERSION)
@org.hibernate.annotations.Cache(
//       usage = CacheConcurrencyStrategy.READ_ONLY,
        usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        region = "pojos.Hotel",
        include = "non-lazy")

@Setter @Getter
@NoArgsConstructor
public class Hotel {
    private static final long serialVersionUID = 1L;

    @Id
    //for auto_increment id - use IDENTITY
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Access(PROPERTY)
    // hotel_id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    private  Long id;

    //hotel_name VARCHAR(30) NOT NULL,
    @Column(unique = true, nullable = false, length = 30)
    @Access(PROPERTY)
    private String name;

    //hotel_email VARCHAR(64) NOT NULL
    @Column(nullable = false, length = 64)
    @Access(PROPERTY)
    private String email;

    @Version
    @Access(PROPERTY)
    private Long version;

    //don't save in DB as field of Table Hotel
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            mappedBy = "hotel")
    @BatchSize(size = 3)     //specify the size for batch loading the entries of a lazy collection.
    @Access(PROPERTY)
    private List<RoomType> roomTypeList = new ArrayList<>();

    //don't save in DB as field of Table Hotel
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            mappedBy = "hotel")
    @BatchSize(size = 3)     //specify the size for batch loading the entries of a lazy collection.
    @Access(PROPERTY)
    private List<Admin> adminList = new ArrayList<>();

    public Hotel(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void addRoomType(RoomType roomType) {
        roomTypeList.add( roomType );
        roomType.setHotel( this );
    }

    public void removeRoomType(RoomType roomType) {
        roomTypeList.remove( roomType );
        roomType.setHotel( null );
    }
    public void addAdmin(Admin admin) {
        adminList.add( admin );
        admin.setHotel( this );
    }

    public void removeAdmin(Admin admin) {
        adminList.remove( admin );
        admin.setHotel( null );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hotel)) return false;

        Hotel hotel = (Hotel) o;

        if (!getId().equals(hotel.getId())) return false;
        if (!getName().equals(hotel.getName())) return false;
        return getEmail().equals(hotel.getEmail());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getEmail().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
