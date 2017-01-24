package entity;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dialal14 on 23/01/17.
 */
@Entity
@Table(name = "electeur")
public class Electeur {

    @Id
    @Column(name = "EID")
    private Long EID;

    @Column(name = "NOM")
    private String nom;

    @Column(name = "PRENOM")
    private String prenom;

    /*
        ASTUCE POUR LA VERSION LAZY
     */
    @Column(name = "VID")
    private long vid;
    /*
        ASTUCE POUR LA VERSION LAZY
    */
    @Column(name = "BID")
    private Long bid;

    @Transient
    private Ville ville = new Ville();

    @Transient
    private BureauVote bureauVote = new BureauVote();

    public Long getEID() {
        return EID;
    }

    public Long getEid() {
        return EID;
    }

    public void setEID(Long EID) {
        this.EID = EID;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Ville getVille() {
        return ville;
    }

    public void setVille(Ville ville) {
        this.ville = ville;
    }

    public BureauVote getBureauVote() {
        return bureauVote;
    }

    public void setBureauVote(BureauVote bureauVote) {
        this.bureauVote = bureauVote;
    }

    public long getVid() {
        return vid;
    }

    public void setVid(long vid) {
        this.vid = vid;
    }

    public Long getBid() {
        return bid;
    }

    public void setBid(Long bid) {
        this.bid = bid;
    }

    @Override
    public String toString() {
        return "Electeur{" +
                "EID=" + EID +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", ville=" + ville +
                ", bureauVote=" + bureauVote +
                '}';
    }
}
