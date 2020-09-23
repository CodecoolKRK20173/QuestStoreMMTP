package org.example.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.DAO.ArtifactDAO;
import org.example.DAO.DBConnection;
import org.example.DAO.Exception.AbsenceOfRecordsException;
import org.example.DAO.StudentDAO;
import org.example.model.Artifact;
import org.example.model.Student;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class WalletHandler implements HttpHandler {

    DBConnection dbConnection;
    StudentDAO studentDAO;
    ArtifactDAO artifactDAO;

    public WalletHandler() {
        this.dbConnection = new DBConnection();
        this.artifactDAO = new ArtifactDAO(dbConnection);
        this.studentDAO = new StudentDAO(dbConnection);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response = "";
        System.out.println("Method " + method);
        int status = 200;
        try {
            if (method.equals("GET")) {
                String url = exchange.getRequestURI().getRawPath();
                String[] urlParts = url.split("/");
                String userDetailsID = urlParts[2];
                if (urlParts[3].equals("balance")) {
                    response = getBalance(userDetailsID);
                }
                if (urlParts[3].equals("cards")) {
                    response = getArtifacts(userDetailsID);
                }
                System.out.println(response);
                ResponseHelper.sendResponse(response, exchange, status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            assert response != null;
            exchange.sendResponseHeaders(404, response.getBytes().length);
        }
    }

    private String getArtifacts(String userDetailsID) throws JsonProcessingException {
        List<Artifact> artifacts = artifactDAO.getAllbyID(UUID.fromString(userDetailsID));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(artifacts);
    }

    private String getBalance(String id) throws AbsenceOfRecordsException {
        Student student = studentDAO.get(UUID.fromString(id));
        return String.valueOf(student.getCoins());
    }

}
