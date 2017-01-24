package dao.impl;

import dao.BureauVoteDao;
import dao.utils.ResultSetMapper;
import entity.Adresse;
import entity.BureauVote;
import entity.Electeur;
import entity.Ville;
import entity.utils.KeyBureauVote;
import main.ConfigConnection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

import static dao.impl.VilleDaoImpl.villeHashMap;
import static org.springframework.util.StringUtils.capitalize;

/**
 * Created by dialal14 on 23/01/17.
 */
public class BureauVoteDaoImpl implements BureauVoteDao {

    private Connection connection;
    /*
        Represente les object en memoire
     */
    private final Map<KeyBureauVote,BureauVote> bureauVoteMap = new HashMap<>();
    /*
    verifie si tou les objects sont en memoire, a true unique lorsqu'on appelle la fonction list :)
     */
    private static boolean isAllcharge =false;


    public BureauVoteDaoImpl() {
        try {
            connection = ConfigConnection.getConnection("../main/FichierConnexion.txt");
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BureauVote read(Long pk,Long pq) {
        if(bureauVoteMap.containsKey(new KeyBureauVote(pk,pq))){
            System.out.println("FIND IN MEMORY ");
            return bureauVoteMap.get(new KeyBureauVote(pk,pq));
        }

        PreparedStatement statement;
        StringBuilder query = new StringBuilder();
        BureauVote bureauVote = new BureauVote();
        try {
            query.append("SELECT  * FROM ")
                    .append(BureauVote.class.getSimpleName().toUpperCase()).append(" WHERE BID  = ? AND VID = ?");
            statement = connection.prepareStatement(query.toString());
            statement.setLong(1, pk);
            statement.setLong(2, pq);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                //Retrieve by column name
                bureauVote.setBID(resultSet.getLong("bid"));
                bureauVote.setVille(new VilleDaoImpl().read(resultSet.getLong("vid")));
                bureauVote.setNom(resultSet.getString("nom"));
                bureauVote.getAdresse().setRue(resultSet.getString("rue"));
                bureauVote.getAdresse().setCp(resultSet.getString("cp"));
            }
            resultSet.close();

            bureauVoteMap.put(new KeyBureauVote(pk,pq),bureauVote);

            return bureauVote;

        } catch (SQLException e) {
            return null;
        }
    }

    @SuppressWarnings("Since15")
    @Override
    public BureauVote create(BureauVote bureauVote) {
        PreparedStatement statement;
        StringBuilder query = new StringBuilder();
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();

        query.append("SELECT * FROM BUREAUVOTE WHERE NOM = ?");
        try {
            statement = connection.prepareStatement(query.toString());
            statement.setString(1, bureauVote.getNom());
            ResultSet resultSet = statement.executeQuery();
            BureauVote bureauVoteExiste = new BureauVote();

            while(resultSet.next()){
                //Retrieve by column name
                bureauVoteExiste.setBID(resultSet.getLong("bid"));
                bureauVoteExiste.setVille(new VilleDaoImpl().read(resultSet.getLong("vid")));
                bureauVoteExiste.setNom(resultSet.getString("nom"));
                bureauVoteExiste.getAdresse().setRue(resultSet.getString("rue"));
                bureauVoteExiste.getAdresse().setCp(resultSet.getString("cp"));
            }
            resultSet.close();
            if(bureauVoteExiste.getBID() != null){
                Ville ville = (bureauVote.getVille().getVID() == null)? new VilleDaoImpl().create(bureauVote.getVille()) : bureauVote.getVille();
                bureauVote.setVille(ville);

                bureauVoteMap.put(new KeyBureauVote(bureauVote.getBID(),bureauVote.getVille().getVID()),bureauVote);

                return  bureauVoteExiste;
            }

        } catch (Exception ignored) {
        }

        try {
            query = new StringBuilder();
            query.append("INSERT INTO BureauVote ");

            if (BureauVote.class.isAnnotationPresent(Entity.class)) {
                // get all the attributes of outputClass

                Field[] fields = BureauVote.class.getDeclaredFields();
                Field field;
                // ID AUTO GENERATE i = 1 pour ne pas e prendre en compte
                for (int i = 1; i < fields.length; i++) {
                    field = fields[i];

                    if (field.isAnnotationPresent(Column.class)) {
                        Column column = field.getAnnotation(Column.class);
                        key.append(column.name()).append(",");

                        Object invoke = bureauVote.getClass().getDeclaredMethod("get" + capitalize(column.name().
                                toLowerCase())).invoke(bureauVote);
                        if(invoke instanceof Number || Objects.isNull(invoke)){
                            value.append(invoke).append(",");
                        }else {
                            value.append("'").append(invoke).append("',");
                        }
                    }
                }

            }

            Adresse adresse = bureauVote.getAdresse();
            key.append(" RUE, ");
            value.append("'").append(adresse.getRue()).append("',");
            key.append("CP");
            value.append("'").append(adresse.getCp()).append("'");
            Ville ville = (bureauVote.getVille().getVID() == null)? new VilleDaoImpl().create(bureauVote.getVille()) : bureauVote.getVille();
            bureauVote.setVille(ville);


            query.append(" ( BID,VID, ").append(key).append(" ) VALUES (").append(bureauVote.getBID()).append(", ").
                    append(ville.getVID()).append(",").append(value).append(" )");

            statement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            if(statement.executeUpdate() == 0) throw new SQLException("error");
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {

                    bureauVote.setBID(generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            bureauVoteMap.put(new KeyBureauVote(bureauVote.getBID(),bureauVote.getVille().getVID()),bureauVote);
            return bureauVote;

        } catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<BureauVote> list() {
        if(isAllcharge) {
            System.out.println("FIND IN MEMERY ");
            return new LinkedList<>(bureauVoteMap.values());
        }

        PreparedStatement statement;
        List<BureauVote> bureauVotes = new ArrayList<>();
        try {
            statement = connection.prepareStatement("SELECT * FROM " + BureauVote.class.getSimpleName());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                //Retrieve by column name
                BureauVote bureauVote = new BureauVote();

                bureauVote.setBID(resultSet.getLong("bid"));
                bureauVote.setVille(new VilleDaoImpl().read(resultSet.getLong("vid")));
                bureauVote.setNom(resultSet.getString("nom"));
                bureauVote.getAdresse().setRue(resultSet.getString("rue"));
                bureauVote.getAdresse().setCp(resultSet.getString("cp"));
                bureauVotes.add(bureauVote);
            }

            for (BureauVote bureauVote: bureauVotes) {
                bureauVoteMap.put(new KeyBureauVote(bureauVote.getBID(),bureauVote.getVille().getVID()),bureauVote);
                isAllcharge = true;
            }
            resultSet.close();
            return bureauVotes;

        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public BureauVote update(BureauVote t) {
        PreparedStatement statement;
        StringBuilder query = new StringBuilder();
        StringBuilder setUpdate = new StringBuilder();

        try {
            query.append("UPDATE ").append(BureauVote.class.getSimpleName()).append(" SET ");

            if (BureauVote.class.isAnnotationPresent(Entity.class)) {
                // get all the attributes of outputClass

                Field[] fields = BureauVote.class.getDeclaredFields();
                Field field;
                //i= 1 pour ne pas set ID
                for (int i = 1; i < fields.length; i++) {
                    field = fields[i];
                    if (field.isAnnotationPresent(Column.class)) {
                        Column column = field.getAnnotation(Column.class);
                        setUpdate.append(column.name() + " = '" + t.getClass().getDeclaredMethod("get"
                                + capitalize(column.name().toLowerCase())).invoke(t) + "',");

                    }
                }
                Adresse adresse = t.getAdresse();
                setUpdate.append(" RUE = '").append(adresse.getRue()).append("',");
                setUpdate.append(" RUE = '").append(adresse.getCp()).append("'");
            }
            query.append(setUpdate).append(" WHERE ").append(" BID = ").append(t.getBid()).append(" AND VID = ").append(t.getVille().getVID());
            statement = connection.prepareStatement(query.toString());

            statement.executeUpdate();

            bureauVoteMap.put(new KeyBureauVote(t.getBID(),t.getVille().getVID()),t);

            return t;

        } catch (SQLException | NoSuchMethodException |
                IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(Long pk,Long pq) {
        PreparedStatement statement;
        StringBuilder query = new StringBuilder();

        try {
            query.append("DELETE FROM ").append(BureauVote.class.getSimpleName().toUpperCase()).append(" WHERE BID = ?").append(" WHERE VID = ?");
            statement = connection.prepareStatement(query.toString());
            statement.setLong(1, pk);
            statement.setLong(2, pq);

            statement.executeUpdate();

            bureauVoteMap.remove(new KeyBureauVote(pk,pq));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<BureauVote> find(String key, Object value) {
        if(isAllcharge){
            LinkedList<BureauVote> bureauVotes = new LinkedList<>();
            for (BureauVote bureauVote: bureauVoteMap.values()  ) {

                try {
                    if(bureauVote.getClass().getDeclaredMethod("get" + capitalize(key.toLowerCase())).invoke(bureauVote).equals(value))
                        bureauVotes.add(bureauVote);

                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("FIND IN MEMORY ");
            return bureauVotes;
        }

        PreparedStatement statement;
        StringBuilder query = new StringBuilder();

        try {
            query.append("SELECT * FROM BureauVote WHERE ").append(key.toUpperCase()).append("  = ?");
            statement = connection.prepareStatement(query.toString());
            statement.setObject(1, value);

            List<BureauVote> bureauVotes = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){

                BureauVote bureauVote = new BureauVote();
                bureauVote.setBID(resultSet.getLong("bid"));
                bureauVote.setNom(resultSet.getString("nom"));
                bureauVote.setVille(new VilleDaoImpl().read(resultSet.getLong("vid")));
                bureauVote.getAdresse().setRue(resultSet.getString("rue"));
                bureauVote.getAdresse().setCp(resultSet.getString("cp"));
                bureauVotes.add(bureauVote);
            }


            for (BureauVote bureauVote :bureauVotes){
                bureauVoteMap.put(new KeyBureauVote(bureauVote.getBID(),bureauVote.getVille().getVID()),bureauVote);
            }

            return bureauVotes;

        } catch (SQLException e) {
            return null;
        }
    }
}
