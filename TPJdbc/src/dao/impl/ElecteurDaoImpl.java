package dao.impl;

import dao.ElecteurDao;
import dao.VilleDao;
import dao.utils.ResultSetMapper;
import entity.*;
import entity.Electeur;
import main.ConfigConnection;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.springframework.util.StringUtils.capitalize;

/**
 * Created by dialal14 on 23/01/17.
 */
public class ElecteurDaoImpl implements ElecteurDao {

    private Connection connection;
    /*
        Represente les object en memoire
    */
    private static final Map<Long,Electeur> electeurMap = new HashMap<>();
    /*
    verifie si tou les objects sont en memoire, a true unique lorsqu'on appelle la fonction list :)
     */
    private static boolean isAllcharge =false;

    public ElecteurDaoImpl() {
        try {
            connection = ConfigConnection.getConnection("../main/FichierConnexion.txt");
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    /*
        Lecture agressive
     */
    @Override
    public Electeur read(Long pk) {

        if(electeurMap.containsKey(pk)){
            System.out.println("FIND IN MEMORY ");

            return electeurMap.get(pk);
        }

        PreparedStatement statement;
        StringBuilder query = new StringBuilder();
        Electeur electeur = new Electeur();
        try {
            query.append("SELECT  * FROM ")
                    .append(Electeur.class.getSimpleName().toUpperCase()).append(" WHERE EID  = ?");
            statement = connection.prepareStatement(query.toString());
            statement.setLong(1, pk);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                //Retrieve by column name
                electeur.setEID(resultSet.getLong("eid"));
                electeur.setNom(resultSet.getString("nom"));
                electeur.setPrenom(resultSet.getString("prenom"));
                Ville ville = new VilleDaoImpl().read(resultSet.getLong("vid"));
                electeur.getVille().setNom(ville.getNom());
                electeur.getVille().setVID(ville.getVID());
                BureauVote bureauVote = new BureauVoteDaoImpl().read(resultSet.getLong("bid"), resultSet.getLong("vid"));
                electeur.getBureauVote().setBID(bureauVote.getBid());
                electeur.getBureauVote().setVille(bureauVote.getVille());
                electeur.getBureauVote().setNom(bureauVote.getNom());
                electeur.getBureauVote().setAdresse(bureauVote.getAdresse());
            }
            resultSet.close();

            electeurMap.put(electeur.getEID(),electeur);

            return electeur;

        } catch (SQLException e) {
            return null;
        }
    }

    /*
        Lecture lazy de l'electeur
     */
    @Override
    public Electeur get(Long id) {

        if(electeurMap.containsKey(id)) return electeurMap.get(id);

        PreparedStatement statement;
        StringBuilder query = new StringBuilder();
        Electeur electeur = new Electeur();
        try {
            query.append("SELECT  * FROM ")
                    .append(Electeur.class.getSimpleName().toUpperCase()).append(" WHERE EID  = ?");
            statement = connection.prepareStatement(query.toString());
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                //Retrieve by column name
                electeur.setEID(resultSet.getLong("eid"));
                electeur.setNom(resultSet.getString("nom"));
                electeur.setPrenom(resultSet.getString("prenom"));
                electeur.setVid(resultSet.getLong("vid"));
                electeur.setBid(resultSet.getLong("bid"));
            }
            resultSet.close();

            electeurMap.put(electeur.getEID(),electeur);

            return electeur;

        } catch (SQLException e) {
            return null;
        }
    }

    /*
    lecture lazy de bureau d'un electeur
     */
    @Override
    public Electeur getBureauVote(Electeur electeur) {

        electeur.setBureauVote(new BureauVoteDaoImpl().read(electeur.getBid(),electeur.getVid()));
        return electeur;
    }

    /*
    lecture lazy du ville d'un electeur
     */
    @Override
    public Electeur getVille(Electeur electeur) {
        electeur.setVille(new VilleDaoImpl().read(electeur.getVid()));
        return electeur;
    }

