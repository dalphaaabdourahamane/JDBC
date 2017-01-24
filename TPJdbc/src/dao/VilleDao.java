package dao;

import entity.Electeur;
import entity.Ville;

import java.util.List;

/**
 * Created by dialal14 on 23/01/17.
 */
public interface VilleDao {

    Ville create(Ville t);

    Ville read(Long pk);

    Ville getBureauVote(Ville ville);

    List<Ville> list();

    Ville update(Ville t);

    void delete(Long t);

    List<Ville> find(String key, Object value);
}
