import java.sql.*;
import java.util.Properties;

/**
 * https://bitbucket.org/dbschema/cassandra-jdbc-driver/src/master/
 * <p>
 * #
 * # Copyright (C) 2015-2017, Zhichun Wu
 * #
 * # Licensed to the Apache Software Foundation (ASF) under one
 * # or more contributor license agreements.  See the NOTICE file
 * # distributed with this work for additional information
 * # regarding copyright ownership.  The ASF licenses this file
 * # to you under the Apache License, Version 2.0 (the
 * # "License"); you may not use this file except in compliance
 * # with the License.  You may obtain a copy of the License at
 * #
 * #   http://www.apache.org/licenses/LICENSE-2.0
 * #
 * # Unless required by applicable law or agreed to in writing,
 * # software distributed under the License is distributed on an
 * # "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * # KIND, either express or implied.  See the License for the
 * # specific language governing permissions and limitations
 * # under the License.
 * #
 * <p>
 * # JDBC driver version
 * version: 0.6.2
 * # general configuration
 * locale : en_US
 * <p>
 * # the configuration below for the driver is pretty much same as the following connection url:
 * # jdbc:c*:datastax://localhost:-1/system_auth?user=cassandra&password=cassandra&quiet=true&sqlFriendly=true&...
 * driver :
 * # DataStax Java driver as primary, and possiblly astyanax for Cassandra 2.0.x later
 * provider : datastax
 * # comma separated hosts(port is optional there and only the first one will be considered)
 * hosts : localhost
 * # non-positive port implies the default port will be used in the provider, which is datastax in this case
 * port : -1
 * # by default everyone has access to system_auth keyspace
 * keyspace : system_auth
 * user : cassandra
 * password : cassandra
 * <p>
 * # be quiet for unsupported JDBC features
 * quiet : true
 * # this enables SQL parser, which tries to translate SQL into equivalent CQL(s) - think about MDX to SQL
 * sqlFriendly : true
 * # if you turn tracing read/write requests on, you'll see more logs(DEBUG level) in backend on how it goes
 * # more on https://docs.datastax.com/en/cql/3.3/cql/cql_reference/tracing_r.html
 * tracing : false
 * <p>
 * readConsistencyLevel : &CL LOCAL_ONE
 * # you may use ANY for better write performance, if hinted handoff is not going to be an issue in your case
 * # also please be aware of that counter table does not support ANY at the moment
 * writeConsistencyLevel : *CL
 * consistencyLevel : *CL
 * <p>
 * # LOGGED or UNLOGGED, you may set COUNTER in magic comments
 * batch : UNLOGGED
 * # parsing SQL / CQL is not free hence we cache what we did before
 * cqlCacheSize : 1000
 * # set 0 to let the provider the decide what's the best
 * fetchSize : 100
 * # append "LIMIT 10000" to all queries by default, set 0 to disable this
 * rowLimit : 10000
 * readTimeout : 30 # in seconds
 * connectionTimeout : 5 # in seconds
 * keepAlive : true
 * compression : LZ4 # NONE, LZ4 or SNAPPY
 * <p>
 * # logging configuration for tinylog(http://www.tinylog.org/configuration)
 * logger :
 * level : INFO
 * stacktrace : -1
 * format : "{date:yyyy-MM-dd HH:mm:ss} [{thread}] {class_name}.{method}({line}) {level}: {message}"
 */
