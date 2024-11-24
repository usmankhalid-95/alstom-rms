package alstom.rms.springboot.controller;

import alstom.rms.springboot.exception.ResourceNotFoundException;
import alstom.rms.springboot.model.Contact;
import alstom.rms.springboot.model.Station;
import alstom.rms.springboot.repository.ContactRepository;
import alstom.rms.springboot.repository.StationRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private StationRepository stationRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Contact createContact(@RequestBody @Valid Contact contact) {
        if (contact.getStation().getId() == null) {
            throw new ResourceNotFoundException("station id cannot be null");
        }

        UUID stationId = UUID.fromString(String.valueOf(contact.getStation().getId()));
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with ID: " + stationId));
        contact.setStation(station);

        return contactRepository.save(contact);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable UUID id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact doesn't exists with id:" + id));
        return ResponseEntity.ok(contact);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<Contact> updateContactById(@PathVariable UUID id, @RequestBody Contact contactDetails) {
        Contact updateContact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact doesn't exists with id:" + id));

        if (contactDetails.getContactEmail() != null) {
            updateContact.setContactEmail(contactDetails.getContactEmail());
        }

        if (contactDetails.getContactPerson() != null) {
            updateContact.setContactPerson(contactDetails.getContactPerson());
        }

        if (contactDetails.getContactPhone() != null) {
            updateContact.setContactPhone(contactDetails.getContactPhone());
        }
        if (contactDetails.getStation() != null && contactDetails.getStation().getId() != null) {
            UUID stationId = contactDetails.getStation().getId();
            Station station = stationRepository.findById(stationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Station not found with ID: " + stationId));

            updateContact.setStation(station);
        }

        contactRepository.save(updateContact);
        return ResponseEntity.ok(updateContact);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteContactById(@PathVariable UUID id) {
        Contact updateContact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact doesn't exists with id:" + id));

        contactRepository.delete(updateContact);
        return ResponseEntity.ok("contact data successfully deleted.");
    }

}
