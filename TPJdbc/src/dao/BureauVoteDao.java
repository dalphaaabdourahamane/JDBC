package dao;

import entity.BureauVote;
import entity.Ville;

import java.util.List;

/**
 * Created by dialal14 on 23/01/17.
 */
public interface BureauVoteDao{

    BureauVote create(BureauVote t);

    BureauVote read(Long pk,Long pq);

    List<BureauVote> list();

    BureauVote update(BureauVote t);

    void delete(Long pk,Long pq);

    List<BureauVote> find(String key, Object value);


}
