package git.olegmusic.server.commandprocessing.commands;

import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.utils.HistoryManager;

/**
 * Команда history:
 * выводит последние 9 выполненных команд (без аргументов) в обратном хронологическом порядке.
 */
public class HistoryCommand implements Command {
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
        StringBuilder sb = new StringBuilder();
        for (String cmdName : HistoryManager.getHistory()) {
            sb.append(cmdName).append("\n");
        }
        return sb.toString().trim();
    }

    @Override
    public String descr() {
        return "history : вывести последние 9 выполненных команд (без аргументов)";
    }
}
