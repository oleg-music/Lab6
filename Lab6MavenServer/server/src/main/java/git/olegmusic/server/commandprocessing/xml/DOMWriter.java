package git.olegmusic.server.commandprocessing.xml;

import git.olegmusic.common.Person;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Set;

/**
 * Класс DOMWriter предоставляет функциональность для записи данных в XML-документы
 * с использованием DOM (Document Object Model).
 * Реализация сохранена максимально близкой к исходному варианту лабораторной 5.
 */
public class DOMWriter {
    private static final String DATA_FILE = "server/src/main/resources/startFile.txt";

    /**
     * Записывает коллекцию объектов Person в XML-файл.
     * Каждый объект представляется как <person> с полями из оригинальной структуры.
     * @param personSet коллекция объектов Person для сохранения
     */
    public static void writeCollectionInXML(Set<Person> personSet) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("persons");
            doc.appendChild(root);

            for (Person person : personSet) {
                Element pElem = doc.createElement("person");

                addElement(doc, pElem, "id",           person.getId().toString());
                addElement(doc, pElem, "name",         person.getName());

                Element coords = doc.createElement("coordinates");
                addElement(doc, coords, "x", person.getCoordinates().getX().toString());
                addElement(doc, coords, "y", person.getCoordinates().getY().toString());
                pElem.appendChild(coords);

                addElement(doc, pElem, "creationDate", person.getCreationDate().toString());
                addElement(doc, pElem, "height",       person.getHeight() != null
                        ? person.getHeight().toString() : "");

                addElement(doc, pElem, "birthday",     person.getBirthday().toString());

                if (person.getEyeColor() != null) {
                    addElement(doc, pElem, "eyeColor", person.getEyeColor().toString());
                } else {
                    addElement(doc, pElem, "eyeColor", "");
                }

                addElement(doc, pElem, "hairColor",    person.getHairColor().toString());

                if (person.getLocation() != null) {
                    Element loc = doc.createElement("location");
                    addElement(doc, loc, "x", String.valueOf(person.getLocation().getX()));
                    addElement(doc, loc, "y", String.valueOf(person.getLocation().getY()));
                    addElement(doc, loc, "z", String.valueOf(person.getLocation().getZ()));
                    addElement(doc, loc, "name", person.getLocation().getName());
                    pElem.appendChild(loc);
                }

                root.appendChild(pElem);
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer tr = tf.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(DATA_FILE));
            tr.transform(source, result);

            System.out.println("Коллекция успешно сохранена в файл: " + DATA_FILE);
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении коллекции: " + e.getMessage());
        }
    }

    /**
     * Вспомогательный метод для добавления простого элемента с текстом.
     */
    private static void addElement(Document doc, Element parent, String tag, String text) {
        Element el = doc.createElement(tag);
        el.appendChild(doc.createTextNode(text != null ? text : ""));
        parent.appendChild(el);
    }
}
