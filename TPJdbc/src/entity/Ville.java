package entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dialal14 on 23/01/17.
 */
@Entity
@Table(name = "VILLE")
public class Ville {

    @Id
    @Column(name = "VID")
    private Long VID;

    @Column(name = "NOM")
    private String nom;

    /*
        Lien bi directionnel
     */
    private List<BureauVote> bureauVotes = new LinkedList<>();

    public Long getVID() {
        return VID;
    }

    public void setVID(Long VID) {
        this.VID = VID;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<BureauVote> getBureauVotes() {
        return bureauVotes;
    }

    public void setBureauVotes(List<BureauVote> bureauVotes) {
        this.bureauVotes = bureauVotes;
    }

    @Override
    public String toString() {
        return "Ville{" +
                "VID=" + VID +
                ", nom='" + nom + '\'' +
                '}';
    }

    /*
    Affiche la ville avec les bureaux de votes
     */
    public String printWithBureauVote() {
        return "Ville{" +
                "VID=" + VID +
                ", nom='" + nom + '\'' +
                ", bureauVotes=" + bureauVotes +
                '}';
    }
}