    @SuppressWarnings("Since15")
    @Override
    public Electeur create(Electeur electeur) {
        PreparedStatement statement;
        StringBuilder query = new StringBuilder();
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();

        query.append("SELECT * FROM ELECTEUR WHERE NOM = ?");
        try {
            statement = connection.prepareStatement(query.toString());
            statement.setString(1, electeur.getNom());
            ResultSet resultSet = statement.executeQuery();
            Electeur electeurExuste = new Electeur();

            while(resultSet.next()){
                //Retrieve by column name
                electeurExuste.setEID(resultSet.getLong("eid"));
                electeurExuste.setVille(new VilleDaoImpl().read(resultSet.getLong("vid")));
                electeurExuste.setNom(resultSet.getString("nom"));
                electeurExuste.setPrenom(resultSet.getString("prenom"));
                electeurExuste.setBureauVote(new BureauVoteDaoImpl().read(resultSet.getLong("bid"),resultSet.getLong("vid")));
            }
            resultSet.close();
            if(electeurExuste.getEID() != null){

                return  electeurExuste;
            }

        } catch (Exception ignored) {
        }


        try {
            query = new StringBuilder();
            query.append("INSERT INTO ELECTEUR ");

            if (Electeur.class.isAnnotationPresent(Entity.class)) {
                // get all the attributes of outputClass

                Field[] fields = Electeur.class.getDeclaredFields();
                Field field;
                // ID AUTO GENERATE i = 1 pour ne pas e prendre en compte
                for (int i = 1; i < fields.length; i++) {
                    field = fields[i];

                    if (field.isAnnotationPresent(Column.class)) {
                        Column column = field.getAnnotation(Column.class);
                        key.append(column.name());

                        Object invoke = electeur.getClass().getDeclaredMethod("get" + capitalize(column.name().
                                toLowerCase())).invoke(electeur);
                        if(invoke instanceof Number || Objects.isNull(invoke)){
                            value.append(invoke);
                        }else {
                            value.append("'").append(invoke).append("'");
                        }
                    }
                    if (i != fields.length - 1 && fields[i+1].isAnnotationPresent(Column.class)) {
                        key.append(", ");
                        value.append(", ");
                    }
                }

            }
            Ville ville = (electeur.getVille().getVID() == null)? new VilleDaoImpl().create(electeur.getVille()) : electeur.getVille();
            electeur.setVille(ville);

            BureauVote bureauVote = (electeur.getBureauVote().getBID() == null)? new BureauVoteDaoImpl().create(electeur.getBureauVote()) :
                    electeur.getBureauVote();
            electeur.setBureauVote(bureauVote);

            key.append(" ,VID, BID ");
            value.append(",").append(ville.getVID()).append(" , ").append(bureauVote.getBID());

            query.append(" (EID, ").append(key).append(" ) VALUES (nextval('electeur_eid_seq'), ").append(value).append(" )");
            new BureauVoteDaoImpl().create(electeur.getBureauVote());
            new VilleDaoImpl().create(electeur.getVille());

            System.out.println(query);
            statement = connection.prepareStatement(query.toString());

            statement.executeUpdate();
            return electeur;

        } catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Electeur> list() {
        if(isAllcharge) {
            System.out.println("FIND IN MEMERY ");
            return new LinkedList<>(electeurMap.values());
        }

        PreparedStatement statement;
        List<Electeur> electeurs = new ArrayList<>();
        try {
            statement = connection.prepareStatement("SELECT * FROM " + Electeur.class.getSimpleName());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                //Retrieve by column name
                Electeur electeur = new Electeur();

                electeur.setEID(resultSet.getLong("eid"));
                electeur.setNom(resultSet.getString("nom"));
                electeur.setPrenom(resultSet.getString("prenom"));
                Ville ville = new VilleDaoImpl().read(resultSet.getLong("vid"));
                electeur.getVille().setNom(ville.getNom());
                electeur.getVille().setVID(ville.getVID());
                BureauVote bureauVote = new BureauVoteDaoImpl().read(resultSet.getLong("bid"), resultSet.getLong("vid"));
                electeur.getBureauVote().setBID(bureauVote.getBid());
                electeur.getBureauVote().setVille(bureauVote.getVille());
                electeur.getBureauVote().setNom(bureauVote.getNom());
                electeur.getBureauVote().setAdresse(bureauVote.getAdresse());

                electeurs.add(electeur);
            }
            resultSet.close();
            return electeurs;

        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Electeur update(Electeur electeur) {
        PreparedStatement statement;
        StringBuilder query = new StringBuilder();
        StringBuilder setUpdate = new StringBuilder();

        try {
            query.append("UPDATE ").append(Electeur.class.getSimpleName()).append(" SET ");

            if (Electeur.class.isAnnotationPresent(Entity.class)) {
                // get all the attributes of outputClass

                Field[] fields = Electeur.class.getDeclaredFields();
                Field field;
                //i= 1 pour ne pas set ID
                for (int i = 1; i < fields.length -4 ; i++) {
                    field = fields[i];
                    if (field.isAnnotationPresent(Column.class)) {
                        Column column = field.getAnnotation(Column.class);
                        setUpdate.append(column.name()).append(" = '").append(electeur.getClass().getDeclaredMethod("get"
                                + capitalize(column.name().toLowerCase())).invoke(electeur)).append("',");

                    }

                }

            }
            setUpdate.append("VID = ").append("").append(electeur.getVille().getVID()).append("");
            setUpdate.append(", BID = ").append("").append(electeur.getBureauVote().getBID()).append("");
            query.append(setUpdate).append(" WHERE ").append(" EID = ").append(electeur.getEID());

            System.out.println(query);

            statement = connection.prepareStatement(query.toString());

            BureauVote bureauVote = new BureauVoteDaoImpl().create(electeur.getBureauVote());
            Ville ville = new VilleDaoImpl().create(electeur.getVille());
            statement.executeUpdate();

            electeur.setVille(ville);
            electeur.setBureauVote(bureauVote);

            return electeur;

        } catch (SQLException | NoSuchMethodException |
                IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(Long pk) {
        PreparedStatement statement;
        StringBuilder query = new StringBuilder();

        try {
            query.append("DELETE FROM ").append(Electeur.class.getSimpleName().toUpperCase()).append(" WHERE EID = ?");
            statement = connection.prepareStatement(query.toString());
            statement.setLong(1, pk);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
