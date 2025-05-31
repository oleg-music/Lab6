package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;
import git.olegmusic.server.commandprocessing.xml.DOMWriter;

import java.util.Set;

public class SaveCommand {
    public static void execute() {
        try {
            Set<Person> persons = CollectionManager.getPersonSet();
            DOMWriter.writeCollectionInXML(persons);
            System.out.println("Коллекция успешно сохранена!");
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении коллекции: " + e.getMessage());
        }
    }
}
