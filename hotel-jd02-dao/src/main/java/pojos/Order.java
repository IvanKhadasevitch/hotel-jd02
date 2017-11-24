package pojos;

import lombok.*;
import pojos.enums.OrderStatusType;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

import static javax.persistence.AccessType.PROPERTY;

@Entity
@Table(name = "`ORDER`")
@OptimisticLocking(type = OptimisticLockType.VERSION)
@org.hibernate.annotations.Cache(
//       usage = CacheConcurrencyStrategy.READ_ONLY,
        usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE,
        include = "non-lazy")

@Data
@EqualsAndHashCode(exclude = "bill")
@NoArgsConstructor
public class Order {
    @Id
    //for auto_increment id - use IDENTITY
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Access(PROPERTY)
    // id 				bigint not null auto_increment,
    private  Long id;


    // createDate 			date,
    @Temporal(TemporalType.DATE)
    @CreationTimestamp
    @Column(insertable =true,  updatable = false)
    @Access(PROPERTY)
    private Date createDate;

    // updateDate 			date,
    @Temporal(TemporalType.DATE)
    @UpdateTimestamp
    @Column(insertable =false, updatable = true)
    @Access(PROPERTY)
    private Date updateDate;

    // admin_id 		bigint,
    @OneToOne
    @Access(PROPERTY)
    private  Admin admin;

    // `status` 		varchar(12) not null,
    @Enumerated(EnumType.STRING)        //safe as varchar
    @Column(nullable = false, length = 12)
    @Access(PROPERTY)
    private OrderStatusType status;

    // `user_id` 		bigint,
    @OneToOne
    @Access(PROPERTY)
    private  User user;

//    private  Long roomTypeId;

    // room_id 		bigint,
    @OneToOne
    @Access(PROPERTY)
    private  Room room;

    // arrivalDate			date,
//    @Temporal(TemporalType.DATE)
    @Access(PROPERTY)
    private java.sql.Date arrivalDate;

    // eventsDate 			date,
//    @Temporal(TemporalType.DATE)
    @Access(PROPERTY)
    private java.sql.Date eventsDate;

    // total 			bigint,
    @Access(PROPERTY)
    private Long total;

    //  bill_id 		bigint,
//    @OneToOne(cascade = CascadeType.ALL)
    @OneToOne(cascade = CascadeType.REMOVE)
    @Access(PROPERTY)
    private  Bill bill;

    // version			bigint,
    @Version
    @Access(PROPERTY)
    private Long version;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", admin=" + admin +
                ", status=" + status +
                ", user=" + user +
                ", room=" + room +
                ", arrivalDate=" + arrivalDate +
                ", eventsDate=" + eventsDate +
                ", total=" + total +
                ", bill id=" + (bill != null ? String.valueOf(bill.getId())  : "null") +
                ", version=" + version +
                '}';
    }
}
