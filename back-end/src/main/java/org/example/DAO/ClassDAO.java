package org.example.DAO;

import org.example.model.Class;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class ClassDAO implements DAO<Class>{

    DBConnection dbConnection;
    DAOGetSet daoGetSet;

    public ClassDAO(DBConnection dbConnection, DAOGetSet daoGetSet) {
        this.dbConnection = dbConnection;
        this.daoGetSet = daoGetSet;
    }

    @Override
    public void add(Class claass) {
        dbConnection.runSqlQuery(String.format("INSERT INTO classes (id, name) VALUES ('%s', '%s');", claass.getId(), claass.getName()));
    }

    @Override
    public void remove(Class claass) {
        dbConnection.runSqlQuery(String.format("DELETE FROM classes WHERE id = '%s';", claass.getId()));
    }

    @Override
    public void edit(Class claass) {

    }

    @Override
    public List<Class> getAll() {
        return null;
    }

    @Override
    public Class get(UUID id) throws  SQLException {
        return null;
    }

}
