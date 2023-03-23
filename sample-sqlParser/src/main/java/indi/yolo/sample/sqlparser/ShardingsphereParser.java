package indi.yolo.sample.sqlparser;

import org.apache.shardingsphere.sql.parser.SQLParserEngine;
import org.apache.shardingsphere.sql.parser.SQLParserEngineFactory;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.JoinSpecificationSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.JoinedTableSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.TableFactorSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.TableReferenceSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.column.ColumnSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.expr.ExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.expr.simple.LiteralExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.expr.subquery.SubqueryExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.expr.subquery.SubquerySegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.item.*;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.order.GroupBySegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.pagination.top.TopProjectionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.AndPredicate;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.PredicateSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.WhereSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.value.*;
import org.apache.shardingsphere.sql.parser.sql.segment.generic.table.SimpleTableSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.generic.table.SubqueryTableSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.generic.table.TableSegment;
import org.apache.shardingsphere.sql.parser.sql.statement.ddl.AlterTableStatement;
import org.apache.shardingsphere.sql.parser.sql.statement.ddl.CreateIndexStatement;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement;

import java.sql.*;
import java.util.*;

public class ShardingsphereParser {

    private final String originalSql;
    private final Map<Integer, Integer> variants = new HashMap<>();
    private final List<Integer> likeIndexes = new ArrayList<>();

    private final String defaultSchema;
    private final Connection connection;

    public ShardingsphereParser(String sql, String defaultOwner) {
        this.originalSql = indexOfLike(sql);
        this.defaultSchema = defaultOwner;
        this.connection = null;
    }

    public String getOriginalSql() {
        String temp = originalSql;
        if (!likeIndexes.isEmpty()) {
            for (int i = likeIndexes.size() - 1; i >= 0; i--) {
                int index = likeIndexes.get(i);
                temp = temp.substring(0, index) + "like" + temp.substring(index + 1);
            }
        }
        return temp;
    }

    public String getVariantSql() {
        String originalSql = getOriginalSql();
        String variantSql = originalSql;
        Integer[] keys = new Integer[variants.size()];
        QuickSort.sort(variants.keySet().toArray(keys));
        int diff = 0;
        int j = 0;
        for (int i = 0; i < keys.length; i++) {
            int start = keys[i] + j * 3;
            int end = variants.get(keys[i]) + j * 3;
            for (int k = j; k < likeIndexes.size(); k++) {
                int index = likeIndexes.get(k);
                if (index < start) {
                    j = (k + 1);
                    start += 3;
                    end += 3;
                    break;
                }
            }
            if (i == 0) {
                variantSql = originalSql.substring(0, start) + "?" + originalSql.substring(end + 1);
            } else {
                variantSql = variantSql.substring(0, (start - diff)) + "?" + variantSql.substring(end - diff + 1);
            }
            diff += end - start;
        }
        return variantSql;
    }

    //like expression discard so change like to '=' and
    private String indexOfLike(String sql) {
        String temp = sql.toLowerCase();
        int index = temp.indexOf("like");
        if (index > 0) {
            likeIndexes.add(index);
            sql = sql.substring(0, index) + "=" + sql.substring(index + 4);
            return indexOfLike(sql);
        } else {
            return sql;
        }
    }

    private void alterTable(SQLParserEngine engine) {
        AlterTableStatement statement = (AlterTableStatement) engine.parse(originalSql, false);
        SimpleTableSegment tableSegment = statement.getTable();
        StringBuilder builder = new StringBuilder("alterTable==>").append(parseSimpleTable(tableSegment));
//        List<String> cols = new ArrayList<>();
//        for (AddColumnDefinitionSegment addColumnDefinitionSegment : statement.getAddColumnDefinitions()) {
//            for (ColumnDefinitionSegment columnDefinition : addColumnDefinitionSegment.getColumnDefinitions()) {
//                cols.add(columnDefinition.getColumnName().getQualifiedName());
//            }
//        }
//        for (ConstraintDefinitionSegment addConstraintDefinition : statement.getAddConstraintDefinitions()) {
//            for (ColumnSegment primaryKeyColumn : addConstraintDefinition.getPrimaryKeyColumns()) {
//                cols.add(primaryKeyColumn.getQualifiedName());
//            }
//        }
//        for (DropColumnDefinitionSegment dropColumnDefinition : statement.getDropColumnDefinitions()) {
//            for (ColumnSegment column : dropColumnDefinition.getColumns()) {
//                cols.add(column.getQualifiedName());
//            }
//        }
//        for (ModifyColumnDefinitionSegment modifyColumnDefinition : statement.getModifyColumnDefinitions()) {
//            cols.add(modifyColumnDefinition.getColumnDefinition().getColumnName().getQualifiedName());
//        }
//        builder.append("cols[");
//        for (int i = 0; i < cols.size(); i++) {
//            builder.append(cols.get(i));
//            if (i != cols.size() - 1) {
//                builder.append(",");
//            }
//        }
//        builder.append("]");
        System.out.println(builder.toString());
    }

    private TableInfo parseSimpleTable(SimpleTableSegment tableSegment) {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setName(tableSegment.getTableName().getIdentifier().getValue());
        if (tableSegment.getOwner().isPresent()) {
            tableInfo.setOwner(tableSegment.getOwner().get().getIdentifier().getValue());
        } else {
            tableInfo.setOwner(defaultSchema);
        }
        if (tableSegment.getAlias().isPresent()) {
            tableInfo.setAlias(tableSegment.getAlias().get());
        }
        return tableInfo;
    }

    private void createIndex(SQLParserEngine engine) {
        StringBuilder builder = new StringBuilder("createIndex==>");
        CreateIndexStatement statement = (CreateIndexStatement) engine.parse(originalSql, false);
        builder.append(parseSimpleTable(statement.getTable()));
//        IndexSegment indexSegment = statement.getIndex();
//        if (indexSegment != null) {
//            builder.append(",index[").append(indexSegment.getIdentifier().getValue()).append("]");
//        }
        System.out.println(builder.toString());
    }

