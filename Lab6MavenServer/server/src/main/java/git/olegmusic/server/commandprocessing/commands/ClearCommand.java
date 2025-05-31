package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.server.commandprocessing.utils.CollectionManager;
import git.olegmusic.common.Person;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Команда 'clear' — очищает коллекцию.
 */
public class ClearCommand implements Command {

    @Override
    public void setArgument(String argument) {
    }

    @Override
    public void setPerson(Person person) {
    }

    @Override
    public String execute() {
        Set<Person> coll = CollectionManager.getPersonSet();
        coll.removeAll(coll.stream().collect(Collectors.toList()));
        return "Коллекция успешно очищена.";
    }

    @Override
    public String descr() {
        return "clear : очистить коллекцию";
    }
}

