package seedu.address.logic.commands.reservation;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_RESERVATIONS;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.reservation.DisplayReservationListRequestEvent;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.Model;

/**
 * Sorts and lists all reservations in reservations list by date/time in ascending order.
 */
public class SortReservationsCommand extends Command {

    public static final String COMMAND_WORD = "sort-reservations";

    public static final String COMMAND_ALIAS = "sortr";

    public static final String MESSAGE_SORTED = "Reservations have been sorted by Date/Time";

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);
        model.sortReservations();
        model.updateFilteredReservationList(PREDICATE_SHOW_ALL_RESERVATIONS);
        model.commitAddressBook();
        EventsCenter.getInstance().post(new DisplayReservationListRequestEvent());
        return new CommandResult(String.format(MESSAGE_SORTED));
    }

}
