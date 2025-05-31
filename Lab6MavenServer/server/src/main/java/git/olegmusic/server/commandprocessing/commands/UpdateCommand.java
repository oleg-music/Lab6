package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;
import git.olegmusic.server.commandprocessing.utils.PersonCreationServer;

import java.util.Optional;
import java.util.Set;

/**
 * Команда 'update' — обновляет элемент коллекции по id.
 */
public class UpdateCommand implements Command {

    private String argument;
    private Person person;

    @Override
    public void setArgument(String argument) {
        this.argument = argument;
    }

    @Override
    public void setPerson(Person person) {
        this.person = person;
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
            if (person == null) {
                return "Ошибка: объект Person не передан.";
            }
            personObj = person;
        }

        if (argument == null) {
            return "Ошибка: не указан id для обновления!";
        }

        int id;
        try {
            id = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            return "Ошибка: id должен быть числом!";
        }

        Set<Person> coll = CollectionManager.getPersonSet();

        Optional<Person> toUpdate = coll.stream()
                .filter(p -> p.getId() == id)
                .findFirst();

        if (toUpdate.isPresent()) {
            coll.remove(toUpdate.get());
            personObj.setId(id);
            coll.add(personObj);
            return "Элемент с id " + id + " успешно обновлён!";
        } else {
            return "Элемент с id " + id + " не найден.";
        }
    }



    @Override
    public String descr() {
        return "update <id> : обновить элемент коллекции по id";
    }
}
