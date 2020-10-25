package at.campus02;

import javax.persistence.*;

@Entity
public class Animal {

    @Id
    private String name;

    @OneToOne //(cascade = CascadeType.PERSIST)
    private Student owner;

    @ManyToOne
    private Species species;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Student getOwner() {
        return owner;
    }

    public void setOwner(Student owner) {
        this.owner = owner;
    }

    public Species getSpecies() {
        return species;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }
}
