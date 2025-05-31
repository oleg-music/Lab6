package git.olegmusic.server.commandprocessing.utils;

import git.olegmusic.common.Person;

import java.util.*;

/**
 * Класс CollectionManager управляет коллекцией объектов Person.
 * Предоставляет методы для добавления, удаления, поиска и очистки элементов коллекции.
 * Также хранит время инициализации коллекции.
 */
public class CollectionManager {

    /**
     * Время инициализации коллекции.
     */
    private static Date initializationTime = new Date();

    /**
     * Коллекция для хранения объектов Person.
     */
    private static HashSet<Person> personSet = new HashSet<>();

    /**
     * Добавляет объект Person в коллекцию.
     *
     * @param person Объект Person для добавления.
     */
    public static void add(Person person) {
        personSet.add(person);
    }

    /**
     * Возвращает коллекцию объектов Person.
     *
     * @return Коллекция объектов Person.
     */
    public static HashSet<Person> getPersonSet() {
        return personSet;
    }

    /**
     * Возвращает время инициализации коллекции.
     *
     * @return Время инициализации коллекции.
     */
    public static Date getInitializationTime() {
        return initializationTime;
    }

    /**
     * Ищет объект Person по его идентификатору.
     *
     * @param id Идентификатор объекта Person.
     * @return Найденный объект Person или null, если объект не найден.
     */
    public static Person getPersonById(Integer id) {
        for (Person person : personSet) {
            if (person.getId() == id) {
                return person;
            }
        }
        return null;
    }

    /**
     * Удаляет объект Person из коллекции.
     *
     * @param person Объект Person для удаления.
     */
    public static void removePerson(Person person) {
        personSet.remove(person);
    }

    /**
     * Очищает коллекцию объектов Person.
     */
    public static void clearPersonSet() {
        personSet.clear();
    }
}

