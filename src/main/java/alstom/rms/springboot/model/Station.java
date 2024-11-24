package alstom.rms.springboot.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stations")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Station {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )

    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "stationCode cannot be null")
    @Column(name = "station_code", unique = true, nullable = false)
    private String stationCode;

    @NotNull(message = "stationName cannot be null")
    @Column(name = "station_name", nullable = false)
    private String stationName;

    @NotNull(message = "location cannot be null")
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @OneToMany(mappedBy = "station", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<Contact> contacts;

}


