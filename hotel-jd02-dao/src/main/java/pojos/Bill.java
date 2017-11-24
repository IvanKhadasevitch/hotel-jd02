package pojos;

import lombok.*;
import pojos.enums.BillStatusType;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.AccessType.PROPERTY;

@Entity
@Table(name = "BILL")
@OptimisticLocking(type = OptimisticLockType.VERSION)
@org.hibernate.annotations.Cache(
//       usage = CacheConcurrencyStrategy.READ_ONLY,
        usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        include = "non-lazy")

@Data
@EqualsAndHashCode(exclude = "order")
@NoArgsConstructor
public class Bill {
    @Id
    //for auto_increment id - use IDENTITY
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Access(PROPERTY)
    // id 					bigint not null auto_increment,
    private  long id;

    // createDate 			date,
    @Temporal(TemporalType.DATE)
    @CreationTimestamp
    @Column(insertable =true,  updatable = false)
    @Access(PROPERTY)
    private Date createDate;

    // `order_id` 			bigint,
//    @OneToOne(cascade = CascadeType.ALL)
    @OneToOne(cascade = CascadeType.REMOVE)
    @Access(PROPERTY)
    private  Order order;

    // `user_id` 			bigint,
    @OneToOne
    @Access(PROPERTY)
    private  User user;

    // room_id 			bigint,
    @OneToOne
    @Access(PROPERTY)
    private  Room room;

    //  arrivalDate			date,
    @Temporal(TemporalType.DATE)
    @Access(PROPERTY)
    private Date arrivalDate;

    // eventsDate 			date,
    @Temporal(TemporalType.DATE)
    @Access(PROPERTY)
    private Date eventsDate;

    //  total 				bigint,
    @Access(PROPERTY)
    private Long total;

    //  `status` 			varchar(12) not null,
    @Enumerated(EnumType.STRING)        //safe as varchar
    @Column(nullable = false, length = 12)
    @Access(PROPERTY)
    private BillStatusType status;

    //  version				bigint,
    @Version
    @Access(PROPERTY)
    private Long version;

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", createDate=" + createDate +
                ", order id=" + (order != null ? order.getId() : "null") +
                ", user=" + user +
                ", room=" + room +
                ", arrivalDate=" + arrivalDate +
                ", eventsDate=" + eventsDate +
                ", total=" + total +
                ", status=" + status +
                ", version=" + version +
                '}';
    }
}
