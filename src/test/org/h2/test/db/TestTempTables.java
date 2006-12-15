/*
 * Copyright 2004-2006 H2 Group. Licensed under the H2 License, Version 1.0 (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.test.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.test.TestBase;

public class TestTempTables extends TestBase {

    public void test() throws Exception {
        deleteDb("tempTables");
        Connection c1 = getConnection("tempTables");
        Connection c2 = getConnection("tempTables");
        Statement s1 = c1.createStatement();
        Statement s2 = c2.createStatement();
        s1.execute("CREATE LOCAL TEMPORARY TABLE LT(A INT)");
        s1.execute("CREATE GLOBAL TEMPORARY TABLE GT1(ID INT)");
        s2.execute("CREATE GLOBAL TEMPORARY TABLE GT2(ID INT)");
        s2.execute("CREATE LOCAL TEMPORARY TABLE LT(B INT)");
        s2.execute("SELECT B FROM LT");
        s1.execute("SELECT A FROM LT");
        s1.execute("SELECT * FROM GT1");
        s2.execute("SELECT * FROM GT1");
        s1.execute("SELECT * FROM GT2");
        s2.execute("SELECT * FROM GT2");
        s2.execute("DROP TABLE GT1");
        s2.execute("DROP TABLE GT2");
        s2.execute("DROP TABLE LT");
        s1.execute("DROP TABLE LT");

        // temp tables: 'on commit' syntax is currently not documented, because not tested well
        // and hopefully nobody is using it, as it looks like functional sugar 
        // (this features are here for compatibility only)
        ResultSet rs;
        c1.setAutoCommit(false);
        s1.execute("create local temporary table testtemp(id int) on commit delete rows");
        s1.execute("insert into testtemp values(1)");
        rs = s1.executeQuery("select * from testtemp");
        checkResultRowCount(rs, 1);
        c1.commit();
        rs = s1.executeQuery("select * from testtemp");
        checkResultRowCount(rs, 0);
        s1.execute("drop table testtemp");
        
        s1.execute("create local temporary table testtemp(id int) on commit drop");
        s1.execute("insert into testtemp values(1)");
        rs = s1.executeQuery("select * from testtemp");
        checkResultRowCount(rs, 1);
        c1.commit();
        try {
            rs = s1.executeQuery("select * from testtemp");
            error("testtemp should have been dropped automatically");
        } catch(SQLException e) {
            checkNotGeneralException(e);
        }
        
        c1.close();
        c2.close();
    }

}
