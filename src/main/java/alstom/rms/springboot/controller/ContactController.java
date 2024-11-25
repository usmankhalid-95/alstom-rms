package alstom.rms.springboot.controller;

import alstom.rms.springboot.model.Contact;
import alstom.rms.springboot.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Contact> getAllContacts() {
        return contactService.getAllContacts();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable UUID id) {
        return ResponseEntity.ok(contactService.findContactById(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Contact createContact(@RequestBody @Valid Contact contact) {
        return contactService.createContact(contact);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<Contact> updateContactById(@PathVariable UUID id, @RequestBody Contact contactDetails) {
        return ResponseEntity.ok(contactService.updateContactById(id, contactDetails));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteContactById(@PathVariable UUID id) {
        contactService.deleteContactById(id);
        return ResponseEntity.ok("Contact successfully deleted.");
    }
}