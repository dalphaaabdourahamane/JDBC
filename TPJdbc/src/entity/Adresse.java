package entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by dialal14 on 23/01/17.
 */

public class Adresse {

    @Column(name = "RUE")
    private String Rue;

    @Column(name = "CP")
    private String cp;

    public String getRue() {
        return Rue;
    }

    public void setRue(String rue) {
        Rue = rue;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    @Override
    public String toString() {
        return "Adresse{" +
                "Rue='" + Rue + '\'' +
                ", cp='" + cp + '\'' +
                '}';
    }
}
