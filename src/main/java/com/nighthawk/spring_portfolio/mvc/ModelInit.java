package com.nighthawk.spring_portfolio.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.nighthawk.spring_portfolio.mvc.enemy.Enemy;
import com.nighthawk.spring_portfolio.mvc.enemy.EnemyJPA;
import com.nighthawk.spring_portfolio.mvc.note.Note;
import com.nighthawk.spring_portfolio.mvc.note.NoteJpaRepository;
import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonDetailsService;
import com.nighthawk.spring_portfolio.mvc.person.PersonRole;
import com.nighthawk.spring_portfolio.mvc.person.PersonRoleJpaRepository;

@Component
@Configuration // Scans Application for ModelInit Bean, this detects CommandLineRunner
public class ModelInit {  
    @Autowired NoteJpaRepository noteRepo;
    @Autowired PersonDetailsService personService;
    @Autowired PersonRoleJpaRepository roleRepo;
    @Autowired EnemyJPA enemyRepo; // Add enemy repository

    @Bean
    CommandLineRunner run() {  // The run() method will be executed after the application starts
        return args -> {

            // adding roles
            PersonRole[] personRoles = PersonRole.init();
            for (PersonRole role : personRoles) {
                PersonRole existingRole = roleRepo.findByName(role.getName());
                if (existingRole != null) {
                    // role already exists
                    continue;
                } else {
                    // role doesn't exist
                    roleRepo.save(role);
                }
            }

            List<Enemy> enemyList = Enemy.init(); // Initialize the list of enemies
            for (Enemy enemy : enemyList) {
                Enemy existingEnemy = enemyRepo.findByName(enemy.getName());
                if (existingEnemy == null) {
                    enemyRepo.save(enemy);
                }
            }

            // Person database is populated with test data
            Person[] personArray = Person.init();
            for (Person person : personArray) {
                //findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase
                List<Person> personFound = personService.list(person.getName(), person.getEmail());  // lookup
                if (personFound.size() == 0) {
                    personService.save(person);  // save

                    // Each "test person" starts with a "test note"
                    String text = "Test " + person.getEmail();
                    Note n = new Note(text, person);  // constructor uses new person as Many-to-One association
                    noteRepo.save(n);  // JPA Save
                    personService.addRoleToPerson(person.getEmail(), "ROLE_PLAYER");
                }
            }
            // for lesson demonstration: giving admin role to Mortensen
            personService.addRoleToPerson(personArray[0].getEmail(), "ROLE_ADMIN");
            personService.addRoleToPerson(personArray[1].getEmail(), "ROLE_ADMIN");
            personService.addRoleToPerson(personArray[2].getEmail(), "ROLE_ADMIN");
            personService.addRoleToPerson(personArray[3].getEmail(), "ROLE_ADMIN");
            personService.addRoleToPerson(personArray[4].getEmail(), "ROLE_ADMIN");
            personService.addRoleToPerson(personArray[5].getEmail(), "ROLE_ADMIN");
            
        };
    }
}