    private void parseTableFactor(TableFactorSegment tableFactorSegment, List<TableInfo> tableList) {
        Collection<ColumnSegment> columnSegments = tableFactorSegment.getColumns();
        if (!columnSegments.isEmpty()) throw new RuntimeException("this need todo");
        if (!tableFactorSegment.getTableReferences().isEmpty()) throw new RuntimeException("this need todo");
//        Collection<TableReferenceSegment> tableReferenceSegments = tableFactorSegment.getTableReferences();
//        for (TableReferenceSegment tableReferenceSegment : tableReferenceSegments) {
//            parseTableReferenceSegment(tableReferenceSegment, tableList);
//        }
        TableSegment tableSegment = tableFactorSegment.getTable();
        if (tableSegment instanceof SimpleTableSegment) {
            tableList.add(parseSimpleTable((SimpleTableSegment) tableSegment));
        } else {
            SubqueryTableSegment subqueryTableSegment = (SubqueryTableSegment) tableSegment;
            TableInfo tableInfo = null;
            if (subqueryTableSegment.getAlias().isPresent()) {
                tableInfo = new TableInfo(true);
                tableInfo.setOwner(defaultSchema);
                tableInfo.setName(subqueryTableSegment.getAlias().get());
                tableList.add(tableInfo);
            }
            SubquerySegment subquerySegment = subqueryTableSegment.getSubquery();
            List<ColumnInfo> cols = parseSelectStatement(subquerySegment.getSelect(), tableList);
            if (tableInfo != null) {
                tableInfo.fillSelect(cols);
            }
        }
    }

    private void parseJoinSpecificationSegment(JoinSpecificationSegment joinSpecificationSegment, List<TableInfo> tableInfoList) {
        PredicateSegment predicateSegment = joinSpecificationSegment.getPredicateSegment();
        ColumnInfo leftCol = parseColumnSegment(predicateSegment.getColumn(), null);
        int index = matchTable(tableInfoList, leftCol);
        if (index < 0) {
            throw new RuntimeException(leftCol + " can't find table");
        } else {
            tableInfoList.get(index).fillColumn(leftCol);
        }
        PredicateRightValue rightValue = predicateSegment.getRightValue();
        if (rightValue instanceof ColumnSegment) {
            ColumnInfo rightCol = parseColumnSegment((ColumnSegment) rightValue, null);
            int i = matchTable(tableInfoList, rightCol);
            if (i < 0) {
                throw new RuntimeException(rightCol + " can't find table");
            } else {
                tableInfoList.get(i).fillColumn(rightCol);
            }
        } else {
            throw new RuntimeException("JoinSpecificationSegment rightValue[" + rightValue.getClass().getName() + " is not support");
        }
        Collection<ColumnSegment> columnSegments = joinSpecificationSegment.getUsingColumns();
        if (!columnSegments.isEmpty()) {
            throw new RuntimeException("JoinSpecificationSegment UsingColumns is not support");
        }
    }

    private Optional<ColumnInfo> parseProjectionSegment(ProjectionSegment projectionSegment, List<TableInfo> list) {
        ColumnInfo columnInfo = null;
        if (projectionSegment instanceof ShorthandProjectionSegment) {
            columnInfo = new ColumnInfo();
            ShorthandProjectionSegment shorthandProjectionSegment = (ShorthandProjectionSegment) projectionSegment;
            columnInfo.setName("*");
            if (shorthandProjectionSegment.getOwner().isPresent()) {
                columnInfo.setOwner(shorthandProjectionSegment.getOwner().get().getIdentifier().getValue());
            }
        } else if (projectionSegment instanceof ColumnProjectionSegment) {
            ColumnProjectionSegment columnProjectionSegment = (ColumnProjectionSegment) projectionSegment;
            columnInfo = parseColumnSegment(columnProjectionSegment.getColumn(), columnProjectionSegment.getAlias().orElse(null));
        } else if (projectionSegment instanceof AggregationProjectionSegment) {
            AggregationProjectionSegment aggregationDistinctProjectionSegment = (AggregationProjectionSegment) projectionSegment;
            throw new RuntimeException(aggregationDistinctProjectionSegment + " need to support");
        } else if (projectionSegment instanceof ExpressionProjectionSegment) {
            ExpressionProjectionSegment expressionProjectionSegment = (ExpressionProjectionSegment) projectionSegment;
            throw new RuntimeException(expressionProjectionSegment + " need to support");
        } else if (projectionSegment instanceof SubqueryProjectionSegment) {
            SubqueryProjectionSegment subqueryProjectionSegment = (SubqueryProjectionSegment) projectionSegment;
            List<ColumnInfo> cols = parseSelectStatement(subqueryProjectionSegment.getSubquery().getSelect(), list);
            assert cols.size() == 1;
            if (subqueryProjectionSegment.getAlias().isPresent()) {
                columnInfo = cols.get(0);
                columnInfo.setAlias(subqueryProjectionSegment.getAlias().get());
            }
        } else if (projectionSegment instanceof TopProjectionSegment) {
            TopProjectionSegment topProjectionSegment = (TopProjectionSegment) projectionSegment;
            throw new RuntimeException(topProjectionSegment + " need to support");
        } else throw new RuntimeException(projectionSegment.getClass() + " is not support");
        return Optional.ofNullable(columnInfo);
    }

    private ColumnInfo parseColumnSegment(ColumnSegment columnSegment, String alias) {
        ColumnInfo columnInfo = new ColumnInfo();
        if (columnSegment.getOwner().isPresent()) {
            columnInfo.setOwner(columnSegment.getOwner().get().getIdentifier().getValue());
        }
        columnInfo.setName(columnSegment.getIdentifier().getValue());
        columnInfo.setAlias(alias);
        return columnInfo;
    }

    private void parseTableReferenceSegment(TableReferenceSegment tableReferenceSegment, List<TableInfo> tableList) {
        parseTableFactor(tableReferenceSegment.getTableFactor(), tableList);
        Collection<JoinedTableSegment> joinedTableSegments = tableReferenceSegment.getJoinedTables();
        for (JoinedTableSegment joinedTableSegment : joinedTableSegments) {
            parseTableFactor(joinedTableSegment.getTableFactor(), tableList);
            parseJoinSpecificationSegment(joinedTableSegment.getJoinSpecification(), tableList);
        }
    }

