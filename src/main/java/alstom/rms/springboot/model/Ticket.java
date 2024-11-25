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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "tickets",
        uniqueConstraints = @UniqueConstraint(columnNames = {"seat_number", "schedule_id", "departure_date"})
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ticket {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "ticket_number", unique = true, nullable = false)
    private String ticketNumber;

    @NotNull(message = "seatNumbers cannot be null")
    @ElementCollection
    @CollectionTable(name = "ticket_seats", joinColumns = @JoinColumn(name = "ticket_id"))
    @Column(name = "seat_number", nullable = false)
    private List<String> seatNumbers;

    @NotNull(message = "passenger cannot be null")
    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @NotNull(message = "schedule cannot be null")
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @NotNull(message = "departureDate cannot be null")
    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @PrePersist
    public void generateTicketNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomLetters = generateRandomLetters(3);
        String randomDigits = generateRandomDigits(3);
        this.ticketNumber = String.format("%s-%s-%s", datePart, randomLetters, randomDigits);
    }

    private String generateRandomLetters(int length) {
        Random random = new Random();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char letter = (char) ('A' + random.nextInt(26));
            result.append(letter);
        }
        return result.toString();
    }

    private String generateRandomDigits(int length) {
        Random random = new Random();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char digit = (char) ('0' + random.nextInt(10));
            result.append(digit);
        }
        return result.toString();
    }

}
