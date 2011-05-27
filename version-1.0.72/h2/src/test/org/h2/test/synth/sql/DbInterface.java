/*
 * Copyright 2004-2008 H2 Group. Multiple-Licensed under the H2 License, 
 * Version 1.0, and under the Eclipse Public License, Version 1.0
 * (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.test.synth.sql;

import java.sql.SQLException;

/**
 * Represents a connection to a (real or simulated) database.
 */
public interface DbInterface {

    void reset() throws SQLException;

    void connect() throws Exception;

    void disconnect() throws SQLException;

    void end() throws SQLException;

    void createTable(Table table) throws SQLException;

    void dropTable(Table table) throws SQLException;

    void createIndex(Index index) throws SQLException;

    void dropIndex(Index index) throws SQLException;

    Result insert(Table table, Column[] c, Value[] v) throws SQLException;

    Result select(String sql) throws SQLException;

    Result delete(Table table, String condition) throws SQLException;

    /**
     * Update the given table with the new values.
     * 
     * @param table the table 
     * @param columns the columns to update
     * @param values the new values
     * @param condition the condition
     * @return the result of the update
     */
    Result update(Table table, Column[] columns, Value[] values, String condition) throws SQLException;

    void setAutoCommit(boolean b) throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;
}
