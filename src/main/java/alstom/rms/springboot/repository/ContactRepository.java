package alstom.rms.springboot.repository;

import alstom.rms.springboot.model.Contact;
import alstom.rms.springboot.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
    Contact findByContactEmail(String email);
}

