package at.campus02;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Student {
    // automatisch generierte technische ID, PrimaryKey
    @Id
    @GeneratedValue
    private Integer id;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    // enum mit 2 Werten: MALE und FEMALE
    private Gender gender;

    @OneToOne (mappedBy = "owner", cascade = CascadeType.ALL)   //Animal wird durch Cascade-Verindung automatisch miterzeugt und gelöscht
    private Animal pet;

    public Animal getPet() {
        return pet;
    }

    public void setPet(Animal pet) {
        this.pet = pet;
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id) &&
                Objects.equals(firstName, student.firstName) &&
                Objects.equals(lastName, student.lastName) &&
                Objects.equals(birthday, student.birthday) &&
                gender == student.gender;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, birthday, gender);
    }
}
