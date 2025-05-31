package git.olegmusic.server.commandprocessing.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Хранит историю последних выполненных команд (без аргументов).
 */
public class HistoryManager {
    private static final int MAX_HISTORY = 9;
    private static final LinkedList<String> history = new LinkedList<>();

    /** Добавляет имя команды в начало истории. */
    public static synchronized void add(String commandName) {
        history.addFirst(commandName);
        if (history.size() > MAX_HISTORY) {
            history.removeLast();
        }
    }

    /** Возвращает список команд в порядке от самой последней к более старым. */
    public static synchronized List<String> getHistory() {
        return List.copyOf(history);
    }
}
