package main;/*
 * From http://www.fankhausers.com/postgresql/jdbc/#driver_download
 * and from http://deptinfo.unice.fr/~grin/mescours/minfo/bdavancees/tp/tpjdbc1/index.html
 * and adaptated by M. Manouvrier to the database example
 */

import main.ConfigConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TestJDBCPostgresql
{
    Connection       db=null;   // A connection to the database
    Statement        sql=null;  // Our statement to run queries with
    DatabaseMetaData dbmd;      // This is basically info the driver delivers
    // about the DB it just connected to.
    // Just used to get the DB version to confirm the
    // connection in this example.

    public TestJDBCPostgresql(String argv[])
            throws ClassNotFoundException, SQLException, java.io.IOException
    {
    /* Put the following lines instead of the four first lines
     * of the try block in order to not use the ConfigConnection class*/
    /*String database = argv[0];
    String username = argv[1];
    String password = argv[2];
    Class.forName("org.postgresql.Driver"); //load the driver
    db = DriverManager.getConnection("jdbc:postgresql:"+database,
                                     username,
                                     password); //connect to the db */

        try {
            //Code using the ConfigConnection class
            String username = argv[0];
            String password = argv[1];
            String fichierProp = argv[2];
            System.out.println("Username=" +username + " Passwd="+password+" fichierProp="+fichierProp+"\n");
            db = ConfigConnection.getConnection(fichierProp,username,password);
		
		
		/* AUTOCOMMIT: If set to true, PostgreSQL will automatically do 
		 * a COMMIT after each successful command that is not inside 
		 * an explicit transaction block (that is, unless a BEGIN with no 
		 * matching COMMIT has been given). 
		 * If set to false, PostgreSQL will commit only upon receiving 
		 * an explicit COMMIT command.
		 * The default is true, for compatibility with historical 
		 * PostgreSQL behavior. However, for maximum compatibility 
		 * with the SQL specification, set it to false.
		 * cf. http://www.postgresql.org/docs/7.3/static/runtime-config.html
		 */
            db.setAutoCommit(false);

            dbmd = db.getMetaData(); //get MetaData to confirm connection

            System.out.println("Connection to SGBD "+ dbmd.getDatabaseProductName()+ " version "+
                    dbmd.getDatabaseProductVersion()+ " database " +
                    dbmd.getURL()+ " \nusing " + dbmd.getDriverName() + " version "+
                    dbmd.getDriverVersion()+ " " + "successful.\n");

            sql = db.createStatement(); //create a statement that we can use later

            // Creation of a table Note
            String sqlText = "CREATE TABLE Note ( Note_ID SERIAL, " +
                    "Inscription_ID integer NOT NULL," +
                    "Note real," +
                    "CONSTRAINT PK_Notes PRIMARY KEY (Note_ID)," +
                    " CONSTRAINT FK_Notes_Inscription " +
                    " FOREIGN KEY (Inscription_ID) " +
                    " REFERENCES Inscription (Inscription_ID) " +
                    " );";

            System.out.println("Executing this command: "+sqlText+"\n");
            sql.executeUpdate(sqlText);
            // Don't forget command COMMIT!!!
            db.commit();

            // Insertion into table Inscription
            sqlText = "INSERT INTO Inscription VALUES " +
                    " (nextval('inscription_inscription_id_seq')," +
                    " (SELECT Etudiant_ID FROM Etudiant WHERE Nom='SUFFIT')," +
                    " (SELECT Enseignement_ID FROM Enseignement " +
                    " WHERE Intitule LIKE '%HIBERNATE%')," +
                    " (SELECT Master_ID FROM Enseignement " +
                    " WHERE Intitule LIKE '%HIBERNATE%'));";
            System.out.println("Executing this command: "+sqlText+"\n");
            sql.executeUpdate(sqlText);

            // Insertion into table Note
            sqlText = "INSERT INTO Note VALUES " +
                    " (nextval('note_note_id_seq')," +
                    " (SELECT Inscription_ID FROM Inscription i, Enseignement e, Etudiant et " +
                    "WHERE Intitule LIKE '%HIBERNATE%' " +
                    "AND e.Enseignement_ID=i.Enseignement_ID " +
                    "AND et.Etudiant_ID=i.Etudiant_ID " +
                    "AND et.Nom='SUFFIT' " +
                    "AND e.Master_ID=i.Master_ID)," +
                    "14);";
            System.out.println("Executing this command : "+sqlText+"\n");
            sql.executeUpdate(sqlText);
            sql.executeUpdate(sqlText);
            // Don't forget command COMMIT!!!
            db.commit();

            //Update of table Salle
            // SELECT before update
            sqlText="SELECT capacite FROM Salle " +
                    "WHERE Batiment ='B' " +
                    "AND Numero_Salle='020' FOR UPDATE";
            System.out.println("Executing this command : "+sqlText+"\n");
            ResultSet rset = sql.executeQuery(sqlText);
            // To print current result record before update
            if (rset.next()) {
                //Column numbered from 1 (not from zero)
                System.out.println("Capacité de la salle B020 avant MàJ : "
                        + rset.getInt(1) + "\n");

                // UPDATE
                sqlText = "UPDATE Salle SET capacite = 30 WHERE Batiment ='B' AND Numero_Salle='020'";
                System.out.println("Executing this command: "+sqlText+"\n");
                sql.executeUpdate(sqlText);
                System.out.println (sql.getUpdateCount()+
                        " rows were update by this statement\n");
                // Don't forget command COMMIT!!!
                db.commit();
                // SELECT after UPDATE
                rset = sql.executeQuery("SELECT Capacite FROM Salle " +
                        "WHERE Batiment ='B' " +
                        "AND Numero_Salle='020'");
                // To print current result record before update
                while (rset.next()) {
                    System.out.println("Capacité de la salle B020 après MàJ : "
                            + rset.getInt(1) + "\n");
                }
            }
            else {
                db.rollback(); // free lock created by  "SELECT ... FOR UPDATE"
                System.out.println("Pas de salle B020 : " +
                        "Libération du verrou posé lors du SELECT ...FOR UPDATE. \n" );
            }

            rset = sql.executeQuery("SELECT Date_Resa FROM Reservation " +
                    "WHERE Batiment='B' " +
                    "AND Numero_Salle='022'");
            while (rset.next()) {

                // To print meta data
                // !!There is a bug , table name is empty
                System.out.println("Nom de la table : (généralement vide bug du driver)"
                        + rset.getMetaData().getTableName(1));
                System.out.println("Type de la colonne  : "
                        + rset.getMetaData().getColumnTypeName(1));
                System.out.println("Nom de la colonne : "
                        + rset.getMetaData().getColumnName(1) + "\n");

                // To print current result record
                System.out.println("Date de reservation du nuplet résultat: "
                        + rset.getDate(1) + "\n");

                // Example of code for managing Date
                java.sql.Date dateResa = rset.getDate(1);
                if (! rset.wasNull()) {
                    String dateResaF =
                            DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.FRANCE).format(dateResa);
                    SimpleDateFormat formateur =
                            (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.FRANCE);
                    formateur.applyPattern("MMMM"); // configure le formateur pour avoir
                    // le mois en lettres (avec plus de
                    // 3 lettres (car plus de 3 M)
                    String mois = formateur.format(dateResa);
                    System.out.println("Affichage de la date de manière formaté:");
                    System.out.println("Réservation pour le : " + dateResaF);
                    System.out.println("Au mois de " + mois);
                }
                else
                    System.out.println();
            }

            // Creation of a PreparedStatement object for sending
            // parameterized SQL statements to the database
            System.out.println("\n\nNow demostrating a prepared statement...");
            sqlText = "INSERT INTO Salle VALUES (?,?,?)";
            System.out.println("The Statement looks like this: "+sqlText+"\n");
            System.out.println("Looping several times filling in the fields...\n");
            PreparedStatement ps = db.prepareStatement(sqlText);

            // Execute the parameterized SQL statements
            // Seting the specified parameter to the given string value
            String [] NumBatiment = {"A", "B", "C", "P", "D"};
            String [] NumSalle = {"208", "026", "405", "340", "120"};
            int lenNB = NumBatiment.length;
            for (int i=0, c=30 ; (i<lenNB) && (c<35) ;c++,i++)
            {
                System.out.println(i+" " + NumBatiment[i]+ " " + NumSalle[i]+ "...\n");
                ps.setString(1,NumBatiment[i]); //set column one (Batiment) to ith string of NumBatiment
                ps.setString(2,NumSalle[i]);; //set column two (Numero_Salle) to ith string of NumSalle
                ps.setInt(3,c); //set column three (Capacite) to c
                ps.executeUpdate();
            }
            // Don't forget command COMMIT!!!
            db.commit();
            ps.close();

            System.out.println("Now executing the command: "+
                    "SELECT * FROM Salle");
            ResultSet results = sql.executeQuery("SELECT * FROM Salle");
            System.out.println("Affichage des nuplets de la relation Salle après insertion :");
            if (results != null)
            {
                while (results.next())
                {
                    // To print the current result record
                    System.out.println("Batiment =" +results.getString(1)+
                            "; Numero de salle = "+results.getString("Numero_Salle")+
                            "; Capacité = "+results.getInt("Capacite")+"\n");
                }
            }
            results.close();

        }
        catch (SQLException ex)
        {
            System.out.println("***Exception:\n"+ex);
            ex.printStackTrace();
            // ROLLBACK updates
            System.out.println("\nROLLBACK!\n"+ex);
            if (db != null) db.rollback();
        }
        finally {
            System.out.println("Deconnexion");
            if (sql != null) sql.close();
            if (db != null) db.close();
        }
    }

    public static void correctUsage()
    {
        System.out.println("\nIncorrect number of arguments.\nUsage:\n "+
                "java   \n");
        System.exit(1);
    }

    public static void main (String args[])
    { System.out.println("nb arg="+args.length);

        if (args.length != 3) correctUsage();
        try
        {
            new TestJDBCPostgresql(args);
        }
        catch (Exception ex)
        {
            System.out.println("***Exception:\n"+ex);
            ex.printStackTrace();
        }
    }
}