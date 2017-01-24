package dao.utils;
/**
 * Created by dialal14 on 23/01/17.
 */
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;/**
 * Created by dialal14 on 23/01/17.
 */

import org.apache.commons.beanutils.BeanUtils;
/**
 * Created by DIALLO on 10/12/2016.
 */
public class ResultSetMapper<T> {
    @SuppressWarnings("unchecked")
    public List<T> mapRersultSetToObject(ResultSet rs, Class outputClass) {
        List<T> outputList = null;
        try {
            if (rs != null) {
                if (outputClass.isAnnotationPresent(Entity.class)) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    Field[] fields = outputClass.getDeclaredFields();
                    while (rs.next()) {
                        T bean = (T) outputClass.newInstance();
                        for (int _iterator = 0; _iterator < rsmd
                                .getColumnCount(); _iterator++) {
                            String columnName = rsmd.getColumnName(_iterator + 1);
                            Object columnValue = rs.getObject(_iterator + 1);
                            for (Field field : fields) {
                                if (field.isAnnotationPresent(Column.class)) {
                                    Column column = field
                                            .getAnnotation(Column.class);
                                    if (column.name().equalsIgnoreCase(
                                            columnName)
                                            && columnValue != null) {
                                        BeanUtils.setProperty(bean, field
                                                .getName(), columnValue);
                                        break;
                                    }
                                }
                            }
                        }
                        if (outputList == null) {
                            outputList = new ArrayList();
                        }
                        outputList.add(bean);
                    }

                } else {
                    //  error
                }
            } else {
                return Collections.emptyList();
            }
        } catch (IllegalAccessException | SQLException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return outputList;
    }
}