    private List<ColumnInfo> parseSelectStatement(SelectStatement selectStatement, List<TableInfo> tableList) {
        List<TableInfo> tempList = new ArrayList<>();
        Collection<TableReferenceSegment> tableReferenceSegments = selectStatement.getTableReferences();
        for (TableReferenceSegment tableReferenceSegment : tableReferenceSegments) {
            parseTableReferenceSegment(tableReferenceSegment, tempList);
        }

        assert !tempList.isEmpty();
        List<ColumnInfo> list = new ArrayList<>();
        ProjectionsSegment projectionsSegment = selectStatement.getProjections();
        Collection<ProjectionSegment> projectionSegments = projectionsSegment.getProjections();
        for (ProjectionSegment projectionSegment : projectionSegments) {
            Optional<ColumnInfo> columnInfo = parseProjectionSegment(projectionSegment, tableList);
            if (columnInfo.isPresent()) {
                list.add(columnInfo.get());
                int index = matchTable(tempList, columnInfo.get());
                if (index < 0) {
                    throw new RuntimeException(columnInfo + " can't find table");
                } else {
                    ColumnInfo colInfo = columnInfo.get();
                    TableInfo tableInfo = tempList.get(index);
                    if (!("*".equals(colInfo.getName()) && tableInfo.isVirtual())) {
                        tableInfo.fillSelect(colInfo);
                    }
                }
            }
        }
        if (selectStatement.getWhere().isPresent()) {
            WhereSegment whereSegment = selectStatement.getWhere().get();
            Collection<AndPredicate> andPredicates = whereSegment.getAndPredicates();
            for (AndPredicate andPredicate : andPredicates) {
                Collection<PredicateSegment> predicateSegments = andPredicate.getPredicates();
                for (PredicateSegment predicateSegment : predicateSegments) {
                    ColumnInfo leftCol = parseColumnSegment(predicateSegment.getColumn(), null);
                    int index = matchTable(tempList, leftCol);
                    if (index < 0) {
                        throw new RuntimeException(leftCol + " can't find table");
                    } else {
                        tempList.get(index).fillColumn(leftCol);
                    }
                    PredicateRightValue rightValue = predicateSegment.getRightValue();
                    if (rightValue instanceof ColumnSegment) {
                        ColumnInfo rightCol = parseColumnSegment((ColumnSegment) rightValue, null);
                        int i = matchTable(tempList, rightCol);
                        if (i < 0) {
                            throw new RuntimeException(rightCol + " can't find table");
                        } else {
                            tempList.get(i).fillColumn(rightCol);
                        }
                    } else if (rightValue instanceof PredicateBetweenRightValue) {
                        throw new RuntimeException(rightValue + " need to support");
                    } else if (rightValue instanceof PredicateBracketValue) {
                        throw new RuntimeException(rightValue + " need to support");
                    } else if (rightValue instanceof PredicateCompareRightValue) {
                        PredicateCompareRightValue compareRightValue = (PredicateCompareRightValue) rightValue;
                        ExpressionSegment expressionSegment = compareRightValue.getExpression();
                        if (expressionSegment instanceof LiteralExpressionSegment) {
                            LiteralExpressionSegment literalExpressionSegment = (LiteralExpressionSegment) expressionSegment;
                            variants.put(literalExpressionSegment.getStartIndex(), literalExpressionSegment.getStopIndex());
                        } else {
                            throw new RuntimeException(rightValue + " need to support");
                        }
                    } else if (rightValue instanceof PredicateInRightValue) {
                        PredicateInRightValue predicateInRightValue = (PredicateInRightValue) rightValue;
                        //predicateInRightValue.getPredicateBracketValue(); ignore
                        Collection<ExpressionSegment> expressionSegments = predicateInRightValue.getSqlExpressions();
                        for (ExpressionSegment expressionSegment : expressionSegments) {
                            if (expressionSegment instanceof SubqueryExpressionSegment) {
                                parseSelectStatement(((SubqueryExpressionSegment) expressionSegment).getSubquery().getSelect(), tableList);
                            } else {
                                throw new RuntimeException(expressionSegment + " need to support");
                            }
                        }
                    } else if (rightValue instanceof SubquerySegment) {
                        SubquerySegment subquerySegment = (SubquerySegment) rightValue;
                        parseSelectStatement(subquerySegment.getSelect(), tableList);
//                    } else if (rightValue instanceof PredicateLeftBracketValue ||
//                            rightValue instanceof PredicateRightBracketValue) { //"(", ")"
                    }
                }
            }
        }

        if (selectStatement.getGroupBy().isPresent()) {
            GroupBySegment groupBySegment = selectStatement.getGroupBy().get();
        }

        if (selectStatement.getOrderBy().isPresent()) {
        }

        TableInfo.merge(tableList, tempList);
        return list;
    }

