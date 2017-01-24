package dao;

import entity.Electeur;

import java.util.List;

/**
 * Created by dialal14 on 23/01/17.
 */
public interface ElecteurDao{

    Electeur create(Electeur t);

    Electeur read(Long id);

    Electeur get(Long id);

    Electeur getBureauVote(Electeur electeur);

    Electeur getVille(Electeur electeur);

    List<Electeur> list();

    Electeur update(Electeur t);

    void delete(Long id);

}
