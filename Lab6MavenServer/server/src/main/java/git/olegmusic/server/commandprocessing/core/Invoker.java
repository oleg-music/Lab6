package git.olegmusic.server.commandprocessing.core;

import git.olegmusic.common.CommandRequest;
import git.olegmusic.common.CommandResponse;
import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.commands.*;
import git.olegmusic.server.commandprocessing.utils.HistoryManager;
import git.olegmusic.server.commandprocessing.xml.DOMReader;
import git.olegmusic.server.commandprocessing.xml.DOMWriter;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Invoker {
    private static final String DATA_FILE = "server/src/main/resources/startFile.txt";

    private static final HashMap<String, Command> commands = new HashMap<>();
     {
        commands.put("help", new HelpCommand(commands));
        commands.put("info", new InfoCommand());
        commands.put("show", new ShowCommand());
        commands.put("add", new AddCommand());
        commands.put("update", new UpdateCommand());
        commands.put("remove_by_id", new RemoveByIdCommand());
        commands.put("clear", new ClearCommand());
        commands.put("count_greater_than_birthday", new CountGreaterThanBirthdayCommand());
        commands.put("history", new HistoryCommand());
        commands.put("print_unique_eye_color", new PrintUniqueEyeColorCommand());
        commands.put("print_field_descending_birthday", new PrintFieldDescendingBirthdayCommand());
        commands.put("remove_greater", new RemoveGreaterCommand());
        commands.put("remove_lower", new RemoveLowerCommand());
        commands.put("execute_script", new ExecuteScriptCommand(this));
    }

    /** Загружает коллекцию из XML-файла в память через DOMReader **/
    public void loadCollection() throws IOException {
        String xml = Files.readString(Path.of(DATA_FILE), StandardCharsets.UTF_8);
        DOMReader.createPersonsFromXMLString(xml);  // читает и наполняет CollectionManager :contentReference[oaicite:0]{index=0}:contentReference[oaicite:1]{index=1}
    }

    /** Сохраняет текущую коллекцию из CollectionManager в XML-файл через DOMWriter **/
    public void saveCollection() {
        try {
            Set<Person> persons = CollectionManager.getPersonSet();
            DOMWriter.writeCollectionInXML(persons);
        } catch (Exception e) {
            System.err.println("Error saving collection: " + e.getMessage());
        }
    }

    public CommandResponse process(CommandRequest request) {
        String name = request.getCommandName();
        Command cmd = commands.get(name);
        if (cmd == null) {
            return new CommandResponse("Неизвестная команда: " + name);
        }
        try {
            cmd.setArgument(request.getArgument());
            cmd.setPerson(request.getPersonData());
            HistoryManager.add(name);
            return new CommandResponse(cmd.execute());
        } catch (Exception e) {
            return new CommandResponse("Ошибка выполнения: " + e.getMessage());
        }
    }

    public String processScriptLine(String name, String argument, ArrayList<String> paramLines) {
        Command cmd = commands.get(name);
        if (cmd == null) return "Неизвестная команда: " + name;
        cmd.setArgument(argument);
        cmd.setPerson(null);
        return cmd.execute();
    }

}