    private int matchTable(List<TableInfo> tableInfoList, ColumnInfo columnInfo) {
        if (tableInfoList.size() == 1) {
            return 0;
        }

        if (columnInfo.getOwner().isPresent()) {
            for (int i = 0; i < tableInfoList.size(); i++) {
                TableInfo tableInfo = tableInfoList.get(i);
                String owner = columnInfo.getOwner().get();
                if (tableInfo.getName().equalsIgnoreCase(owner) || tableInfo.getAlias().equalsIgnoreCase(owner) ||
                        tableInfo.toSQLString().equalsIgnoreCase(owner)) {
                    return i;
                }
            }
        } else {
            if ("*".equals(columnInfo.getName()) && tableInfoList.size() > 1) {
                throw new RuntimeException("column '*' can not match multi table");
            }
            for (int i = 0; i < tableInfoList.size(); i++) {
                TableInfo tableInfo = tableInfoList.get(i);
                if (tableInfo.getColumns().isPresent()) {
                    for (ColumnInfo colInfo : tableInfo.getColumns().get()) {
                        if (colInfo.getName().equalsIgnoreCase(columnInfo.getName()) ||
                                colInfo.getAlias().equalsIgnoreCase(columnInfo.getName())) {
                            return i;
                        }
                    }
                }
            }
            for (int i = 0; i < tableInfoList.size(); i++) {
                TableInfo tableInfo = tableInfoList.get(i);
//                    if (connection == null) return -1;
                if (connection == null) return 0; //todo just fo test
                String sql = "select * from " + tableInfo.toSQLString() + " where 1=0";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int count = rsmd.getColumnCount();
                    for (int j = 1; j <= count; j++) {
                        String col = rsmd.getColumnName(j);
//                            if (JdbcConstants.HIVE.equals(connect.getDbType())) { //table.col
//                                col = col.substring(col.indexOf(".") + 1);
//                            }
                        ColumnInfo colInfo = new ColumnInfo();
                        colInfo.setOwner(tableInfo.toSQLString());
                        colInfo.setName(col);
                        tableInfo.fillColumn(colInfo);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (tableInfo.getColumns().isPresent()) {
                    for (ColumnInfo colInfo : tableInfo.getColumns().get()) {
                        if (colInfo.getName().equalsIgnoreCase(columnInfo.getName()) ||
                                colInfo.getAlias().equalsIgnoreCase(columnInfo.getName())) {
                            return i;
                        }
                    }
                }
            }
        }
        return -1;
    }

    private List<TableInfo> select(SQLParserEngine engine) {
        List<TableInfo> tableList = new ArrayList<>();
        SelectStatement statement = (SelectStatement) engine.parse(originalSql, false);
        parseSelectStatement(statement, tableList);
        System.out.println(getVariantSql());
        return tableList;
    }

    /**
     * databaseTypeName:MySQL,Oracle,PostgreSQL,SQLServer,SQL92
     * <p>
     * not support:
     * 1. alter database
     * "alter database song1 default character set gbk";
     * 2. expression like xxx; (like转换成'＝')
     * "SELECT * FROM  book b where name LIKE '神%'"
     */
    public static void main(String[] args) {
        SQLParserEngine engine = SQLParserEngineFactory.getSQLParserEngine("PostgreSQL");
        String sql = "ALTER TABLE test.Persons ADD Birthday date";
        new ShardingsphereParser(sql, "abc").alterTable(engine);
        sql = "ALTER TABLE db.Persons DROP COLUMN Birthday";
        new ShardingsphereParser(sql, "abc").alterTable(engine);
        sql = "CREATE INDEX PersonIndex ON Person (LastName DESC)";
        new ShardingsphereParser(sql, "abc").createIndex(engine);
        sql = "CREATE INDEX PersonIndex ON Person (LastName,FirstName)";
        new ShardingsphereParser(sql, "abc").createIndex(engine);
        sql = "CREATE UNIQUE INDEX PersonIndex ON db.Person (LastName,FirstName)";
        new ShardingsphereParser(sql, "abc").createIndex(engine);
        sql = "select t.*,h.FWXZ from (SELECT bgx.BDCDYH, cq.BDCQZH   from  gtj_bdcdyqlgx bgx ,gtj_zd_shiyq zd,gtj_djfz_cqzs cq where bgx.zdzhbsm=zd.bsm and bgx.zdzhqlbsm= cq.qlbsm and cq.qlbm='JSYDSYQ' and bgx.BDCDYH='\"+BDCDYH+\"') T left join gtj_h h on t.BDCDYH=h.BDCDYH";
        List<TableInfo> tableInfoList = new ShardingsphereParser(sql, "abc").select(engine);
        System.out.println(Arrays.toString(tableInfoList.toArray()));
        sql = "select id,name from test where (col2>3 or date>'2019-02-28T09:43:10.224000') and col4='北京'";
        tableInfoList = new ShardingsphereParser(sql, "abc").select(engine);
        System.out.println(Arrays.toString(tableInfoList.toArray()));
        sql = "SELECT id,name,time FROM table1 WHERE id2 IN (SELECT id3 FROM table2 WHERE name2 ='lili')";
        tableInfoList = new ShardingsphereParser(sql, "abc").select(engine);
        System.out.println(Arrays.toString(tableInfoList.toArray()));
        sql = "select applications.applicantId, lid, l.aid, h.hid, zjhm, xm, hname, roomId, r.rid, l.rent " +
                "from leases l, applications, applicants, houses h, rooms r where l.hid = h.hid and l.rid = r.rid " +
                "and l.aid = applications.aid and applications.applicantId = applicants.applicantId";
        tableInfoList = new ShardingsphereParser(sql, "abc").select(engine);
        System.out.println(Arrays.toString(tableInfoList.toArray()));
        sql = "SELECT * FROM  book b where name LIKE '神%'";
        tableInfoList = new ShardingsphereParser(sql, "abc").select(engine);
        System.out.println(Arrays.toString(tableInfoList.toArray()));
        sql = "SELECT * FROM  book b where name LIKE '神%' and country like '?CN'";
        tableInfoList = new ShardingsphereParser(sql, "abc").select(engine);
        System.out.println(Arrays.toString(tableInfoList.toArray()));
        sql = "select id,name from test.dbo.user where user_name=? and password=?";
        tableInfoList = new ShardingsphereParser(sql, "abc").select(engine);
        System.out.println(Arrays.toString(tableInfoList.toArray()));
    }
//
//    public void testCreateTable() throws Exception {
//        String sql = "CREATE TABLE Persons (Id_P int,LastName varchar(255),FirstName varchar(255)," +
//                "Address varchar(255),City varchar(255))";
//        sql = "CREATE TABLE Persons AS SELECT id, address, city, state, zip FROM companies WHERE id1> 1000";
//        sql = "CREATE TABLE Persons AS (SELECT id, address, city, state, zip FROM companies WHERE id1> 1000)";
//        sql = "CREATE TABLE Persons AS (SELECT id, address FROM (select id, name, address from db.companies WHERE id1 > 1000))";
//        auditEvent = new SQLEvent.Builder(sql);
//        SQLHandler sqlHandler = new SQLHandler(wrapConnect, sql, auditEvent);
//        sqlHandler.handler();
//        List<SQLHandler.ColEntry> cols = sqlHandler.getCols();
//        assertEquals(1, cols.size());
//    }
//
//    @Test
//    public void testCreateView() throws Exception {
//        String sql = "CREATE VIEW ProductsView AS SELECT ProductName,UnitPrice FROM Products " +
//                "WHERE UnitPrice>(SELECT AVG(UnitPrice) FROM Products)";
//        sql = "CREATE VIEW ProductsView AS SELECT ProductName,UnitPrice FROM Products " +
//                "WHERE UnitPrice1>(SELECT AVG(UnitPrice) FROM Products1)";
//        sql = "CREATE TABLE user_info (" +
//                "  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键'," +
//                "  `user_id` bigint(11) NOT NULL COMMENT '用户id'," +
//                "  `username` varchar(45) NOT NULL COMMENT '真实姓名'," +
//                "  `email` varchar(30) NOT NULL COMMENT '用户邮箱'," +
//                "  `nickname` varchar(45) NOT NULL COMMENT '昵称'," +
//                "  `birthday` date NOT NULL COMMENT '生日'," +
//                "  `sex` tinyint(4) DEFAULT '0' COMMENT '性别'," +
//                "  `short_introduce` varchar(150) DEFAULT NULL COMMENT '一句话介绍自己，最多50个汉字'," +
//                "  `user_resume` varchar(300) NOT NULL COMMENT '用户提交的简历存放地址'," +
//                "  `user_register_ip` int NOT NULL COMMENT '用户注册时的源ip'," +
//                "  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
//                "  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'," +
//                "  `user_review_status` tinyint NOT NULL COMMENT '用户资料审核状态，1为通过，2为审核中，3为未通过，4为还未提交审核'," +
//                "  PRIMARY KEY (`id`)," +
//                "  UNIQUE KEY `uniq_user_id` (`user_id`)," +
//                "  KEY `idx_username`(`username`)," +
//                "  KEY `idx_create_time_status`(`create_time`,`user_review_status`)" +
//                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='网站用户基本信息'";
//        SQLHandler sqlHandler = new SQLHandler(wrapConnect, sql, auditEvent);
//        sqlHandler.handler();
//        List<SQLHandler.ColEntry> cols = sqlHandler.getCols();
//        assertEquals(1, cols.size());
//    }
//
//    @Test
//    public void testDelete() throws Exception {
//        String sql = "DELETE FROM Person WHERE LastName = 'Wilson' ";
//        sql = "delete from Person where S_date not in " +
//                "(select e2.maxdt from" +
//                "(select Order_Id as oid,Product_Id,Amt,MAX(S_date) as maxdt from Exam" +
//                " group by Order_Id,Product_Id,Amt) as e2)";
//        auditEvent = new SQLEvent.Builder(sql);
//        SQLHandler sqlHandler = new SQLHandler(wrapConnect, sql, auditEvent);
//        sqlHandler.handler();
//        List<SQLHandler.ColEntry> cols = sqlHandler.getCols();
//        assertEquals(1, cols.size());
//    }
//
//    @Test
//    public void testDrop() throws Exception {
//        String sql = "DROP TABLE Customer";
//        sql = "DROP TABLE Customer,d2.producer";
//        auditEvent = new SQLEvent.Builder(sql);
//        SQLHandler sqlHandler = new SQLHandler(wrapConnect, sql, auditEvent);
//        sqlHandler.handler();
//        List<SQLHandler.ColEntry> cols = sqlHandler.getCols();
//        assertEquals(1, cols.size());
//    }
//
//    @Test
//    public void testInsert() throws Exception {
//        String sql = "INSERT INTO Store_Information (Store_Name, Sales, Txn_Date) VALUES ('Los Angeles', 900, 'Jan-10-1999')";
//        sql = "INSERT INTO Store_Information (Store_Name, Sales, Txn_Date) " +
//                "SELECT store_name, sales, txn_Date FROM Sales_Information " +
//                "WHERE Year(Txn_Date1) = 1998";
//        auditEvent = new SQLEvent.Builder(sql);
//        SQLHandler sqlHandler = new SQLHandler(wrapConnect, sql, auditEvent);
//        sqlHandler.handler();
//        List<SQLHandler.ColEntry> cols = sqlHandler.getCols();
//        assertEquals(1, cols.size());
//    }
//
//    @Test
//    public void testTruncate() throws Exception {
//        String sql = "TRUNCATE TABLE Customer";
//        auditEvent = new SQLEvent.Builder(sql);
//        SQLHandler sqlHandler = new SQLHandler(wrapConnect, sql, auditEvent);
//        sqlHandler.handler();
//        List<SQLHandler.ColEntry> cols = sqlHandler.getCols();
//        assertEquals(1, cols.size());
//    }
//
//    @Test
//    public void testUpdate() throws Exception {
//        String sql = "UPDATE Store_Information SET Sales = 500 WHERE Store_Name = 'Los Angeles' " +
//                "AND Txn_Date = 'Jan-08-1999';";
//        sql = "update Store_Information set shop_money=(select shop_money from build_info2 where build_info2.id=Store_Information.id)" +
//                " where Store_Information.user = build_info2.user and Store_Information.user = 'test3'";
//        auditEvent = new SQLEvent.Builder(sql);
//        SQLHandler sqlHandler = new SQLHandler(wrapConnect, sql, auditEvent);
//        sqlHandler.handler();
//        List<SQLHandler.ColEntry> cols = sqlHandler.getCols();
//        assertEquals(1, cols.size());
//    }
//
//    @Test
//    public void testSelect() throws Exception {

//
//        sql = "SELECT RID, ROOMID, HOUSEID, LIVESIZE, RENT  , UPTIME FROM unimas.aa.rooms";
//        sql = "select 1";
//        sql = "select rid,count(*) from unimas.rooms group by rid";
//        sql = "select NAME,sex,count(*) as x  from unimas.reader_info " +
//                "where NAME  like '%赵%'  or NAME  like '%李%' or  NAME  like '%张%' AND " +
//                "date_format((str_to_date(time,'%Y-%m-%d')),'%Y-%m')=(SELECT MAX(date_format((str_to_date(time,'%Y-%m-%d')),'%Y-%m')) from unimas.reader_info) " +
//                "group by NAME,sex";
//        sql = "select  case left(zjhm,2) " +
//                "when '11' then '北京市' " +
//                "when '12' then '天津市' " +
//                "when '13' then '河北省' " +
//                "when '14' then '山西省' " +
//                "when '15' then '内蒙古自治区' " +
//                "when '21' then '辽宁省' " +
//                "when '22' then '吉林省' " +
//                "when '23' then '黑龙江省' " +
//                "when '31' then '上海市' " +
//                "when '32' then '江苏省' " +
//                "when '33' then '浙江省' " +
//                "when '34' then '安徽省' " +
//                "when '35' then '福建省' " +
//                "when '36' then '江西省' " +
//                "when '37' then '山东省' " +
//                "when '41' then '河南省' " +
//                "when '42' then '湖北省' " +
//                "when '43' then '湖南省' " +
//                "when '44' then '广东省' " +
//                "when '45' then '广西壮族自治区' " +
//                "when '46' then '海南省' " +
//                "when '50' then '重庆市' " +
//                "when '51' then '四川省' " +
//                "when '52' then '贵州省' " +
//                "when '53' then '云南省' " +
//                "when '54' then '西藏自治区' " +
//                "when '61' then '陕西省' " +
//                "when '62' then '甘肃省' " +
//                "when '63' then '青海省' " +
//                "when '64' then '宁夏回族自治区' " +
//                "when '65' then '新疆维吾尔自治区' " +
//                "when '71' then '台湾省' " +
//                "when '81' then '香港特别行政区' " +
//                "when '82' then '澳门特别行政区' " +
//                "else '未知' " +
//                "end as 省份, " +
//                "    count(*) as sum " +
//                " FROM unimas.reader_info  group by 省份 order by sum";
//        sql = "select count(*) from mh_xm where (cp_state in (\"项目启动\",\"项目评审\",\"项目验收\",\"项目结束\")) and name like \"*合众等保测试*\" and cp_org like \"*启明*\" and zr_name like \"*刘*\" ";
//        sql = "select vuln_id vuln_id1,DATE_FORMAT(gmt_create,'%Y-%m-%d %H:%i:%S') gmt_create from vuln where id in (select a.id from (select max(id) id from vuln group by vuln_id order by id) a)";
//        sql = "select count(*) from tb_event where event_param != '通告'  and tongbao != 90 and gmt_create>'2019-02-23 14:14:41' union select count(*) from tb_vuln where  tongbao != 90 and gmt_create>'2019-02-23 14:14:41'";
//        sql = "select count(*) from (select DATE_FORMAT(gmt_modified,'%Y-%m-%d %H:%i:%S') gmt_modified from vuln where level=\"低\" and end_time is NULL) a where a.gmt_modified <\"2017-11-23 11:59:25\"";
//        sql = "select a.id from (select max(id) id from vuln group by vuln_id order by id) a";
//        sql = "select c.id,coalesce(c.orgName,\"其他机构\") as orgName,coalesce(d.name,\"无\") as " +
//                "name from (select a.id as Orgid,a.orgName,coalesce(b.pid,-1) as pid,b.id from mechanism " +
//                "as a right JOIN personnel_institutional_relations as b on a.id=b.Orgid order by id asc) c " +
//                "LEFT JOIN technical_staff d on c.pid=d.id";
//        sql = "select a.checkid,a.id,a.name,a.cpjg,a.checkres,a.gmt_create,a.zgfk from " +
//                "(select * from key_unit_check_gl_add) a, " +
//                "(select name from key_unit_check_gl_add where id=1) b where a.name=b.name";
//        sql = "select * from (select now,country,company,takings,account  from world2015 where  last=-1 union all " +
//                "select now,country,company,takings,account  from world2016 where  last=-1 union all " +
//                "select now,country,company,takings,account  from world2017 where  last=-1 union all " +
//                "select now,country,company,takings,account  from world2018 where  last=-1 union all " +
//                "select now,country,company,takings,account  from world2019 where  last=-1) as news ORDER by now";
//        sql = "select a.country,count(*) as count from (select * from (select now,country,company,takings,account  from world2015 where  last=-1 union all\n" +
//                "select now,country,company,takings,account  from world2016 where  last=-1 union all " +
//                "select now,country,company,takings,account  from world2017 where  last=-1 union all " +
//                "select now,country,company,takings,account  from world2018 where  last=-1 union all " +
//                "select now,country,company,takings,account  from world2019 where  last=-1) as news  ORDER by news.now) as a GROUP by a.country ORDER by count desc";
//        sql = "select *,price*1.5 as el折后价 from product";
//        sql = "select sum(price) from product";
//        sql = "select name from baoxian where name !='' and name !='' limit 100";
//        sql = "SELECT  T.ZL, T.QLRMC, T.ZJZL, T.ZJH, T.QLRLB, T.DHHM, T.ZSBSM,  T.QLBM, T.BDCDYH, T.YT, T.GYQK, T.MJ, T.SZSJ, T.DYZT, T.CFZT, T.XZZT, T.DYBSM , C.BSM AS CFBSM, C.CFWH, C.CFQSSJ, C.CFJSSJ, TX.BSM as TXBSM  FROM ( select  Z.BSM, Z.ZL, R.QLRMC, R.ZJZL, R.ZJH, R.QLRLB, IFNULL(R.GDDH,R.YDDH) AS DHHM, R.ZSBSM, Z.QLBSM, Z.QLBM, Z.BDCDYH, Z.YT, Z.GYQK, Z.MJ, Z.SZSJ, B.DYZT, B.CFZT, B.XZZT, B.DYBSM  FROM  (select BSM, ZL, QLBSM, QLBM, BDCDYH, YT, GYQK, MJ, SZSJ from gtj_DJFZ_CQZS where BDCQZH='\"+BDCQZH+\"' )Z left join gtj_QLR R on Z.qlbsm=R.qlbsm and  R.QLRLB='1' left join gtj_BDCDYZT B on Z.BDCDYH=B.BDCDYH ) t LEFT JOIN  CFDJ_DYGX CX on T.QLBSM=CX.QLBSM LEFT JOIN gtj_CFDJ C ON CX.cfdjbsm=C.bsm LEFT JOIN GTJ_T_XMGX TX ON T.QLBSM =TX.YQLBSM";
//        sql = "SELECT T.BDCDJZMH, T.QLRMC,  T.ZJZL, T.ZJH, T.QLRLB, T.ZL, T.QLBSM, T.QLBM, T.BDCDYH,  C.BSM, C.CFWH, C.CFQSSJ, C.CFJSSJ, B.DYZT,  B.CFZT, B.XZZT,  TX.BSM AS TXBSM  FROM (SELECT R.QLRMC, R.ZJZL, R.ZJH, R.QLRLB, DJ.BDCDJZMH, DJ.ZL, DJ.QLBSM, DJ.QLBM, DJ.BDCDYH FROM DJFZ_DJZM DJ,GTJ_QLR R WHERE DJ.qlbsm=R.qlbsm AND DJ.BDCDJZMH ='\"+BDCDJZMH+\"') T LEFT JOIN CFDJ_DYGX CX on T.QLBSM=CX.QLBSM LEFT JOIN gtj_CFDJ C ON CX.cfdjbsm=C.bsm LEFT JOIN gtj_BDCDYZT B ON T.BDCDYH=B.BDCDYH LEFT JOIN GTJ_T_XMGX TX ON T.QLBSM =TX.YQLBSM";
//        sql = "SELECT T.QLRMC,T.ZJZL,T.ZJH, T.QLBSM,T.QLBM,T.BDCDYH,T.ZL, T.BDCQZH,cx.bsm from(select distinct R.QLRMC,R.ZJZL,R.ZJH,R.QLBSM,R.QLBM,Z.BDCDYH,Z.ZL,Z.BDCQZH FROM (SELECT QLRMC,ZJZL,ZJH,QLBSM,QLBM from gtj_QLR  where qlbm='FDCQ'  AND ZJZL='\"+ZJZL+\"' AND ZJH='\"+ZJH+\"') R ,gtj_DJFZ_CQZS Z WHERE Z.qlbsm=R.qlbsm) T left join  CFDJ_DYGX CX ON T.QLBSM=CX.QLBSM";
//        sql = "SELECT BDCDYH, BSM, ZL, ZDMJ, JZMJ, YT, SJYT, YSJYT, 'zd_shiyq' AS dybm   FROM  gtj_ZD_SHIYQ  WHERE djh = '\"+DJH+\"'";
//        sql = "SELECT T.*, B.DYBSM, B.DYBM FROM (SELECT H.ZRZBSM,  H.BDCDYH, H.ZL,  H.FWYT1, H.FWYT2, H.FWYT3, H.GHYT, H.SCJZMJ, H.SCTNJZMJ, H.SCFTJZMJ   FROM GTJ_H H WHERE BDCDYH_19='\"+ZDH+\"') T LEFT JOIN BDCDYZT B ON T.BDCDYH=B.BDCDYH";
//        sql = "SELECT T.*,TX.BSM AS TXBSM from (select  'ZD_SHIYQ' as dybm, ZD.BDCDJBSM,  ZD.BDCDYH  from gtj_t_zd zd where zd.dcbh='\"+DCBH+\"') T LEFT JOIN gtj_t_xmgx tx ON T.BDCDJBSM=TX.ydybsm";
//        sql = "select t.*,h.FWXZ from (SELECT bgx.BDCDYH, cq.BDCQZH   from  gtj_bdcdyqlgx bgx ,gtj_zd_shiyq zd,gtj_djfz_cqzs cq where bgx.zdzhbsm=zd.bsm and bgx.zdzhqlbsm= cq.qlbsm and cq.qlbm='JSYDSYQ' and bgx.BDCDYH='\"+BDCDYH+\"') T left join gtj_h h on t.BDCDYH=h.BDCDYH";
//        sql = "SELECT DYXX.* FROM ( \n" +
//                "SELECT GTDY.*, '' AS GFHTBH, \n" +
//                "( SELECT m.QLLXMC FROM gtj_t_lcdy m WHERE S.DJXL = m.lcdm) AS DJDLMC, \n" +
//                "( SELECT m.LCLX FROM gtj_t_lcdy m WHERE S.DJXL = m.lcdm) AS DJXLMC, \n" +
//                "concat(SF.SSJE, \"(\", SF.SSJEDX, \")\") AS SFJE, \n" +
//                "'抵押' AS rmk FROM ( SELECT\n" +
//                "\t\t\tD.BSM AS BSM_D, D.YWH, D.DYBDCLX AS DYBDCLX_D,\n" +
//                "\t\t\tIFNULL(D.BDBZQSE, D.ZGZQSE) AS DYJE_D,\n" +
//                "\t\t\tD.QLQX AS QLQX_D ,\n" +
//                "\t\t\tD.DJSJ AS DJSJ_D,\n" +
//                "\t\t\t(CASE D.QSZT WHEN '0' THEN '临时' WHEN '1' THEN '正常' WHEN '2' THEN '历史' ELSE '其他' END) AS QSZT_D,\n" +
//                "\t\t\tD.DYSX AS DYSX_D,\n" +
//                "\t\t\tR.BSM AS BSM_R,\n" +
//                "\t\t\tR.ywh AS YWH_R,\n" +
//                "\t\t\tR.qlrmc AS QLRMC_R,\n" +
//                "\t\t\tR.ZJZL AS IDNTY_CLS,\n" +
//                "\t\t\tR.zjh AS ZJH_R,\n" +
//                "\t\t\tR.QLRLX AS QLRLX_R,\n" +
//                "\t\t\tDYZ.QLR AS QLR_DYZ,\n" +
//                "\t\t\tDYZ.BDCDJZMH AS TQZ_DYZ,\n" +
//                "\t\t\tH.BSM AS BSM_H,\n" +
//                "\t\t\tH.ZL,\n" +
//                "\t\t\t(CASE H.FWLB WHEN 1 THEN '期房' WHEN 2 THEN '现房' END) AS FWLB,\n" +
//                "\t\t\t( SELECT X.code_name FROM code_fwxz X WHERE x.id = H.FWXZ) AS FWXZ,\n" +
//                "\t\t\tH.QXDM,\n" +
//                "\t\t\tIFNULL(H.SCJZMJ,\n" +
//                "\t\t\tH.YCJZMJ) AS MJ,\n" +
//                "\t\t\tH.BDCDYH,\n" +
//                "\t\t\tDJZM.BDCDJZMH\n" +
//                "\t\tFROM ( SELECT BSM,ywh,qlrmc,ZJZL,zjh,QLRLX,QLBSM FROM GTJ_QLR WHERE qlrmc = '陈忠海' AND zjh = '330324198910023691' AND QLRLB = '8')R,\n" +
//                "\t\t\tGTJ_DYAQ D,\n" +
//                "\t\t\tGTJ_djfz_djzm DYZ,\n" +
//                "\t\t\tGTJ_DYAQ_DYGX YGX,\n" +
//                "\t\t\tGTJ_H H,\n" +
//                "\t\t\tGTJ_DJFZ_DJZM DJZM\n" +
//                "\t\tWHERE\n" +
//                "\t\t\tD.BSM = R.QLBSM\n" +
//                "\t\t\tAND D.BSM = DYZ.QLBSM\n" +
//                "\t\t\tAND D.BSM = DJZM.QLBSM\n" +
//                "\t\t\tAND DYZ.QLBSM = YGX.DYAQBSM\n" +
//                "\t\t\tAND YGX.DYBSM = H.BSM) GTDY\n" +
//                "\tLEFT JOIN zzwk.gtj_zz_slsq S ON GTDY.YWH = S.YWH\n" +
//                "\tLEFT JOIN GTJ_SF SF ON GTDY.BSM_D = sf.qlbsm AND sf.qlbm = 'DYAQ') DYXX\n" +
//                "GROUP BY 1, 2, 3, 7, 9\n" +
//                "UNION ALL\n" +
//                "SELECT DYXX.* FROM ( \n" +
//                "SELECT GTDY.*, t.htbh AS GFHTBH, ( SELECT m.QLLXMC FROM gtj_t_lcdy m WHERE S.DJXL = m.lcdm) AS DJDLMC,\n" +
//                "\t\t( SELECT m.LCLX FROM gtj_t_lcdy m WHERE S.DJXL = m.lcdm) AS DJXLMC, concat(SF.SSJE, \"(\", SF.SSJEDX, \")\") AS SFJE,\n" +
//                "\t\t'预告抵押' AS rmk FROM\n" +
//                "\t\t( SELECT\n" +
//                "\t\t\tD.BSM AS BSM_D,\n" +
//                "\t\t\tD.YWH,\n" +
//                "\t\t\tDGX.BDCDYLX,\n" +
//                "\t\t\tD.QDJG AS QDJG_D,\n" +
//                "\t\t\tconcat(D.qlqssj, '至', D.qljssj) AS QLQX_D,\n" +
//                "\t\t\tD.DJSJ,\n" +
//                "\t\t\t(CASE D.QSZT WHEN '0' THEN '临时' WHEN '1' THEN '正常' WHEN '2' THEN '历史' ELSE '其他' END) AS QSZT_D,\n" +
//                "\t\t\tD.DYSX AS DYSX_D,\n" +
//                "\t\t\tR.BSM,\n" +
//                "\t\t\tR.YWH AS YWH_R,\n" +
//                "\t\t\tR.qlrmc,\n" +
//                "\t\t\tR.ZJZL,\n" +
//                "\t\t\tR.zjh,\n" +
//                "\t\t\tR.QLRLX,\n" +
//                "\t\t\tD.DYSX,\n" +
//                "\t\t\t'' AS real_prop_rgts_rgstrn_crt,\n" +
//                "\t\t\tH.BSM AS BSM_H,\n" +
//                "\t\t\tH.ZL,\n" +
//                "\t\t\t(CASE H.FWLB WHEN 1 THEN '期房' WHEN 2 THEN '现房' END) AS FWLB_H,\n" +
//                "\t\t\t( SELECT X.code_name FROM code_fwxz X WHERE x.id = H.FWXZ) AS FWXZ_H,\n" +
//                "\t\t\tH.QXDM,\n" +
//                "\t\t\tIFNULL(H.SCJZMJ,\n" +
//                "\t\t\tH.YCJZMJ) AS MJ ,\n" +
//                "\t\t\tH.BDCDYH,\n" +
//                "\t\t\tDJZM.BDCDJZMH\n" +
//                "\t\tFROM (\n" +
//                "\t\t\tSELECT BSM,YWH,qlrmc,ZJZL,zjh,QLRLX,qlbsm FROM GTJ_QLR WHERE qlrmc = '陈忠海' AND zjh = '330324198910023691' AND QLRLB = '8')R,\n" +
//                "\t\t\tYGDJ D,\n" +
//                "\t\t\tGTJ_ygdj_dygx DGX,\n" +
//                "\t\t\tGTJ_H H,\n" +
//                "\t\t\tGTJ_DJFZ_DJZM DJZM\n" +
//                "\t\tWHERE\n" +
//                "\t\t\tD.BSM = R.qlbsm\n" +
//                "\t\t\tAND D.YGDJZL IN(3, 4)\n" +
//                "\t\t\tAND D.BSM = DJZM.QLBSM\n" +
//                "\t\t\tAND DGX.ygdjbsm = D.bsm\n" +
//                "\t\t\tAND DGX.DYBSM = h.BSM ) GTDY\n" +
//                "\tLEFT JOIN zzwk.gtj_zz_slsq S ON GTDY.YWH = S.YWH\n" +
//                "\tLEFT JOIN YGDJ t ON GTDY.YWH = t.YWH AND t.ygdjzl = 1\n" +
//                "\tLEFT JOIN GTJ_SF SF ON GTDY.BSM_D = sf.qlbsm AND sf.qlbm = 'YGDJ') DYXX\n" +
//                "GROUP BY 1, 2, 3, 7, 9;";
//        sql = "select hasindexes,hasrules,`tablespace` from tt where tablename !='' limit 3";
//        sql = "select\n" +
//                "   student.sname,\n" +
//                "   course.cname,\n" +
//                "   sc.score\n" +
//                "from\n" +
//                "   student,\n" +
//                "   sc,\n" +
//                "   course ,\n" +
//                "   teacher\n" +
//                "where\n" +
//                "   student.s_id = sc.s_id\n" +
//                "   and sc.c_id = course.c_id\n" +
//                "   and sc.t_id = teacher.t_id\n" +
//                "   and teacher.tname = 'tname553'\n" +
//                "   and sc.score =(\n" +
//                "   select\n" +
//                "      max(score)\n" +
//                "   from\n" +
//                "      sc\n" +
//                "   where\n" +
//                "      sc.t_id = teacher.t_Id);";
//        auditEvent = new SQLEvent.Builder(sql);
//        SQLHandler sqlHandler = new SQLHandler(wrapConnect, sql, auditEvent);
//        sqlHandler.handler();
//        List<SQLHandler.ColEntry> cols = sqlHandler.getCols();
//        assertEquals(1, cols.size());
//    }
//
//    @Test
//    public void safeUD() {
//        String sql = "update Store_Information set shop_money=(select shop_money from build_info2 where build_info2.id=Store_Information.id)" +
//                " where Store_Information.user = build_info2.user and Store_Information.user = 'test3'";
//        sql = "delete from Person where S_date not in " +
//                "(select e2.maxdt from" +
//                "(select Order_Id as oid,Product_Id,Amt,MAX(S_date) as maxdt from Exam" +
//                " group by Order_Id,Product_Id,Amt) as e2)";
//        String _sql = sql.toLowerCase();
//        String newSql;
//        if (_sql.startsWith("update")) {
//            SQLStatementParser stmtParser = SQLParserUtils.createSQLStatementParser(sql, wrapConnect.getDbType());
//            SQLUpdateStatement stmt = (SQLUpdateStatement) stmtParser.parseStatementList().get(0);
//            int i = _sql.indexOf("update");
//            int j = _sql.indexOf("set");
//            newSql = "select count(*) from " + sql.substring(i + 6, j) + " where " + stmt.getWhere().toString();
//        } else {
//            int i = _sql.indexOf("delete");
//            newSql = "select count(*) " + sql.substring(i + 6);
//        }
//        System.out.println(newSql);
//    }
}
