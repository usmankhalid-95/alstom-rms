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
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "passengers")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Passenger {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "passenger_name")
    private String passengerName;

    @NotNull(message = "contactPhone cannot be null")
    @Column(name = "contact_phone", unique = true)
    private String contactPhone;

    @NotNull(message = "contactEmail cannot be null")
    @Column(name = "contact_email", unique = true)
    private String contactEmail;

}
