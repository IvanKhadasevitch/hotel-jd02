package pojos;

import pojos.enums.CurrencyType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.AccessType.PROPERTY;

@Entity
@Table(name = "ROOM_TYPE")
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Cache(
       usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
//       usage = CacheConcurrencyStrategy.READ_WRITE,
       region = "pojos.RoomType",
       include = "non-lazy")

@Getter @Setter
@NoArgsConstructor
public class RoomType {
    private static final long serialVersionUID = 1L;

    @Id
    //for auto_increment id - use IDENTITY
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Access(PROPERTY)
    // roomType_id	INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    private Long id;

    //roomType_name  	VARCHAR(30) NOT NULL,
    @Column(nullable = false, length = 30)
    @Access(PROPERTY)
    private String name;

    //roomType_seats 	INT NOT NULL,
    @Column(nullable = false)
    @Access(PROPERTY)
    private Integer seats;

    //roomType_price 		INT NOT NULL,
    @Column(nullable = false)
    @Access(PROPERTY)
    private Integer price;

    //roomType_currency 	VARCHAR(12) NOT NULL,
    @Enumerated(EnumType.STRING)        //safe as varchar
    @Column(nullable = false, length = 12)
    @Access(PROPERTY)
    private CurrencyType currency;

    //roomType_hotel_id 	INT(11) NOT NULL,
    @ManyToOne
    @Access(PROPERTY)
    private Hotel hotel;

    @Version
    @Access(PROPERTY)
    private Long version;

    //don't save in DB as field of Table ROOM_TYPE
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            mappedBy = "roomType")
    @BatchSize(size = 3)     //specify the size for batch loading the entries of a lazy collection.
    @Access(PROPERTY)
    private List<Room> roomList = new ArrayList<>();

    public RoomType(String name, Integer seats, Integer price, CurrencyType currency) {
        this.name = name;
        this.seats = seats;
        this.price = price;
        this.currency = currency;
    }

    public void addRoom(Room room) {
        roomList.add( room );
        room.setRoomType( this );
    }

    public void removeRoom(Room room) {
        roomList.remove( room );
        room.setRoomType( null );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomType)) return false;

        RoomType roomType = (RoomType) o;

        if (!getId().equals(roomType.getId())) return false;
        if (!getName().equals(roomType.getName())) return false;
        if (!getSeats().equals(roomType.getSeats())) return false;
        if (!getPrice().equals(roomType.getPrice())) return false;
        if (getCurrency() != roomType.getCurrency()) return false;
        return getHotel().equals(roomType.getHotel());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getSeats().hashCode();
        result = 31 * result + getPrice().hashCode();
        result = 31 * result + getCurrency().hashCode();
        result = 31 * result + getHotel().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RoomType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", seats=" + seats +
                ", price=" + price +
                ", currency=" + currency +
                ", hotel=" + hotel +
                '}';
    }
}
