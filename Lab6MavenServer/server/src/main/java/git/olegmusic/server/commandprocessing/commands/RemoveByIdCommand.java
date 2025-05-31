package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;

import java.util.Optional;
import java.util.Set;

/**
 * Команда remove_by_id <id> – удаляет элемент коллекции по его id.
 */
public class RemoveByIdCommand implements Command {
    private String argument;

    @Override
    public void setArgument(String argument) {
        this.argument = argument;
    }

    @Override
    public void setPerson(Person person) {
    }

    @Override
    public String execute() {
        if (argument == null) {
            return "Ошибка: не указан id для удаления!";
        }
        int id;
        try {
            id = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            return "Ошибка: id должен быть числом!";
        }

        Set<Person> coll = CollectionManager.getPersonSet();

        Optional<Person> personToRemove = coll.stream()
                .filter(p -> p.getId() == id)
                .findFirst();

        if (personToRemove.isPresent()) {
            coll.remove(personToRemove.get());
            return "Элемент с id " + id + " успешно удалён.";
        } else {
            return "Элемент с id " + id + " не найден.";
        }
    }

    @Override
    public String descr() {
        return "remove_by_id <id> : удалить элемент коллекции по id";
    }
}
