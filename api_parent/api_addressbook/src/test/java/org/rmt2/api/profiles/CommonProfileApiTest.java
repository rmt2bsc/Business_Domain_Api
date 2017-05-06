/**
 * 
 */
package org.rmt2.api.profiles;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.dao.contacts.ContactDaoException;
import org.dao.contacts.ContactsConst;
import org.dao.mapping.orm.rmt2.Address;
import org.dao.mapping.orm.rmt2.Person;
import org.dao.mapping.orm.rmt2.VwBusinessAddress;
import org.dao.mapping.orm.rmt2.VwCommonContact;
import org.dto.BusinessContactDto;
import org.dto.ContactDto;
import org.dto.PersonalContactDto;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiException;
import org.modules.contacts.ContactsApiFactory;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.BaseDaoTest;

import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;

/**
 * @author royterrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class })
public class CommonProfileApiTest extends BaseDaoTest {

    private List<VwCommonContact> mockSingleProfileFetchResponse;
    private List<VwCommonContact> mockCriteriaProfileFetchResponse;
    private List<VwCommonContact> mockAllProfileFetchResponse;
    private List<VwCommonContact> mockNotFoundFetchResponse;
    private Person mockPersonObject;
    private Address mockAddressObject;

    @Override
    public void setUp() throws Exception {
        APP_NAME = "addressbook";
        super.setUp();
        this.mockSingleProfileFetchResponse = this.createMockSingleContactFetchResponse();
        this.mockCriteriaProfileFetchResponse = this.createMockContactFetchUsingCriteriaResponse();
        this.mockAllProfileFetchResponse = this.createMockContactFetchAllResponse();
        this.mockNotFoundFetchResponse = this.createMockNotFoundSearchResultsResponse();
        this.mockPersonObject = this.createSingleMockOrmObject();
        this.mockAddressObject = this.createAddressMockObject();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    private List<VwCommonContact> createMockNotFoundSearchResultsResponse() {
        List<VwCommonContact> list = new ArrayList<VwCommonContact>();
        return list;
    }

    private List<VwCommonContact> createMockSingleContactFetchResponse() {
        List<VwCommonContact> list = new ArrayList<VwCommonContact>();
        VwCommonContact p = new VwCommonContact();
        p.setContactId(1351);
        p.setContactName("Dennis Chambers");
        p.setContactType(ContactsConst.CONTACT_TYPE_PERSONAL);
        p.setEmail("dennischambers@gmail.com");

        p.setAddrId(2222);
        p.setContactId(1351);
        p.setAddr1("94393 Hall Ave.");
        p.setAddr2("Building 9");
        p.setAddr3("Room 947");
        p.setZipCity("dallas");
        p.setZipState("TX");
        p.setAddrZip(75028);
        p.setAddrZipext(1234);

        list.add(p);
        return list;
    }

    /**
     * Use for the following selection criteria: where contact name begins with
     * 'D'
     * 
     * @return
     */
    private List<VwCommonContact> createMockContactFetchUsingCriteriaResponse() {
        List<VwCommonContact> list = new ArrayList<VwCommonContact>();
        VwCommonContact p = new VwCommonContact();
        p.setContactId(1351);
        p.setContactName("Dennis Chambers");
        p.setContactType(ContactsConst.CONTACT_TYPE_PERSONAL);
        p.setEmail("dennischambers@gmail.com");

        p.setAddrId(2222);
        p.setContactId(1351);
        p.setAddr1("94393 Hall Ave.");
        p.setZipCity("dallas");
        p.setZipState("TX");
        p.setAddrZip(75028);
        p.setAddrZipext(1234);
        list.add(p);

        p = new VwCommonContact();
        p.setContactId(2045);
        p.setContactName("DQ Conenient Store");
        p.setContactType(ContactsConst.CONTACT_TYPE_BUSINESS);
        p.setEmail("dqconvenientstore@gmail.com");

        p.setAddrId(3333);
        p.setContactId(2045);
        p.setAddr1("6473 Lemmon Ave");
        p.setAddr2("Building 847");
        p.setAddr3("Room 947");
        p.setZipCity("dallas");
        p.setZipState("TX");
        p.setAddrZip(75240);
        p.setAddrZipext(9876);
        list.add(p);

        return list;
    }

    private List<VwCommonContact> createMockContactFetchAllResponse() {
        List<VwCommonContact> list = new ArrayList<VwCommonContact>();
        VwCommonContact p = new VwCommonContact();
        p.setContactId(1351);
        p.setContactName("Dennis Chambers");
        p.setContactType(ContactsConst.CONTACT_TYPE_PERSONAL);
        p.setEmail("dennischambers@gmail.com");

        p.setAddrId(2222);
        p.setContactId(1351);
        p.setAddr1("94393 Hall Ave.");
        p.setZipCity("dallas");
        p.setZipState("TX");
        p.setAddrZip(75028);
        p.setAddrZipext(1234);
        list.add(p);

        p = new VwCommonContact();
        p.setContactId(2045);
        p.setContactName("DQ Conenient Store");
        p.setContactType(ContactsConst.CONTACT_TYPE_BUSINESS);
        p.setEmail("dqconvenientstore@gmail.com");

        p.setAddrId(3333);
        p.setContactId(2045);
        p.setAddr1("6473 Lemmon Ave");
        p.setAddr2("Building 847");
        p.setAddr3("Room 947");
        p.setZipCity("dallas");
        p.setZipState("TX");
        p.setAddrZip(75240);
        p.setAddrZipext(9876);
        list.add(p);

        p = new VwCommonContact();
        p.setContactId(3458);
        p.setContactName("Billy Cobham");
        p.setContactType(ContactsConst.CONTACT_TYPE_PERSONAL);
        p.setEmail("billycobham@gmail.com");

        p.setAddrId(4444);
        p.setContactId(3458);
        p.setAddr1("3333 Wingstop Ave");
        p.setZipCity("dallas");
        p.setZipState("TX");
        p.setAddrZip(75232);
        p.setAddrZipext(3333);
        list.add(p);

        return list;
    }

    private PersonalContactDto createMockContactDto(int contactId, int addressId) {
        Person p = this.createSingleMockOrmObject();
        Address a = this.createAddressMockObject();
        p.setPersonId(contactId);
        a.setAddrId(addressId);
        PersonalContactDto perDto = Rmt2AddressBookDtoFactory.getPersonInstance(p, a);
        return perDto;
    }

    private Person createSingleMockOrmObject() {
        Person p = new Person();
        p.setPersonId(1351);
        p.setFirstname("Harvey");
        p.setLastname("Mason");
        p.setMidname("Raye");
        p.setTitle(100);
        p.setEmail("harveymason@gte.net");
        p.setMaritalStatusId(20);
        p.setGenderId(12);
        p.setSsn("675-88-7563");
        p.setRaceId(30);
        return p;
    }

    private Person createCriteriaMockObject(int contactId) {
        Person criteria = new Person();
        criteria.addCriteria(Person.PROP_PERSONID, contactId);
        return criteria;
    }

    private Address createAddressMockObject() {
        Address a = new Address();
        a.setAddrId(2222);
        a.setBusinessId(1351);
        a.setAddr1("94393 Hall Ave.");
        a.setAddr2("Suite 948");
        a.setAddr3("P.O. Box 84763");
        a.setZip(75028);
        a.setZipext(1234);
        return a;
    }

    private Address createAddressCriteriaMockObject(int addressId) {
        Address criteria = new Address();
        criteria.addCriteria(Address.PROP_ADDRID, addressId);
        return criteria;
    }

    @Test
    public void testAllContactFetch() {
        PersonalContactDto busDto = Rmt2AddressBookDtoFactory.getNewPersonInstance();
        try {
            when(this.mockPersistenceClient.retrieveList(any(VwBusinessAddress.class)))
                    .thenReturn(this.mockAllProfileFetchResponse);
        } catch (ContactDaoException e) {
            e.printStackTrace();
            Assert.fail("All personal contact fetch test case failed");
        }

        ContactsApiFactory f = new ContactsApiFactory();
        ContactsApi api = f.createApi(APP_NAME);
        List<ContactDto> results = null;
        try {
            results = api.getContact(busDto);
        } catch (ContactsApiException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(results);
        Assert.assertEquals(3, results.size());

        for (ContactDto contact : results) {
            Assert.assertNotNull(contact);
            Assert.assertNotNull(contact.getContactType());
            Assert.assertEquals(ContactsConst.CONTACT_TYPE_PERSONAL, contact.getContactType());
            Assert.assertTrue(contact.getContactId() > 0);
            Assert.assertNotNull(contact.getContactName());

            Assert.assertTrue(contact instanceof PersonalContactDto);
            PersonalContactDto perContact = (PersonalContactDto) contact;
            Assert.assertNotNull(perContact.getFirstname());
            Assert.assertNotNull(perContact.getLastname());
            Assert.assertNotNull(perContact.getContactEmail());
        }
    }

    @Test
    public void testSingleContactFetch() {
        Person per = new Person();
        per.setPersonId(1351);
        PersonalContactDto busDto = Rmt2AddressBookDtoFactory.getPersonInstance(per, null);
        try {
            when(this.mockPersistenceClient.retrieveList(any(VwCommonContact.class)))
                    .thenReturn(this.mockSingleProfileFetchResponse);
        } catch (ContactDaoException e) {
            e.printStackTrace();
            Assert.fail("Single person contact fetch test case failed");
        }

        ContactsApiFactory f = new ContactsApiFactory();
        ContactsApi api = f.createApi(APP_NAME);
        List<ContactDto> results = null;
        try {
            results = api.getContact(busDto);
        } catch (ContactsApiException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        ContactDto contact = results.get(0);
        Assert.assertNotNull(contact.getContactType());
        Assert.assertEquals(ContactsConst.CONTACT_TYPE_PERSONAL, contact.getContactType());
        Assert.assertEquals(1351, contact.getContactId());
        Assert.assertNotNull(contact.getContactName());
        Assert.assertEquals("Dennis Chambers", contact.getContactName());

        Assert.assertTrue(contact instanceof PersonalContactDto);
        PersonalContactDto perContact = (PersonalContactDto) contact;
        Assert.assertNotNull(perContact.getFirstname());
        Assert.assertNotNull(perContact.getLastname());
        Assert.assertNotNull(perContact.getContactEmail());

    }

    @Test
    public void testContactCriteriaFetch() {
        Person per = new Person();
        per.setLastname("M");
        PersonalContactDto busDto = Rmt2AddressBookDtoFactory.getPersonInstance(per, null);
        try {
            when(this.mockPersistenceClient.retrieveList(any(VwBusinessAddress.class)))
                    .thenReturn(this.mockCriteriaProfileFetchResponse);
        } catch (ContactDaoException e) {
            e.printStackTrace();
            Assert.fail("person contact fetch using specific selection criteria test case failed");
        }

        ContactsApiFactory f = new ContactsApiFactory();
        ContactsApi api = f.createApi(APP_NAME);
        List<ContactDto> results = null;
        try {
            results = api.getContact(busDto);
        } catch (ContactsApiException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        for (ContactDto contact : results) {
            Assert.assertNotNull(contact);
            Assert.assertNotNull(contact.getContactType());
            Assert.assertEquals(ContactsConst.CONTACT_TYPE_PERSONAL, contact.getContactType());
            Assert.assertTrue(contact.getContactId() > 0);
            Assert.assertNotNull(contact.getContactName());
            String name[] = contact.getContactName().split(" ");
            Assert.assertEquals(2, name.length);
            Assert.assertEquals("C", name[1].substring(0, 1));

            Assert.assertTrue(contact instanceof PersonalContactDto);
            PersonalContactDto busContact = (PersonalContactDto) contact;
            Assert.assertNotNull(busContact.getFirstname());
            Assert.assertNotNull(busContact.getLastname());
            Assert.assertNotNull(busContact.getContactEmail());
        }
    }

    @Test
    public void testNotFoundlContactFetch() {
        PersonalContactDto busDto = Rmt2AddressBookDtoFactory.getNewPersonInstance();
        try {
            when(this.mockPersistenceClient.retrieveList(any(VwBusinessAddress.class)))
                    .thenReturn(this.mockNotFoundFetchResponse);
        } catch (ContactDaoException e) {
            e.printStackTrace();
            Assert.fail("Not Found: person contact fetch test case failed");
        }

        ContactsApiFactory f = new ContactsApiFactory();
        ContactsApi api = f.createApi(APP_NAME);
        List<ContactDto> results = null;
        try {
            results = api.getContact(busDto);
        } catch (ContactsApiException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(results);
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void testNullCriteriaContactFetch() {
        ContactsApiFactory f = new ContactsApiFactory();
        ContactsApi api = f.createApi(APP_NAME);
        try {
            api.getContact(null);
            Assert.fail("Expected test case to throw an exception");
        } catch (Exception e) {
            String expectedMsg = "Error retrieving list of contacts using DTO as criteria";
            Assert.assertTrue(e instanceof ContactDaoException);
            Assert.assertEquals(expectedMsg, e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testContactUpdate() {
        PersonalContactDto mockUpdatePersonDto = this.createMockContactDto(1351, 2222);
        try {
            when(this.mockPersistenceClient.retrieveObject(any(Object.class))).thenReturn(this.mockPersonObject,
                    this.mockAddressObject);
        } catch (ContactDaoException e) {
            e.printStackTrace();
            Assert.fail("Business contact update test case failed setting up business and address object calls");
        }
        try {
            when(this.mockPersistenceClient.updateRow(any(Object.class))).thenReturn(1);
        } catch (ContactDaoException e) {
            e.printStackTrace();
            Assert.fail("Business contact update test case failed setting up update call");
        }
        ContactsApiFactory f = new ContactsApiFactory();
        ContactsApi api = f.createApi(APP_NAME);
        int rc = 0;
        try {
            rc = api.updateContact(mockUpdatePersonDto);
        } catch (Exception e) {
            Assert.fail("Business contact update test case failed");
            e.printStackTrace();
        }
        Assert.assertEquals(1, rc);
    }

    @Test
    public void testContactInsert() {
        PersonalContactDto mockUpdatePersonDto = this.createMockContactDto(0, 0);
        try {
            when(this.mockPersistenceClient.insertRow(any(PersonalContactDto.class), any(Boolean.class)))
                    .thenReturn(1351);
        } catch (ContactDaoException e) {
            e.printStackTrace();
            Assert.fail("Person contact insert test case failed setting up update call");
        }
        ContactsApiFactory f = new ContactsApiFactory();
        ContactsApi api = f.createApi(APP_NAME);
        int rc = 0;
        try {
            rc = api.updateContact(mockUpdatePersonDto);
        } catch (Exception e) {
            Assert.fail("Person contact insert test case failed");
            e.printStackTrace();
        }
        Assert.assertEquals(1351, rc);
        Assert.assertEquals(1351, mockUpdatePersonDto.getContactId());
        Assert.assertEquals(1351, mockUpdatePersonDto.getAddrId());

    }

    @Test
    public void testContactDelete() {
        PersonalContactDto mockUpdatePersonDto = this.createMockContactDto(1351, 2222);
        try {
            when(this.mockPersistenceClient.deleteRow(any(BusinessContactDto.class))).thenReturn(1);
        } catch (ContactDaoException e) {
            e.printStackTrace();
            Assert.fail("Person contact delete test case failed setting up update call");
        }
        ContactsApiFactory f = new ContactsApiFactory();
        ContactsApi api = f.createApi(APP_NAME);
        int rc = 0;
        try {
            rc = api.deleteContact(mockUpdatePersonDto);
        } catch (Exception e) {
            Assert.fail("Person contact delete test case failed");
            e.printStackTrace();
        }
        Assert.assertEquals(2, rc);
    }
}