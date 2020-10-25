package at.campus02;

import org.junit.*;

import org.hamcrest.CoreMatchers;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class OneToOneSpec {

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
    public void persistAnimalAndOwnerStoresRelationInDatabase() {
        // given
        Animal bunny = new Animal();
        bunny.setName("Hansi");

        Student owner = prepareStudent("firstname", "lastname", Gender.FEMALE, null);

        bunny.setOwner(owner);
        //in beide Richtungen Persistieren, alternativ geht Refresh
        owner.setPet(bunny);

        // when
        manager.getTransaction().begin();
        manager.persist(owner);
        manager.persist(bunny);
        manager.getTransaction().commit();

        manager.clear();

        // then
        Animal bunnyFromDb = manager.find(Animal.class, bunny.getName());
        assertThat(bunnyFromDb.getOwner(), is(owner));

        Student ownerFromDB = manager.find(Student.class, owner.getId());
        assertThat(ownerFromDB.getPet(), is(notNullValue()));
    }

    @Test
    public void persistStudentWithCascadePersistsAlsoAnimalInDatabase() {
        // given
        Animal bunny = new Animal();
        bunny.setName("Hansi");

        Student owner = prepareStudent("firstname", "lastname", Gender.FEMALE, null);

        bunny.setOwner(owner);
        //in beide Richtungen Persistieren, alternativ geht Refresh
        owner.setPet(bunny);

        // when
        manager.getTransaction().begin();
        manager.persist(owner);
        //bei Verwendung von cascade(persist) wird das in der Datenbank automatich mitangelegt bzw. gelöscht, man muss Animal also nicht selber persistieren
        manager.getTransaction().commit();

        manager.clear();

        // then
        Animal bunnyFromDb = manager.find(Animal.class, bunny.getName());
        assertThat(bunnyFromDb.getOwner(), is(owner));

        Student ownerFromDB = manager.find(Student.class, owner.getId());
        assertThat(ownerFromDB.getPet(), is(notNullValue()));
        assertThat(ownerFromDB.getPet().getName(), is(bunny.getName()));
    }

    @Test
    public void refreshClosesReferencesNotHanledInMemory() {
        // given
        Animal bunny = new Animal();
        bunny.setName("Hansi");

        Student owner = prepareStudent("firstname", "lastname", Gender.FEMALE, null);

        //bunny.setOwner brauchen wir immer, da die Beziehung bei Animal eingetragen werden muss. Owner.setPet können wir uns sparen
        bunny.setOwner(owner);
        //in beide Richtungen Persistieren, alternativ geht Refresh
//        owner.setPet(bunny); -> Absichtlich nicht, da stattdessen Refresh verwendet wird

        // when
        manager.getTransaction().begin();
        //wenn owner.setPet nicht verwendet wird, müssen wir bunny selber persitieren
        manager.persist(bunny);
        manager.persist(owner);
        //bei Verwendung von cascade(persist) wird das in der Datenbank automatich mitangelegt bzw. gelöscht, man muss Animal also nicht selber persistieren
        manager.getTransaction().commit();

        //1st Level cache wird geleert
        manager.clear();

        //
        // alternative zum Refresh um Cache wirklich komplett zu leeren (alle Levels) ist evictAll(),
        // ist aber eigentlich eine Methode die nicht verwendet wird, sondern das Refresh
    //    manager.getEntityManagerFactory().getCache().evictAll();

        // then
        Animal bunnyFromDb = manager.find(Animal.class, bunny.getName());
        assertThat(bunnyFromDb.getOwner(), is(owner));

        Student ownerFromDB = manager.find(Student.class, owner.getId());

        //ohne Refresh (oder Cashe leeren) wird Relation nicht geschlossen, clear löscht nur Level-1 Cache
        // (=Persitance Unit -> Detached alle Entities) möglicherweise ist Objekt weiter unten aber trotzdem noch
        // gecached. Wenn es nicht mehr im Cache ist muss Entität neu erstellt werden, damit wird dann auch
        // gleich die cascading-Relation miterstellt
        assertThat(ownerFromDB.getPet(), is(nullValue()));

        //when
        manager.refresh(ownerFromDB);
        assertThat(ownerFromDB.getPet(), is(notNullValue()));
        assertThat(ownerFromDB.getPet().getName(), is(bunny.getName()));
    }
















}
