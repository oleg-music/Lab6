package git.olegmusic.server.commandprocessing.xml;

import git.olegmusic.server.commandprocessing.utils.CollectionManager;
import git.olegmusic.common.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class DOMReader {
    public static void createPersonsFromXMLString(String fileInString) {
        try (ByteArrayInputStream input = new ByteArrayInputStream(fileInString.getBytes())) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(input);
            document.getDocumentElement().normalize();

            NodeList personNodes = document.getElementsByTagName("person");
            boolean isFileCorrect = true;
            HashSet<Integer> usedIds = new HashSet<>();

            for (int i = 0; i < personNodes.getLength(); i++) {
                Node personNode = personNodes.item(i);
                if (personNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element personElement = (Element) personNode;

                    Integer id;
                    try {
                        String idString = getElementValue(personElement, "id");
                        if (idString == null || idString.trim().isEmpty()) {
                            System.out.println("Поле 'id' не может быть пустым.");
                            isFileCorrect = false;
                            break;
                        }
                        id = Integer.valueOf(idString);
                        if (id <= 0) {
                            System.out.println("Поле 'id' должно быть больше 0.");
                            isFileCorrect = false;
                            break;
                        }
                        if (usedIds.contains(id)) {
                            System.out.println("Поле 'id' должно быть уникальным. Найдено повторяющееся значение: " + id);
                            isFileCorrect = false;
                            break;
                        }
                        usedIds.add(id);
                    } catch (NumberFormatException e) {
                        System.out.println("Неверный формат поля 'id'.");
                        isFileCorrect = false;
                        break;
                    }

                    String name = getElementValue(personElement, "name");
                    if (name == null || name.isEmpty()) {
                        System.out.println("Поле 'name' не может быть пустым. Введите имя корректно.");
                        isFileCorrect = false;
                        break;
                    }

                    Coordinates coordinates;
                    try {
                        Long x = Long.valueOf(getElementValue(personElement, "coordinates/x"));
                        Float y = Float.valueOf(getElementValue(personElement, "coordinates/y"));
                        if (y <= -679) {
                            System.out.println("Значение координаты 'y' у поля coordinates должно быть больше -679.");
                            isFileCorrect = false;
                            break;
                        }
                        coordinates = new Coordinates(x, y);
                    } catch (NumberFormatException e) {
                        System.out.println("Неверный формат поля 'coordinates'.");
                        isFileCorrect = false;
                        break;
                    }

                    Date creationDate;
                    try {
                        String creationDateString = getElementValue(personElement, "creationDate");
                        if (creationDateString == null || creationDateString.trim().isEmpty()) {
                            System.out.println("Поле 'creationDate' не может быть пустым.");
                            isFileCorrect = false;
                            break;
                        }
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                        creationDate = dateFormat.parse(creationDateString);
                    } catch (ParseException e) {
                        System.out.println("Неверный формат поля 'creationDate'. Ожидается формат: EEE MMM dd HH:mm:ss zzz yyyy.");
                        isFileCorrect = false;
                        break;
                    }

                    Long height;
                    try {
                        String heightString = getElementValue(personElement, "height");
                        if (heightString == null || heightString.trim().isEmpty()) {
                            height = null;
                        } else {
                            height = Long.valueOf(heightString);
                            if (height <= 0) {
                                System.out.println("Рост должен быть больше 0");
                                isFileCorrect = false;
                                break;
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Неверный формат поля 'height'.");
                        isFileCorrect = false;
                        break;
                    }

                    ZonedDateTime birthday;
                    try {
                        birthday = ZonedDateTime.parse(getElementValue(personElement, "birthday"));
                    } catch (DateTimeParseException e) {
                        System.out.println("Неверный формат поля 'birthday'.");
                        isFileCorrect = false;
                        break;
                    }

                    EyeColor eyeColor;
                    try {
                        String eyeColorString = getElementValue(personElement, "eyeColor");
                        if (eyeColorString == null || eyeColorString.trim().isEmpty()) {
                            eyeColor = null;
                        } else {
                            eyeColor = EyeColor.valueOf(eyeColorString);
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Неверный формат поля 'eyeColor'.");
                        isFileCorrect = false;
                        break;
                    }

                    HairColor hairColor;
                    try {
                        String hairColorString = getElementValue(personElement, "hairColor");
                        if (hairColorString == null || hairColorString.trim().isEmpty()) {
                            System.out.println("Поле 'hairColor' не может быть пустым.");
                            isFileCorrect = false;
                            break;
                        }
                        hairColor = HairColor.valueOf(hairColorString);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Неверный формат поля 'hairColor'.");
                        isFileCorrect = false;
                        break;
                    }

                    Location location;
                    try {
                        String locationString = getElementValue(personElement, "location");
                        if (locationString == null || locationString.trim().isEmpty()) {
                            location = null;
                        } else {
                            String locationX = getElementValue(personElement, "location/x");
                            String locationY = getElementValue(personElement, "location/y");
                            String locationZ = getElementValue(personElement, "location/z");
                            String locationName = getElementValue(personElement, "location/name");

                            if (locationY == null || locationY.trim().isEmpty()) {
                                System.out.println("Поле 'locationY' не может быть пустым.");
                                isFileCorrect = false;
                                break;
                            } else if (locationZ == null || locationZ.trim().isEmpty()) {
                                System.out.println("Поле 'locationZ' не может быть пустым.");
                                isFileCorrect = false;
                                break;
                            } else if (locationName == null || locationName.trim().isEmpty()) {
                                System.out.println("Поле 'locationName' не может быть пустым.");
                                isFileCorrect = false;
                                break;
                            } else {
                                float x = Float.parseFloat(locationX);
                                Integer y = Integer.valueOf(locationY);
                                Float z = Float.valueOf(locationZ);
                                location = new Location(x, y, z, locationName);
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Неверный формат поля 'location'.");
                        isFileCorrect = false;
                        break;
                    }

                    Person person = new Person(name, coordinates, height, birthday, eyeColor, hairColor, location);
                    person.setId(id);
                    person.setCreationDate(creationDate);
                    CollectionManager.add(person);
                }
            }

            if (!isFileCorrect) {
                System.out.println("Данные из файла некорректны.");
            }

        } catch (Exception e) {
            System.out.println("Ошибка при парсинге XML: " + e.getMessage());
        }
    }

    private static String getElementValue(Element element, String tagPath) {
        String[] tags = tagPath.split("/");
        NodeList nodeList = element.getElementsByTagName(tags[0]);

        if (nodeList.getLength() > 0) {
            Element currentElement = (Element) nodeList.item(0);

            for (int i = 1; i < tags.length; i++) {
                nodeList = currentElement.getElementsByTagName(tags[i]);
                if (nodeList.getLength() > 0) {
                    currentElement = (Element) nodeList.item(0);
                } else {
                    return null;
                }
            }

            return currentElement.getTextContent();
        }

        return null;
    }
}