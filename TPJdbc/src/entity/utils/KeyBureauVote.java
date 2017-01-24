package entity.utils;

/**
 * Created by dialal14 on 24/01/17.
 * Represente la clé metier deu bureau de vote
 */
public class KeyBureauVote {
    private Long BID;
    private Long VID;

    public KeyBureauVote(Long BID, Long VID) {
        this.BID = BID;
        this.VID = VID;
    }

    public KeyBureauVote() {
    }

    public Long getBID() {
        return BID;
    }

    public void setBID(Long BID) {
        this.BID = BID;
    }

    public Long getVID() {
        return VID;
    }

    public void setVID(Long VID) {
        this.VID = VID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyBureauVote that = (KeyBureauVote) o;

        if (!BID.equals(that.BID)) return false;
        return VID.equals(that.VID);

    }

    @Override
    public int hashCode() {
        int result = BID.hashCode();
        result = 31 * result + VID.hashCode();
        return result;
    }
}
