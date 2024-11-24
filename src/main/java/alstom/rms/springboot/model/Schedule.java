package alstom.rms.springboot.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "schedules",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "train_id",
                "origin_station_id",
                "destination_station_id"
        })
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Schedule {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "train cannot be null")
    @ManyToOne
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @NotNull(message = "originStation cannot be null")
    @ManyToOne
    @JoinColumn(name = "origin_station_id", nullable = false)
    private Station originStation;

    @NotNull(message = "destinationStation cannot be null")
    @ManyToOne
    @JoinColumn(name = "destination_station_id", nullable = false)
    private Station destinationStation;

    @NotNull(message = "departureTime cannot be null")
    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @NotNull(message = "arrivalTime cannot be null")
    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @NotNull(message = "ticketPrice cannot be null")
    @Column(name = "ticket_price", nullable = false)
    private Double ticketPrice;

    @NotNull(message = "availableSeats cannot be null")
    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

}
