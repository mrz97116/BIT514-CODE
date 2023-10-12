package com.dongxin.scm.test;

import cn.hutool.core.collection.CollUtil;
import lombok.SneakyThrows;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

/**
 * @author Succy
 * create on 2021/3/17
 */
public class DruidSqlParserTest {

    @Test
    @SneakyThrows
    public void testParseUpdate() {
        String sql = "UPDATE CM_CUSTOMER_PROFILE SET CREATE_BY = 'ADMIN', CREATE_TIME = '2020-12-08 19:04:37', UPDATE_BY = 'ADMIN', UPDATE_TIME = '2021-03-16 20:13:36.074', SYS_ORG_CODE = 'A04', COMPANY_NAME = '测试员', TAX_NO = '需方税号', ADDRESS = '北部湾生态监狱', MOBILE = '18771111112', STATUS = '1', TYPE = 'FORMAL', TENANT_ID = 0, FAX = '需方传真' WHERE TENANT_ID = 0 AND ID = '1336265417614520322'";

//        List<SQLStatement> sqlStatement = SQLUtils.parseStatements(sql, "mysql");
//        System.out.println(sqlStatement.size());
//        for (SQLStatement statement : sqlStatement) {
//            SQLUpdateStatement updateStatement = (SQLUpdateStatement) statement;
//            List<SQLUpdateSetItem> items = updateStatement.getItems();
//            System.out.println(items);
//        }


        CCJSqlParserManager pm = new CCJSqlParserManager();
        Statement statement = pm.parse(new StringReader(sql));
        if (statement instanceof Update) {
            Update update = (Update) statement;

            List<Column> columns = update.getColumns();
            List<Expression> expressions = update.getExpressions();

            String updateBy = "";
            if (CollUtil.isNotEmpty(columns)) {
                for (int i = 0; i < columns.size(); i++) {
                    Column column = columns.get(i);
                    if ("UPDATE_BY".equalsIgnoreCase(column.getColumnName())) {
                        Expression expression = expressions.get(i);
                        Object value = expression.getASTNode().jjtGetValue();
                        if(value instanceof StringValue) {
                            StringValue stringValue = (StringValue) value;
                            updateBy = stringValue.getValue();
                        }
//                        System.out.println(StrUtil.format("{}:{}", column.getColumnName(), expression.toString()));
                        break;
                    }
                }
            }
            System.out.println("updateBy: " + updateBy);
            System.out.println(columns);
        }
    }
}
