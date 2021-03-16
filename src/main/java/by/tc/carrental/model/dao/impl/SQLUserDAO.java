package by.tc.carrental.model.dao.impl;

import by.tc.carrental.model.dao.DAOException;
import by.tc.carrental.model.dao.UserDAO;
import by.tc.carrental.model.dao.connection.DBConnectionPool;
import by.tc.carrental.model.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {
    private static final String ADD_NEW_USER = "INSERT INTO users (login, password, firstName, lastName, phoneNumber) " + "VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_USER = "SELECT * FROM users WHERE username = ? AND password = ?";
    //private static int role_client = 1; //to do - load from db
    //private static int role_ph = 2;

    private final DBConnectionPool connectionPool = DBConnectionPool.getInstance();

    @Override
    public boolean registration(User user) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = connectionPool.getConnection();

            preparedStatement = connection.prepareStatement(ADD_NEW_USER);

            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getLastName());
            preparedStatement.setString(5, user.getPhoneNumber());

            try {
                preparedStatement.executeUpdate();
            }
            catch(SQLException ex) {
                throw new DAOException("User with this username already existst!");
            }

        }
        catch(SQLException e) {
            throw new DAOException("Connection error: " + e.getMessage());
        }
        finally {
            if(preparedStatement != null) {
                try {
                    preparedStatement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null) {

                try {
                    connection.close();
                    connectionPool.releaseConnection(connection);
                }
                catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    @Override
    public User authorization(User user) throws DAOException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User authorizedUser = null;

        try
        {
            connection = connectionPool.getConnection();

            preparedStatement = connection.prepareStatement(FIND_USER);

            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getPassword());

            resultSet = preparedStatement.executeQuery();

            while(resultSet.next())
            {
                int userRole = resultSet.getInt(5);


            }
        }
        catch(SQLException e)
        {
            throw new DAOException("Connection error: " + e.getMessage());
        }
        finally
        {
            if(resultSet != null)
            {
                try
                {
                    resultSet.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }

            if(preparedStatement != null)
            {
                try
                {
                    preparedStatement.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }

            if(connection != null)
            {
                try
                {
                    connection.close();
                    connectionPool.releaseConnection(connection);
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return authorizedUser;
    }
}