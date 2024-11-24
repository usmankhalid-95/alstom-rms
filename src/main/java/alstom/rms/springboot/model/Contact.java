package alstom.rms.springboot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "contacts")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contact {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "contactPerson cannot be null")
    @Column(name = "contact_person", nullable = false)
    private String contactPerson;

    @NotNull(message = "contactPhone cannot be null")
    @Column(name = "contact_phone", unique = true, nullable = false)
    private String contactPhone;

    @NotNull(message = "contactEmail cannot be null")
    @Column(name = "contact_email", unique = true, nullable = false)
    private String contactEmail;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    @JsonBackReference
    private Station station;

}
