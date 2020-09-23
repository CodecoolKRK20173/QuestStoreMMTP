package org.example.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.DAO.DAOGetSet;
import org.example.DAO.DBConnection;
import org.example.DAO.Exception.AbsenceOfRecordsException;
import org.example.DAO.StudentDAO;
import org.example.DAO.UserDAO;
import org.example.config.PasswordCrypter;
import org.example.model.Student;
import org.example.model.User;
import org.example.services.DecoderURL;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class StudentHandler implements HttpHandler {

    DBConnection dbConnection;
    StudentDAO studentDAO;
    UserDAO userDAO;

    public StudentHandler() {
        this.dbConnection = new DBConnection();
        this.studentDAO = new StudentDAO(dbConnection);
        this.userDAO = new UserDAO(dbConnection);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response = "";
        System.out.println("Method " + method);
        int status = 200;
        try {
            if (method.equals("GET")) {
                response = getStudents();
                System.out.println(response);
                ResponseHelper.sendResponse(response, exchange, status);
            }
            if (method.equals("POST")) {
                String url = exchange.getRequestURI().getRawPath();
                String[] urlParts = url.split("/");
                String userDetailsID = urlParts[2];
                if (urlParts[3].equals("remove")) {
                    removeStudent(userDetailsID);
                }
                if (urlParts[3].equals("edit-name")) {
                    editStudentName(urlParts[2], DecoderURL.polishDecoder(urlParts[4]));
                }
                if (urlParts[3].equals("edit-surname")) {
                    editStudentSurname(urlParts[2], DecoderURL.polishDecoder(urlParts[4]));
                }
                if (urlParts[3].equals("edit-mail")) {
                    editStudentMail(urlParts[2], DecoderURL.polishDecoder(urlParts[4]));
                }
                if (urlParts[3].equals("edit-phone")) {
                    editStudentPhone(urlParts[2], DecoderURL.polishDecoder(urlParts[4]));
                }
                if (urlParts[3].equals("add")) {
                    addStudent();
                }
                response = getStudents();
                ResponseHelper.sendResponse(response, exchange, status);
                System.out.println("New response: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(404, response.getBytes().length);
        }
    }

    private String getStudents() throws JsonProcessingException {
        List<Student> students = studentDAO.getAll();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(students);
    }

    private void removeStudent(String userDetailsID) throws Exception {
        User user = userDAO.get(UUID.fromString(userDetailsID));
        studentDAO.remove(user);
    }

    private void editStudentName(String userDetailsID, String newName) throws Exception {
        User student = userDAO.get(UUID.fromString(userDetailsID));
        student.setName(newName);
        studentDAO.edit(student);
    }

    private void editStudentSurname(String userDetailsID, String newSurname) throws AbsenceOfRecordsException {
        User user = userDAO.get(UUID.fromString(userDetailsID));
        user.setSurname(newSurname);
        studentDAO.edit(user);
    }

    private void editStudentMail(String userDetailsID, String newMail) throws AbsenceOfRecordsException {
        User user = userDAO.get(UUID.fromString(userDetailsID));
        user.setEmail(newMail);
        studentDAO.edit(user);
    }

    private void editStudentPhone(String userDetailsID, String newPhone) throws AbsenceOfRecordsException {
        User user = userDAO.get(UUID.fromString(userDetailsID));
        user.setPhoneNumber(newPhone);
        studentDAO.edit(user);
    }

    private void addStudent() {
        Student student = new Student(UUID.randomUUID(), "Name", "Surname", "mail@mail.com",
                PasswordCrypter.crypter("password"), "student", true, "444 222 000",
                0);
        studentDAO.add(student);
    }

}
