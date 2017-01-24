package dao;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dialal14 on 23/01/17.
 */
public interface GeneriqueDao<T, PK extends Serializable> {

    T create(T t);

    T read(PK pk);

    List<T> list();

    T update(T t);

    void delete(PK t);

    List<T> find(String key,Object value);
}
