package dao;
import models.User;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.sql.SQLOutput;

import static org.junit.Assert.*;

public class Sql2oUserDAOTest {
    private Sql2oUserDAO userDAO;
    private Connection connection;

    @Before
    public void setUp() {
        String connString = "jdbc:postgresql://localhost:5432/nutrisitest";
        Sql2o sql2o = new Sql2o(connString, "student","");
        userDAO =   new Sql2oUserDAO(sql2o);
        connection = sql2o.open();
    }

    @Test
    public void createNewUser(){
        User user = userDAO.create("Natalie","natalie@yahoo.com","pass1234");
        assertEquals("Natalie",user.getName());
        assertEquals("natalie@yahoo.com", user.getEmail());
        assertEquals("pass1234", user.getPassword());
        assertEquals(1, user.getId());
    }

    @Test
    public void returnsTrueIfUserExists() {
        userDAO.create("Natalie","natalie@yahoo.com","pass1234");
        boolean userExists = userDAO.userExists("natalie@yahoo.com");
        assertTrue(userExists);
    }

    @Test
    public void returnsFalseIfUserNotExists() {
        boolean userExists = userDAO.userExists("natalie@yahoo.com");
        assertFalse(userExists);
    }

    @Test
    public void authenticateCorrectLoginDetailsReturnsUser() {
        userDAO.create("Natalie","natalie@yahoo.com","pass1234");
        User auth = userDAO.authenticate("natalie@yahoo.com","pass1234");
        User user = new User ("Natalie","natalie@yahoo.com","pass1234");
        assertEquals(user, auth);
    }

    @Test
    public void authenticateIncorrectEmailGivesError() {
        userDAO.create("Natalie","natalie@yahoo.com","pass1234");
        User auth = userDAO.authenticate("wrongemail@yahoo.com","pass1234");
        assertEquals("Incorrect email", userDAO.getError());
    }

    @Test
    public void authenticateIncorrectPasswordGivesError() {
        userDAO.create("Natalie","natalie@yahoo.com","pass1234");
        User auth = userDAO.authenticate("natalie@yahoo.com","wrongpass");
        assertEquals("Incorrect password", userDAO.getError());
    }

    @After
    public void tearDown(){
        connection.createQuery("TRUNCATE users RESTART IDENTITY CASCADE").executeUpdate();
        connection.close();
    }
}
