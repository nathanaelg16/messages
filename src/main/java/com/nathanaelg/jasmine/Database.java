package com.nathanaelg.jasmine;

import com.nathanaelg.jasmine.date.TimeStamp;
import com.nathanaelg.jasmine.messages.Message;
import com.nathanaelg.jasmine.messages.SummarizedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class Database {
    private final String CONNECTIONURL; //Connection URL used with DriverManager.
    private final String USERNAME;
    private final String PASSWORD;

    @Autowired
    public Database(Environment env) {
        CONNECTIONURL = env.getProperty("spring.datasource.url");
        USERNAME = env.getProperty("spring.datasource.username");
        PASSWORD = env.getProperty("spring.datasource.passwordHash");
    }

    public enum Tables {
        ARCHIVED_MESSAGES ("archived_messages"),
        MESSAGES("messages"),
        TOKENS ("tokens"),
        USERS ("users");

        private String field;

        Tables(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }

        @Override
        public String toString() {
            return getField();
        }
    }

    void setMessage(Message messageBean) throws Exception {
        Connection dbConnection =
                DriverManager.getConnection(CONNECTIONURL, USERNAME, PASSWORD);
        try (PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO " + Tables.MESSAGES + " SET recipient = ?, sender = ?, title = ?, message = ?, ts = ?, msgRead = 0, priority = ?;")) {
            statement.setString(1, messageBean.getRecipient());
            statement.setString(2, messageBean.getSender());
            statement.setString(3, messageBean.getTitle());
            statement.setString(4, messageBean.getMessage());
            statement.setString(5, messageBean.getTimestamp());
            statement.setInt(6, messageBean.getPriority());
            statement.executeUpdate();
        }
        dbConnection.close();
    }

    void archiveMessage(int messageID, boolean deleteAfter) throws Exception {
        executeUpdate("SELECT * INTO " + Tables.ARCHIVED_MESSAGES + " FROM " + Tables.MESSAGES + " WHERE id = ?;", messageID);
        executeUpdate("UPDATE " + Tables.ARCHIVED_MESSAGES + " SET deleted = ? WHERE id = ?;", deleteAfter, messageID);
        executeUpdate("DELETE FROM " + Tables.MESSAGES + " WHERE id = ?;", messageID);
    }

    Message getMessage(String recipient) throws Exception {
        Message message;

        Connection dbConnection = DriverManager.getConnection(CONNECTIONURL, USERNAME, PASSWORD);

        PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM " + Tables.MESSAGES + " WHERE recipient = ? AND msgRead = 0 ORDER BY priority DESC, id ASC LIMIT 1;");
        statement.setString(1, recipient);
        ResultSet rs = statement.executeQuery();
        if (rs.first()) {
            message = new Message(rs.getInt("id"), recipient,
                    rs.getString("sender"),
                    rs.getString("title"),
                    rs.getString("message"),
                    rs.getString("ts"));
        } else {
            statement.close();
            return new Message(-1, null, null,"No messages", "There are no more messages at this time.", TimeStamp.getCurrentTimeStamp());
        }
        rs.close();
        statement.close();

        try (PreparedStatement statement2 = dbConnection.prepareStatement("UPDATE " + Tables.MESSAGES + " SET msgRead = 1, msgReadTs = ? WHERE id = ?;")) {
            statement2.setString(1, TimeStamp.getCurrentTimeStamp());
            statement2.setInt(2, message.getID());
            statement2.executeUpdate();
        }

        dbConnection.close();
        return message;
    }

    String getMessageSender(int messageID) throws Exception {
        try (ResultSet rs = executeQuery("SELECT sender FROM " + Tables.MESSAGES + " WHERE id = ?;", messageID)) {
            if (rs.next()) {
                return rs.getString(1);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The message with the given message-id was not found.");
            }
        }
    }

    List<SummarizedMessage> getAllSentMessages(String sender) throws Exception {
        ArrayList<SummarizedMessage> messages = new ArrayList<>();
        CachedRowSet resultSet = executeQuery("SELECT id, title, msgRead, priority FROM " + Tables.MESSAGES + " WHERE sender = ?;", sender);
        while (resultSet.next()) {
            messages.add(new SummarizedMessage(resultSet.getInt(1), resultSet.getString(2), resultSet.getBoolean(3), resultSet.getInt(4)));
        }
        return messages;
    }

    Message getMessage(int messageID) throws Exception {
        Message message;

        Connection dbConnection = DriverManager.getConnection(CONNECTIONURL, USERNAME, PASSWORD);

        PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM " + Tables.MESSAGES + " WHERE id = ?;");
        statement.setInt(1, messageID);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.first()) {
            message = new Message(resultSet.getInt("id"),
                    resultSet.getString("recipient"),
                    resultSet.getString("sender"),
                    resultSet.getString("title"),
                    resultSet.getString("message"),
                    resultSet.getString("ts"));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to find message with given message id");
        }
        resultSet.close();
        statement.close();
        dbConnection.close();
        return message;
    }

    /**
     * Executes SQL data-manipulation statements, such as INSERT, UPDATE, DELETE, etc.
     * Besides the appropriate SQL command needed, it takes an array of arguments
     * in order to execute a sanitized PreparedStatement to avoid unsanitized inputs
     * into the database.
     * @param command SQL data manipulation statement with queries replaced with '?' to be replaced with values in @args
     * @param args Query values used for the @command, in order of appearance from left to right
     * @throws Exception SQLException or general Exceptions
     */
    public void executeUpdate(String command, Object... args) throws Exception {
        Connection dbConnection = DriverManager.getConnection(CONNECTIONURL, USERNAME, PASSWORD);
        try (PreparedStatement statement = dbConnection.prepareStatement(command)) {
            for (int i = 1; i <= args.length; i++) {
                statement.setObject(i, args[i-1]);
            }
            statement.executeUpdate();
        }
        dbConnection.close();
    }

    public CachedRowSet executeQuery(String query, Object... args) throws SQLException {
        Connection dbConnection = DriverManager.getConnection(CONNECTIONURL, USERNAME, PASSWORD);
        try (PreparedStatement statement = dbConnection.prepareStatement(query)) {
            for (int i = 1; i <= args.length; i++) {
                statement.setObject(i, args[i-1]);
            }
            ResultSet result = statement.executeQuery();
            CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(result);
            statement.close();
            dbConnection.close();
            return crs;
        }
    }

    public boolean checkExists(Database.Tables table, String whereClause, Object... args) throws SQLException {
        ResultSet result;
        int exists;
        if (whereClause != null) {
            result = executeQuery("SELECT EXISTS(SELECT 1 FROM " + table + " WHERE " + whereClause + ");", args);
        } else {
            result = executeQuery("SELECT EXISTS(SELECT 1 FROM " + table + ");");
        }
        result.next();
        exists = result.getInt(1);
        result.close();
        return exists == 1;
    }
}