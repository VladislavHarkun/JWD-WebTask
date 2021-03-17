package by.tc.carrental.model.dao.connection;

import by.tc.carrental.model.dao.DAOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DBConnectionPool {
    private String driver;
    private String URL;
    private String login;
    private String password;
    private int poolSize;

    private BlockingQueue<Connection> connectionQueue;
    private BlockingQueue<Connection> usedConnections;

    private static DBConnectionPool instance = null;
    static {
        try {
            instance = new DBConnectionPool();
        }catch (Exception e){
            //dao
        }
    }
    private DBConnectionPool() throws DAOException{
        DBResourceManager dbResourceManager = DBResourceManager.getInstance();
        this.driver = dbResourceManager.getValue(DBParameter.DB_DRIVER);
        this.URL = dbResourceManager.getValue(DBParameter.DB_URL);
        this.login = dbResourceManager.getValue(DBParameter.DB_USER);
        this.password = dbResourceManager.getValue(DBParameter.DB_PASSWORD);
        this.poolSize = Integer.parseInt(dbResourceManager.getValue(DBParameter.DB_POOLSIZE));
        init();
    }
    private void init() throws DAOException{
        try {
            Class.forName(driver);
            connectionQueue = new ArrayBlockingQueue<Connection>(poolSize);
            usedConnections = new ArrayBlockingQueue<Connection>(poolSize);
            for (int i=0;i<poolSize;i++){
                connectionQueue.add(DriverManager.getConnection(URL,login,password));
            }
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }
    public static DBConnectionPool getInstance(){
        return instance;
    }
    public Connection getConnection() throws DAOException {
        Connection connection = null;

        try {
            connection = connectionQueue.take();
            usedConnections.add(connection);
        }catch (InterruptedException e){
            //daoExc
        }
        return  connection;
    }
    public boolean closeConnection(Connection connection){
        if(connection != null){
            usedConnections.remove(connection);
            return connectionQueue.add(connection);
        }
        return false;
    }
}
