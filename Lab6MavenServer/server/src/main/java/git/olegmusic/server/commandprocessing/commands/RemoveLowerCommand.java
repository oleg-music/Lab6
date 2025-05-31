package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;
import git.olegmusic.server.commandprocessing.utils.PersonCreationServer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Команда remove_lower <Person>: удаляет из коллекции все элементы, меньшие чем заданный.
 */
public class RemoveLowerCommand implements Command {
    private Person reference;

    @Override
    public void setArgument(String argument) {
    }

    @Override
    public void setPerson(Person person) {
        this.reference = person;
    }

    @Override
    public String execute() {
        Person personObj;

        if (ExecuteScriptCommand.getRemainingScriptStrings() != null) {
            try {
                personObj = PersonCreationServer.createPersonFromScript();
            } catch (IllegalArgumentException e) {
                return e.getMessage();
            }
            if (personObj == null) {
                return "Ошибка: Не удалось создать объект Person из скрипта.";
            }
        } else {
            if (reference == null) {
                return "Ошибка: требуется объект Person для команды remove_lower.";
            }
            personObj = reference;
        }

        Set<Person> coll = CollectionManager.getPersonSet();
        int before = coll.size();

        List<Person> toRemove = coll.stream()
                .filter(p -> p.compareTo(personObj) < 0)
                .collect(Collectors.toList());

        coll.removeAll(toRemove);

        int removed = before - coll.size();
        return removed > 0
                ? "Удалено элементов, меньших заданного: " + removed
                : "Нет элементов, меньших заданного";
    }



    @Override
    public String descr() {
        return "remove_lower <object> : удалить из коллекции все элементы, меньшие чем заданный";
    }
}