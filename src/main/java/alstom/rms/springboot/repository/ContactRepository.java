package alstom.rms.springboot.repository;

import alstom.rms.springboot.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
    Optional<Contact> findByContactEmail(String email);

    Optional<Contact> findByContactPhone(String phone);
}
