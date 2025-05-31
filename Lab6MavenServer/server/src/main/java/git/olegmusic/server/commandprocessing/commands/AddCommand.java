package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.PersonCreationServer;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;

public class AddCommand implements Command {
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

        CollectionManager.add(personObj);
        int newId = Person.getNextId();
        personObj.setId(newId);
        return "Элемент успешно добавлен!";
    }

    @Override
    public String descr() {
        return "add {element} : добавить новый элемент в коллекцию";
    }
}
