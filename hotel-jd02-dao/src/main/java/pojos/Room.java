package pojos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;

import static javax.persistence.AccessType.PROPERTY;

@Entity
@Table(name = "ROOM")
@OptimisticLocking(type = OptimisticLockType.VERSION)
@org.hibernate.annotations.Cache(
        usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        region = "pojos.Room",
        include = "non-lazy")

@Data
@NoArgsConstructor
public class Room {
    private static final long serialVersionUID = 1L;

    @Id
    //for auto_increment id - use IDENTITY
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Access(PROPERTY)
    //room_id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    private  Long id;

    //room_number VARCHAR(10) NOT NULL,
    @Column(nullable = true, length = 10)
    @Access(PROPERTY)
    private String number;

    //room_roomType_id INT(11) NOT NULL,
    @ManyToOne
    @Access(PROPERTY)
    private  RoomType roomType;

    @Version
    @Access(PROPERTY)
    private Long version;

    public Room(String number) {
        this.number = number;
    }

    public static void main(String[] args) {
        Room room = new Room();

    }
}
