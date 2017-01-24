package dao.impl;

import dao.VilleDao;
import dao.utils.ResultSetMapper;
import entity.Electeur;
import entity.Ville;
import main.ConfigConnection;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

import static org.springframework.util.StringUtils.capitalize;

/**
 * Created by dialal14 on 23/01/17.
 */
public class VilleDaoImpl implements VilleDao {

    private Connection connection;
    /*
        Represente les object en memoire
    */
    static  final Map<Long,Ville> villeHashMap = new HashMap<>();
    /*
    verifie si tou les objects sont en memoire, a true unique lorsqu'on appelle la fonction list :)
     */
    private static boolean isAllcharge =false;


    public VilleDaoImpl() {
        try {

            connection = ConfigConnection.getConnection("../main/FichierConnexion.txt");

        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public Ville read(Long pk) {

        if(villeHashMap.containsKey(pk)){
            System.out.println("FIND IN MEMORY ");
            return villeHashMap.get(pk);
        }

        PreparedStatement statement;
        StringBuilder query = new StringBuilder();
        ResultSetMapper<Ville> resultSetMapper = new ResultSetMapper<Ville>();
        try {
            query.append("SELECT * FROM ")
                    .append(Ville.class.getSimpleName().toUpperCase()).append(" WHERE VID  = ?");
            statement = connection.prepareStatement(query.toString());
            statement.setLong(1, pk);

            Ville ville = resultSetMapper.mapRersultSetToObject(statement.executeQuery(), Ville.class).get(0);
            villeHashMap.put(ville.getVID(),ville);

            return ville;

        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Ville getBureauVote(Ville ville) {
        ville.setBureauVotes(new BureauVoteDaoImpl().find("VID",ville.getVID()));
        return  ville;
    }

    @Override
    public List<Ville> list() {
        if(isAllcharge) {
            System.out.println("FIND IN MEMERY ");
            return new LinkedList<>(villeHashMap.values());
        }

        PreparedStatement statement;
        try {
            ResultSetMapper<Ville> resultSetMapper = new ResultSetMapper<Ville>();
            statement = connection.prepareStatement("SELECT * FROM " + Ville.class.getSimpleName());
            List<Ville> villeList = resultSetMapper.mapRersultSetToObject(statement.executeQuery(), Ville.class);

            for (Ville ville: villeList) {
                villeHashMap.put(ville.getVID(),ville);
                isAllcharge = true;
            }

            return villeList;

        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Ville update(Ville t) {
        PreparedStatement statement;
        StringBuilder query = new StringBuilder();
        StringBuilder setUpdate = new StringBuilder();

        try {
            query.append("UPDATE VILLE SET ");

            if (Ville.class.isAnnotationPresent(Entity.class)) {
                // get all the attributes of outputClass

                Field[] fields = Ville.class.getDeclaredFields();
                Field field;
                //i= 1 pour ne pas set ID
                for (int i = 1; i < fields.length; i++) {
                    field = fields[i];
                    if (field.isAnnotationPresent(Column.class)) {
                        Column column = field.getAnnotation(Column.class);
                        setUpdate.append(column.name() + " = '" + t.getClass().getDeclaredMethod("get"
                                + capitalize(column.name().toLowerCase())).invoke(t) + "'");
                        if (i != fields.length - 1) {
                            setUpdate.append(", ");
                        }
                    }
                }
            }
            query.append(setUpdate).append(" WHERE ").append(" ID = ").append(t.getClass()
                    .getDeclaredMethod("getId").invoke(t));
            statement = connection.prepareStatement(query.toString());

            statement.executeUpdate();

            villeHashMap.put(t.getVID(),t);

            return t;

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
            query.append("DELETE FROM ").append(Ville.class.getSimpleName().toUpperCase()).append(" WHERE VID = ?");
            statement = connection.prepareStatement(query.toString());
            statement.setLong(1, pk);

            statement.executeUpdate();

            villeHashMap.remove(pk);

        } catch (SQLException e) {
        }
    }
    @SuppressWarnings("Since15")
    @Override
    public Ville create(Ville ville) {

        PreparedStatement statement;
        StringBuilder query = new StringBuilder();
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();

        query.append("SELECT * FROM VILLE WHERE NOM = ?");
        try {
            statement = connection.prepareStatement(query.toString());
            statement.setString(1, ville.getNom());
            ResultSetMapper<Ville> resultSetMapper = new ResultSetMapper();
           Ville ville1 =  resultSetMapper.mapRersultSetToObject(statement.executeQuery(), Ville.class).get(0);
            if(ville1.getVID() != null){
                villeHashMap.put(ville1.getVID(),ville1);
                return  ville1;
            }

        } catch (Exception ignored) {
        }


        try {
            query = new StringBuilder();
            query.append("INSERT INTO Ville ");

            if (Ville.class.isAnnotationPresent(Entity.class)) {
                // get all the attributes of outputClass

                Field[] fields = Ville.class.getDeclaredFields();
                Field field;
                // ID AUTO GENERATE i = 1 pour ne pas e prendre en compte
                for (int i = 1; i < fields.length; i++) {
                    field = fields[i];

                    if (field.isAnnotationPresent(Column.class)) {
                        Column column = field.getAnnotation(Column.class);
                        key.append(column.name());
                        Object invoke = ville.getClass().getDeclaredMethod("get" + capitalize(column.name().
                                toLowerCase())).invoke(ville);
                        if(invoke instanceof Number || Objects.isNull(invoke)){
                            value.append(invoke);
                        }else {
                            value.append("'").append(invoke).append("'");
                        }

                        if (i != fields.length - 1 && fields[i+1].isAnnotationPresent(Column.class)) {
                            key.append(", ");
                            value.append(", ");
                        }
                    }
                }

            }

            query.append(" (VID, ").append(key).append(" ) VALUES (nextval('ville_vid_seq'), ").append(value).append(" )");

            statement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);

            if(statement.executeUpdate() == 0) throw new SQLException("error");
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {

                    ville.setVID(generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            villeHashMap.put(ville.getVID(),ville);
            return ville;

        } catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Ville> find(String key, Object value) {
        if(isAllcharge){
            LinkedList<Ville> villes = new LinkedList<>();
            for (Ville ville: villeHashMap.values()  ) {

                try {
                    if(ville.getClass().getDeclaredMethod("get" + capitalize(key.toLowerCase())).invoke(ville).equals(value))
                        villes.add(ville);

                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("FIND IN MEMORY ");
            return villes;
        }

        PreparedStatement statement;
        StringBuilder query = new StringBuilder();
        ResultSetMapper<Ville> resultSetMapper = new ResultSetMapper<Ville>();

        try {
            query.append("SELECT * FROM Ville WHERE ").append(key.toUpperCase()).append("  = ?");
            statement = connection.prepareStatement(query.toString());
            statement.setObject(1, value);

            List<Ville> villes = resultSetMapper.mapRersultSetToObject(statement.executeQuery(), Ville.class);

            for (Ville ville :villes) villeHashMap.put(ville.getVID(),ville);

            return villes;

        } catch (SQLException e) {
            return null;
        }
    }

}
