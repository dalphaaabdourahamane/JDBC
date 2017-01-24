package entity;

import javax.persistence.*;

/**
 * Created by dialal14 on 23/01/17.
 */
@Entity
@Table(name = "BUREAUVOTE")
public class BureauVote {

    @Id
    @Column(name = "BID")
    private Long BID;

    @Transient
    private Ville ville = new Ville();

    @Column(name = "NOM")
    private String nom;

    @Column(name = "VID")
    private Long VID;

    @Transient
    private Adresse adresse = new Adresse();

    public Long getBid() {
        return BID;
    }

    public void setBID(Long BID) {
        this.BID = BID;
    }

    public Long getBID() {
        return BID;
    }

    public Ville getVille() {
        return ville;
    }

    public void setVille(Ville ville) {
        this.ville = ville;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getVID() {
        return VID;
    }

    public void setVID(Long VID) {
        this.VID = VID;
    }

    @Override
    public String toString() {
        return "BureauVote{" +
                "BID=" + BID +
                ", ville=" + ville +
                ", nom='" + nom + '\'' +
                ", adresse=" + adresse +
                '}';
    }
}
