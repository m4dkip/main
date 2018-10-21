package seedu.address.logic.commands.menu;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.menu.DiscountItemCommand.createDiscountedItem;
import static seedu.address.logic.commands.menu.MenuCommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.menu.MenuCommandTestUtil.showItemAtIndex;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_ITEMS;
import static seedu.address.testutil.TypicalAddressBook.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.menu.Item;

/**
 * Contains integration tests (interaction with the Model, UndoCommand and RedoCommand) and unit tests for {@code
 * DeleteCommand}.
 */
public class DiscountItemCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Item itemToDiscount = model.getFilteredItemList().get(INDEX_FIRST.getZeroBased());
        DiscountItemCommand discountItemCommand = new DiscountItemCommand(INDEX_FIRST, 20);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        Item discountedItem = createDiscountedItem(itemToDiscount, 20);

        String expectedMessage = String.format(DiscountItemCommand.MESSAGE_DISCOUNT_ITEM_SUCCESS, discountedItem);

        expectedModel.updateItem(itemToDiscount, discountedItem);
        expectedModel.updateFilteredItemList(PREDICATE_SHOW_ALL_ITEMS);
        expectedModel.commitAddressBook();

        assertCommandSuccess(discountItemCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredItemList().size() + 1);
        DiscountItemCommand discountItemCommand = new DiscountItemCommand(outOfBoundIndex, 0);

        assertCommandFailure(discountItemCommand, model, commandHistory, Messages.MESSAGE_INVALID_ITEM_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showItemAtIndex(model, INDEX_FIRST);

        Item itemToDiscount = model.getFilteredItemList().get(INDEX_FIRST.getZeroBased());
        DiscountItemCommand discountItemCommand = new DiscountItemCommand(INDEX_FIRST, 75);

        Item discountedItem = createDiscountedItem(itemToDiscount, 75);

        String expectedMessage = String.format(DiscountItemCommand.MESSAGE_DISCOUNT_ITEM_SUCCESS, discountedItem);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateItem(itemToDiscount, discountedItem);
        expectedModel.updateFilteredItemList(PREDICATE_SHOW_ALL_ITEMS);
        expectedModel.commitAddressBook();

        assertCommandSuccess(discountItemCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showItemAtIndex(model, INDEX_FIRST);

        Index outOfBoundIndex = INDEX_SECOND;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getItemList().size());

        DiscountItemCommand discountItemCommand = new DiscountItemCommand(outOfBoundIndex, 0);

        assertCommandFailure(discountItemCommand, model, commandHistory, Messages.MESSAGE_INVALID_ITEM_DISPLAYED_INDEX);
    }

    @Test
    public void executeUndoRedo_validIndexUnfilteredList_success() throws Exception {
        Item itemToDiscount = model.getFilteredItemList().get(INDEX_FIRST.getZeroBased());
        DiscountItemCommand discountItemCommand = new DiscountItemCommand(INDEX_FIRST, 20);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        Item discountedItem = createDiscountedItem(itemToDiscount, 20);

        expectedModel.updateItem(itemToDiscount, discountedItem);
        expectedModel.updateFilteredItemList(PREDICATE_SHOW_ALL_ITEMS);
        expectedModel.commitAddressBook();

        // delete -> first item deleted
        discountItemCommand.execute(model, commandHistory);

        // undo -> reverts addressbook back to previous state and filtered item list to show all items
        expectedModel.undoAddressBook();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> same first item deleted again
        expectedModel.redoAddressBook();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void executeUndoRedo_invalidIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredItemList().size() + 1);
        DiscountItemCommand discountItemCommand = new DiscountItemCommand(outOfBoundIndex, 0);

        // execution failed -> address book state not added into model
        assertCommandFailure(discountItemCommand, model, commandHistory, Messages.MESSAGE_INVALID_ITEM_DISPLAYED_INDEX);

        // single address book state in model -> undoCommand and redoCommand fail
        assertCommandFailure(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_FAILURE);
        assertCommandFailure(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_FAILURE);
    }

    /**
     * 1. Discounts a {@code Item} from a filtered list. 2. Undo the discount. 3. The unfiltered list should be shown
     * now. Verify that the index of the previously discounted item in the unfiltered list is different from the
     * index at the filtered list. 4. Redo the discount. This ensures {@code RedoCommand} discounts the item object
     * regardless of indexing.
     */
    @Test
    public void executeUndoRedo_validIndexFilteredList_sameItemDiscounted() throws Exception {
        DiscountItemCommand discountItemCommand = new DiscountItemCommand(INDEX_FIRST, 65);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        showItemAtIndex(model, INDEX_SECOND);
        Item itemToDiscount = model.getFilteredItemList().get(INDEX_FIRST.getZeroBased());
        Item discountedItem = createDiscountedItem(itemToDiscount, 65);

        expectedModel.updateItem(itemToDiscount, discountedItem);
        expectedModel.updateFilteredItemList(PREDICATE_SHOW_ALL_ITEMS);
        expectedModel.commitAddressBook();

        // delete -> deletes second item in unfiltered item list / first item in filtered item list
        discountItemCommand.execute(model, commandHistory);

        // undo -> reverts addressbook back to previous state and filtered item list to show all items
        expectedModel.undoAddressBook();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        assertNotEquals(itemToDiscount, model.getFilteredItemList().get(INDEX_FIRST.getZeroBased()));
        // redo -> deletes same second item in unfiltered item list
        expectedModel.redoAddressBook();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() {
        DiscountItemCommand discountFirstCommand = new DiscountItemCommand(INDEX_FIRST, 1);
        DiscountItemCommand discountSecondCommand = new DiscountItemCommand(INDEX_SECOND, 2);

        // same object -> returns true
        assertTrue(discountFirstCommand.equals(discountFirstCommand));

        // same values -> returns true
        DiscountItemCommand discountFirstCommandCopy = new DiscountItemCommand(INDEX_FIRST, 1);
        assertTrue(discountFirstCommand.equals(discountFirstCommandCopy));

        // different types -> returns false
        assertFalse(discountFirstCommand.equals(1));

        // null -> returns false
        assertFalse(discountFirstCommand.equals(null));

        // different item -> returns false
        assertFalse(discountFirstCommand.equals(discountSecondCommand));
    }
}
