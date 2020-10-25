package at.campus02;

import org.eclipse.persistence.jpa.jpql.parser.ScalarExpressionBNF;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OneToManySpec {


    private EntityManagerFactory factory;
    private EntityManager manager;

    // <editor-fold desc="Hilfsfunktionen">
    private Student prepareStudent(
            String firstname,
            String lastname,
            Gender gender,
            String birthdayString
    ) {
        Student student = new Student();
        student.setFirstName(firstname);
        student.setLastName(lastname);
        student.setGender(gender);
        if (birthdayString != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            student.setBirthday(LocalDate.parse(birthdayString, formatter));
        }
        return student;
    }

    private void create(Student student) {
        manager.getTransaction().begin();
        manager.persist(student);
        manager.getTransaction().commit();
    }

    @Before
    public void setUp() {
        factory = Persistence.createEntityManagerFactory("relationsPersistenceUnit");
        manager = factory.createEntityManager();
    }

    @After
    public void tearDown() {
        if (manager.isOpen()) {
            manager.close();
        }
        if (factory.isOpen()) {
            factory.close();
        }
    }
    // </editor-fold>

    @Ignore
    @Test
    public void persistSpeciesWithCascadeAlsoPersistsAnimals() {
        // given
        Animal bunny = new Animal();
        bunny.setName("Hansi");
        Animal cat = new Animal();
        cat.setName("Rudolf");
        Species mammals = new Species();
        mammals.setName("Mammals");

        //Referenzen im Speicehr verwalten
        bunny.setSpecies(mammals);
        cat.setSpecies(mammals);

        //für Cascade persist müsen animals den Species zugewiesen werden
        mammals.getAnimals().add(bunny);
        mammals.getAnimals().add(cat);

        // when
        manager.getTransaction().begin();
        manager.persist(mammals);
        //nur mammals werden persistiert, alle Tiere sollten in der Datenbank landen
        manager.getTransaction().commit();

        manager.clear();

        // then
        Species mammalsFromDB = manager.find(Species.class, mammals.getId());
        //sicherheitshalber zusätzlich referesh um sicher zu sein, dass mit DB abgeglichen wurde
        manager.refresh(mammalsFromDB);
        assertThat(mammalsFromDB.getAnimals().size(), is(2));

        assertThat(bunny.getSpecies().getId(), is(mammals.getId()));
    }


    @Test
    public void updateExample() {
        //given
        Animal clownfish = new Animal();
        clownfish.setName("nemo");
        Animal squirrel = new Animal();
        squirrel.setName("Squirrel");
        Species fish = new Species();
        squirrel.setName("Fish");

        //Referenzen verwalten
        clownfish.setSpecies(fish);
        //Fehler, wird hier extra eingebaut
        squirrel.setSpecies(fish);
        fish.getAnimals().add(squirrel);    //brauchen wir für cascade
        fish.getAnimals().add(clownfish);

        //Save
        manager.getTransaction().begin();
        manager.persist(fish);
        manager.getTransaction().commit();
        manager.clear();    //damit wird fish und alles was dran

        //when: Fehler korrigieren und gleich Squirrel(Fehler) löschen
        manager.getTransaction().begin();
        Species managedFish = manager.merge(fish);
        managedFish.getAnimals().remove(squirrel);
        Animal managedSquirrel = manager.merge(squirrel);   //detached Entity ist wieder gemanaged wegen dem merge

//        manager.remove(managedSquirrel);
        manager.getTransaction().commit();

    }

}
