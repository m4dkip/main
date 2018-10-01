package seedu.address.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.XmlUtil;
import seedu.address.model.AddressBook;
import seedu.address.testutil.TypicalAddressBook;

public class XmlSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "XmlSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.xml");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.xml");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.xml");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private XmlSerializableAddressBook dataFromFile = null;

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        dataFromFile = XmlUtil.getDataFromFile(TYPICAL_PERSONS_FILE, XmlSerializableAddressBook.class);
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalAddressBook.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        dataFromFile = XmlUtil.getDataFromFile(INVALID_PERSON_FILE, XmlSerializableAddressBook.class);
        thrown.expect(IllegalValueException.class);
        dataFromFile.toModelType();
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        dataFromFile = XmlUtil.getDataFromFile(DUPLICATE_PERSON_FILE, XmlSerializableAddressBook.class);
        thrown.expect(IllegalValueException.class);
        thrown.expectMessage(XmlSerializableAddressBook.MESSAGE_DUPLICATE_PERSON);
        dataFromFile.toModelType();
    }

    @Test
    public void equals() throws Exception {
        dataFromFile = XmlUtil.getDataFromFile(DUPLICATE_PERSON_FILE, XmlSerializableAddressBook.class);

        // same object
        assertTrue(dataFromFile.equals(dataFromFile));

        // different type
        assertFalse(dataFromFile.equals(null));
        assertFalse(dataFromFile.equals(0));
    }
}
