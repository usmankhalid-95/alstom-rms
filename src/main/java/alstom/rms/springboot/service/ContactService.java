package alstom.rms.springboot.service;

import alstom.rms.springboot.exception.CustomException;
import alstom.rms.springboot.model.Contact;
import alstom.rms.springboot.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private EntityValidationService entityValidationService;

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public Contact findContactById(UUID id) {
        return entityValidationService.fetchContactById(id);
    }

    public Contact createContact(Contact contact) {
        validateStation(contact);
        entityValidationService.checkContactUniqueness(contact, null);
        return contactRepository.save(contact);
    }

    public Contact updateContactById(UUID id, Contact contactDetails) {
        Contact updateContact = entityValidationService.fetchContactById(id);
        updateContactDetails(updateContact, contactDetails);
        return contactRepository.save(updateContact);
    }

    private void validateStation(Contact contact) {
        if (contact.getStation() == null || contact.getStation().getId() == null) {
            throw new CustomException("Station ID cannot be null.");
        }

        contact.setStation(entityValidationService.fetchStationById(contact.getStation().getId()));
    }

    private void updateContactDetails(Contact updateContact, Contact contactDetails) {
        if (contactDetails.getContactEmail() != null && !contactDetails.getContactEmail().equals(updateContact.getContactEmail())) {
            entityValidationService.checkContactEmailUniqueness(contactDetails.getContactEmail(), updateContact.getId());
            updateContact.setContactEmail(contactDetails.getContactEmail());
        }

        if (contactDetails.getContactPhone() != null && !contactDetails.getContactPhone().equals(updateContact.getContactPhone())) {
            entityValidationService.checkContactPhoneUniqueness(contactDetails.getContactPhone(), updateContact.getId());
            updateContact.setContactPhone(contactDetails.getContactPhone());
        }

        if (contactDetails.getContactPerson() != null) {
            updateContact.setContactPerson(contactDetails.getContactPerson());
        }

        if (contactDetails.getStation() != null && contactDetails.getStation().getId() != null) {
            updateContact.setStation(entityValidationService.fetchStationById(contactDetails.getStation().getId()));
        }
    }

    public void deleteContactById(UUID id) {
        Contact contact = entityValidationService.fetchContactById(id);
        contactRepository.delete(contact);
    }
}