public class CassandraTest {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        CassandraTest cassandraTest = new CassandraTest();
        String url = "jdbc:c*://192.168.1.186:9042";
        Properties properties = new Properties();
//        用户密码为空无需设置，设置会报错
//        properties.put("user","");
//        properties.put("password","");
        Class.forName("com.github.cassandra.jdbc.CassandraDriver");
        try (Connection conn = DriverManager.getConnection(url, properties)) {
            String sql = "CREATE KEYSPACE test WITH replication = {'class': 'SimpleStrategy','replication_factor': '1'};";
            cassandraTest.create(conn, sql);
            sql = "CREATE TABLE test.users (lastname text PRIMARY KEY,age int,city text,email text,firstname text);";
            cassandraTest.create(conn, sql);
            cassandraTest.insert(conn);
            cassandraTest.query(conn);
            cassandraTest.update(conn);
            cassandraTest.query(conn);
            cassandraTest.delete(conn);
            cassandraTest.query(conn);
            cassandraTest.insertBatch(conn);
            cassandraTest.queryAll(conn);
        }
    }

    public void create(Connection conn, String sql) {
        try (Statement stmt = conn.createStatement()) {
            System.out.println(stmt.executeUpdate(sql));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void insert(Connection conn) {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO test.users (lastname, age, city, email, " +
                "firstname) VALUES (?,?,?,?,?)")) {
            stmt.setString(1, "Jones");
            stmt.setInt(2, 35);
            stmt.setString(3, "Austin");
            stmt.setString(4, "bob@example.com");
            stmt.setString(5, "Bob");
            System.out.println(stmt.executeUpdate());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void insertBatch(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            for (int j = 0; j < 5; j++) {
                for (int i = 0; i < 100; i++) {
                    int id = (j * 100) + (i + 1);
                    stmt.addBatch("INSERT INTO test.users(lastname, age, city, email,firstname) VALUES ('Jones_" + id +
                            "', " + id + ", 'Austin', 'bob@example.com', 'Bob')");
                }
                int[] i = stmt.executeBatch();
                System.out.println(i.length);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Connection conn) {
        try (PreparedStatement stmt = conn.prepareStatement("UPDATE test.users SET age =?  WHERE lastname =? ")) {
            stmt.setInt(1, 36);
            stmt.setString(2, "Jones");
            System.out.println(stmt.executeUpdate());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(Connection conn) throws ClassNotFoundException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM test.users WHERE lastname=?")) {
            stmt.setString(1, "Jones");
            System.out.println(stmt.executeUpdate());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void query(Connection conn) throws ClassNotFoundException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM test.users WHERE lastname=?")) {
            stmt.setString(1, "Jones");
            ResultSet rs = stmt.executeQuery();
            System.out.println(stmt.getResultSetType() == ResultSet.TYPE_FORWARD_ONLY);
            System.out.println(rs.getFetchSize());  //defaultSize from server,initSize from jdbc url
            ResultSetMetaData rsmd = rs.getMetaData();
            int size = rsmd.getColumnCount();
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= size; i++) {
                builder.append(rsmd.getColumnLabel(i));
                if (i != size) {
                    builder.append(",");
                }
            }
            System.out.println(builder.toString());
            builder.setLength(0);
            while (rs.next()) {
                for (int i = 1; i <= size; i++) {
//                    System.out.println(rsmd.getColumnLabel(i));
//                    System.out.println(rsmd.getColumnName(i));
//                    System.out.println(rsmd.getColumnTypeName(i));
//                    System.out.println(rsmd.getColumnDisplaySize(i));
//                    System.out.println(rsmd.getPrecision(i));
//                    System.out.println(rsmd.getScale(i));
//                    System.out.println(rsmd.getColumnType(i));
//                    System.out.println(rs.getString(i));
                    builder.append(rs.getObject(i));
                    if (i != size) {
                        builder.append(",");
                    }
                }
                System.out.println(builder.toString());
                builder.setLength(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void queryAll(Connection conn) throws ClassNotFoundException {
        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(50);
            ResultSet rs = stmt.executeQuery("SELECT * FROM test.users");
            rs.setFetchSize(50);
            System.out.println(rs.getType() == ResultSet.TYPE_FORWARD_ONLY);
            System.out.println(rs.getConcurrency() == ResultSet.CONCUR_READ_ONLY);
            System.out.println(rs.getFetchSize());  //defaultSize from server,initSize from jdbc url
            ResultSetMetaData rsmd = rs.getMetaData();
            int size = rsmd.getColumnCount();
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= size; i++) {
                builder.append(rsmd.getColumnLabel(i));
                if (i != size) {
                    builder.append(",");
                }
            }
            System.out.println(builder.toString());
            builder.setLength(0);
            while (rs.next()) {
                for (int i = 1; i <= size; i++) {
//                    System.out.println(rsmd.getColumnLabel(i));
//                    System.out.println(rsmd.getColumnName(i));
//                    System.out.println(rsmd.getColumnTypeName(i));
//                    System.out.println(rsmd.getColumnDisplaySize(i));
//                    System.out.println(rsmd.getPrecision(i));
//                    System.out.println(rsmd.getScale(i));
//                    System.out.println(rsmd.getColumnType(i));
//                    System.out.println(rs.getString(i));
                    builder.append(rs.getObject(i));
                    if (i != size) {
                        builder.append(",");
                    }
                }
                System.out.println(builder.toString());
                builder.setLength(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
