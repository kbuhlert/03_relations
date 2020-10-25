package at.campus02;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Species {

    @Id @GeneratedValue
    private Integer id;

    private String name;

    @OneToMany (mappedBy = "species", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Animal> animals = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Animal> getAnimals() {
        return animals;
    }
}
