package com.dongxin.scm.record;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 对指定表的更新做拦截，记录履历
 *
 * @author Succy
 * create on 2021/3/16
 */
@Slf4j
@RequiredArgsConstructor
@Intercepts({@Signature(type = StatementHandler.class, method = "update", args = {Statement.class})})
public class DataUpdateInterceptor extends AbstractSqlParserHandler implements Interceptor {

    private final DataSource dataSource;

    private static final String HIS_TABLE_SUFFIX = "_HIS";

    @Value("${data.update-record.tables}")
    private Set<String> tableSet = new HashSet<>();


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Statement statement;
        Object firstArg = invocation.getArgs()[0];
        if (Proxy.isProxyClass(firstArg.getClass())) {
            statement = (Statement) SystemMetaObject.forObject(firstArg).getValue("h.statement");
        } else {
            statement = (Statement) firstArg;
        }
        MetaObject stmtMetaObj = SystemMetaObject.forObject(statement);
        try {
            statement = (Statement) stmtMetaObj.getValue("stmt.statement");
        } catch (Exception e) {
            // do nothing
        }
        if (stmtMetaObj.hasGetter("delegate")) {
            //Hikari
            try {
                statement = (Statement) stmtMetaObj.getValue("delegate");
            } catch (Exception ignored) {
                // ignored
            }
        }

        if (CollUtil.isNotEmpty(tableSet)) {
            tableSet = tableSet.stream().map(String::toUpperCase).collect(Collectors.toSet());
        }

        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        this.sqlParser(metaObject);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        try {
            // 更新
            if (SqlCommandType.UPDATE.equals(mappedStatement.getSqlCommandType())) {
                // 使用反射从mysql的statement中获取sql
                String asSql = ReflectUtil.invoke(statement, "asSql").toString();
                if (StrUtil.isNotBlank(asSql)) {
                    String sql = StrUtil.trim(asSql.toUpperCase());
                    if (sql.startsWith("UPDATE")) {
                        TableNameParser parser = new TableNameParser(sql);
                        String tableName = CollUtil.getFirst(parser.tables()).toUpperCase();
                        // 判断表是否命中
                        if (tableSet.contains(tableName)) {

                            String whereSql = StrUtil.subAfter(sql, "WHERE", true);
                            // 可能会有多条
                            List<Entity> entityList = Db.use(dataSource).query(StrUtil.format("SELECT * FROM {} WHERE {}", tableName, whereSql));

                            for (Entity entity : entityList) {
                                String json = JSONObject.toJSONString(entity, SerializerFeature.WriteDateUseDateFormat);
                                Entity entity1 = Entity.create(StrUtil.format("{}{}", tableName, HIS_TABLE_SUFFIX))
                                        .set("id", IdUtil.fastSimpleUUID())
                                        .set("create_time", new Date())
                                        .set("content", json);

                                Db.use(dataSource).insert(entity1);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // ignore it
            log.warn("record update history failure!", e);
        }

        return invocation.proceed();

    }
}

