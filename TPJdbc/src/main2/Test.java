package main2;

import dao.BureauVoteDao;
import dao.ElecteurDao;
import dao.impl.BureauVoteDaoImpl;
import dao.impl.ElecteurDaoImpl;
import dao.impl.VilleDaoImpl;
import entity.Adresse;
import entity.BureauVote;
import entity.Electeur;
import entity.Ville;

import java.util.List;

/**
 * Created by dialal14 on 23/01/17.
 */
public class Test {
    public static void main(String arg[]){

        /*
        Lecture de Electeur 2L si il exite
         */
        ElecteurDao electeurDao = new ElecteurDaoImpl();
        System.out.println(electeurDao.read(2L));

    /*
        Creation d'une ville
     */

        Ville villeAlpha = new Ville();
        villeAlpha.setNom("burr Alpha");
        Adresse adresse = new Adresse();
        adresse.setRue("king alpha");
        adresse.setCp("92210");
        System.out.println(new VilleDaoImpl().create(villeAlpha));

        /*
        creation d'un bureau de vote
         */
        BureauVoteDao bureauVoteDao = new BureauVoteDaoImpl();
        BureauVote bureauVote = new BureauVote();

        bureauVote.setBID(7L);
        bureauVote.setNom("alpha bur");
        bureauVote.setVille(villeAlpha);
        bureauVote.setAdresse(adresse);
        System.out.println(bureauVoteDao.create(bureauVote));

        /*
        Vreation d'un electeur
         */
        Electeur aissatouDialloCreate = new Electeur();
        aissatouDialloCreate.setBureauVote(bureauVote);
        aissatouDialloCreate.setNom("Diallo");
        aissatouDialloCreate.setPrenom("Aissatou");
        aissatouDialloCreate.setVille(villeAlpha);

        aissatouDialloCreate = electeurDao.create(aissatouDialloCreate);

        /*
        Liste tous les eleceteurs
         */
        List<Electeur> electeurs = electeurDao.list();
        if(electeurs!=null)for (Electeur b:electeurs) System.out.println(b);

        /*
        Get en mode lazy
         */
        System.out.println();
        Electeur aissatouDialloRead = electeurDao.get(4L);
        System.out.println(aissatouDialloRead);
        /*
         lazy part 2
         */
        aissatouDialloRead = electeurDao.getVille(aissatouDialloRead);
        System.out.println(aissatouDialloRead);
        /*
          part 3
         */
        aissatouDialloRead = electeurDao.getBureauVote(aissatouDialloRead);
        System.out.println(aissatouDialloRead);


        System.out.println(new VilleDaoImpl().list());
        System.out.println();
        System.out.println(new VilleDaoImpl().read(17L));
        System.out.println();
        System.out.println(new VilleDaoImpl().find("nom","burr Alpha"));

        /*
        Update Electeur
         */
        System.out.println();
        aissatouDialloCreate.setPrenom("Aissatou t");;
        aissatouDialloCreate = electeurDao.update(aissatouDialloCreate);
        System.out.println(aissatouDialloCreate);
        System.out.println();

        /*
        lecture electeur
         */
        System.out.println(electeurDao.read(4L));

        /*
        print ville
         */
        System.out.println("************************");

        Ville ville2 = new VilleDaoImpl().read(2L);
        System.out.println(ville2);

        ville2 = new VilleDaoImpl().getBureauVote(ville2);
        /*
        fonction qui affiche le ville et c est bureau de vote 
         */
        System.out.println(ville2.printWithBureauVote());


    }
}
