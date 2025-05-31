package git.olegmusic.common;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

public class Person implements Comparable<Person>, Serializable {

    private Integer id;
    private String name;
    private Coordinates coordinates;
    private Date creationDate;
    private Long height;
    private ZonedDateTime birthday;
    private EyeColor eyeColor;
    private HairColor hairColor;
    private Location location;
    private static Integer idCounter = 1;

    public Person(String name, Coordinates coordinates, Long height, ZonedDateTime birthday,
                  EyeColor eyeColor, HairColor hairColor, Location location) {
        this.name = name;
        this.coordinates = coordinates;
        this.height = height;
        this.birthday = birthday;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.location = location;

        this.id = Person.getNextId();
        this.creationDate = new Date();
    }

    public Integer getId() { return this.id; }
    public Date getCreationDate() { return creationDate; }
    public Location getLocation() { return location; }
    public HairColor getHairColor() { return hairColor; }
    public EyeColor getEyeColor() { return eyeColor; }
    public ZonedDateTime getBirthday() { return birthday; }
    public Long getHeight() { return height; }
    public Coordinates getCoordinates() { return coordinates; }
    public String getName() { return name; }
    public static int getNextId() {
        return idCounter++;
    }

    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }
    public void setId(Integer id) { this.id = id; }
    public static void setIdCounter(Integer idCounter) { Person.idCounter = idCounter; }
    public void setName(String name) { this.name = name; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }
    public void setHeight(Long height) { this.height = height; }
    public void setBirthday(ZonedDateTime birthday) { this.birthday = birthday; }
    public void setEyeColor(EyeColor eyeColor) { this.eyeColor = eyeColor; }
    public void setHairColor(HairColor hairColor) { this.hairColor = hairColor; }
    public void setLocation(Location location) { this.location = location; }

    @Override
    public int compareTo(Person other) {
        if (this.height == null && other.height == null) return 0;
        if (this.height == null) return -1;
        if (other.height == null) return 1;
        return this.height.compareTo(other.height);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", eyeColor=" + eyeColor +
                ", creationDate=" + creationDate +
                ", birthday=" + birthday +
                ", height=" + height +
                ", hairColor=" + hairColor +
                ", location=" + location +
                '}';
    }
}