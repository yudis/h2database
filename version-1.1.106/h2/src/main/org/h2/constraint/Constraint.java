/*
 * Copyright 2004-2008 H2 Group. Multiple-Licensed under the H2 License,
 * Version 1.0, and under the Eclipse Public License, Version 1.0
 * (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.constraint;

import java.sql.SQLException;

import org.h2.engine.DbObject;
import org.h2.engine.Session;
import org.h2.index.Index;
import org.h2.message.Message;
import org.h2.message.Trace;
import org.h2.result.Row;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObjectBase;
import org.h2.table.Column;
import org.h2.table.Table;

/**
 * The base class for constraint checking.
 */
public abstract class Constraint extends SchemaObjectBase implements Comparable {

    /**
     * The constraint type name for check constraints.
     */
    public static final String CHECK = "CHECK";

    /**
     * The constraint type name for referential constraints.
     */
    public static final String REFERENTIAL = "REFERENTIAL";

    /**
     * The constraint type name for unique constraints.
     */
    public static final String UNIQUE = "UNIQUE";

    /**
     * The constraint type name for primary key constraints.
     */
    public static final String PRIMARY_KEY = "PRIMARY KEY";

    /**
     * The table for which this constraint is defined.
     */
    protected Table table;

    public Constraint(Schema schema, int id, String name, Table table) {
        initSchemaObjectBase(schema, id, name, Trace.CONSTRAINT);
        this.table = table;
        this.setTemporary(table.getTemporary());
    }

    /**
     * The constraint type name
     *
     * @return the name
     */
    public abstract String getConstraintType();

    /**
     * Check if this row fulfils the constraint.
     * This method throws an exception if not.
     *
     * @param session the session
     * @param t the table
     * @param oldRow the old row
     * @param newRow the new row
     */
    public abstract void checkRow(Session session, Table t, Row oldRow, Row newRow) throws SQLException;

    /**
     * Check if this constraint needs the specified index.
     *
     * @param index the index
     * @return true if the index is used
     */
    public abstract boolean usesIndex(Index index);

    /**
     * This index is now the owner of the specified index.
     *
     * @param index the index
     */
    public abstract void setIndexOwner(Index index);

    /**
     * Check if this constraint contains the given column.
     *
     * @param col the column
     * @return true if it does
     */
    public abstract boolean containsColumn(Column col);

    /**
     * Get the SQL statement to create this constraint.
     *
     * @return the SQL statement
     */
    public abstract String  getCreateSQLWithoutIndexes();

    /**
     * Check if this constraint needs to be checked before updating the data.
     *
     * @return true if it must be checked before updating
     */
    public abstract boolean isBefore();

    /**
     * Check the existing data. This method is called if the constraint is added
     * after data has been inserted into the table.
     *
     * @param session the session
     */
    public abstract void checkExistingData(Session session) throws SQLException;

    /**
     * This method is called after a related table has changed
     * (the table was renamed, or columns have been renamed).
     */
    public abstract void rebuild() throws SQLException;

    /**
     * Get the unique index used to enforce this constraint, or null if no index
     * is used.
     *
     * @return the index
     */
    public abstract Index getUniqueIndex();

    public void checkRename() {
        // ok
    }

    public int getType() {
        return DbObject.CONSTRAINT;
    }

    public Table getTable() {
        return table;
    }

    public Table getRefTable() {
        return table;
    }

    public String getDropSQL() {
        return null;
    }

    private int getConstraintTypeOrder() {
        String constraintType = getConstraintType();
        if (CHECK.equals(constraintType)) {
            return 0;
        } else if (PRIMARY_KEY.equals(constraintType)) {
            return 1;
        } else if (UNIQUE.equals(constraintType)) {
            return 2;
        } else if (REFERENTIAL.equals(constraintType)) {
            return 3;
        } else {
            throw Message.throwInternalError("type: " + constraintType);
        }
    }

    public int compareTo(Object other) {
        if (this == other) {
            return 0;
        }
        Constraint otherConstraint = (Constraint) other;
        int thisType = getConstraintTypeOrder();
        int otherType = otherConstraint.getConstraintTypeOrder();
        return thisType - otherType;
    }

}
