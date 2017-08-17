package org.romanchi.DAO;



import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Роман on 31.07.2017.
 */
@Entity
@Table(name = "highs_v5")
public class HighDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "high_id")
    private int highId;
    @Column(name = "high_value")
    private double highValue;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "match_id")
    private MatchDAO match;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "bookmaker_name")
    private BookmakerDAO bookmaker;
    @Column(name = "bet_winner")
    private int betWinner; // 1 - first 2 - second 0 - draw
    @Column(name = "type")
    private int type; /*1 - bet 2 - handicape*/
    @Column(name = "handicape_total")
    private String handicape_total;

    public String getHandicape_total() {
        return handicape_total;
    }

    public void setHandicape_total(String handicape_total) {
        this.handicape_total = handicape_total;
    }
    /*
     * Комп. ставить тыльки на високы коефи, які йому вже відомі.
     * В житті ми не можемо знати, який це коеф (чи можемо?)
     *
     * */

    public HighDAO() {
    }
    public HighDAO(String highValue, int betWinner,int type,String handicape_total, MatchDAO match, BookmakerDAO bookmaker) {
        try{
            this.highValue = Double.valueOf(highValue);
            this.betWinner = betWinner;
            this.type = type;
            this.match = match;
            this.bookmaker = bookmaker;
            this.handicape_total = handicape_total;
        }catch (NumberFormatException ex){
            System.out.println("Number format exception: " + ex.getLocalizedMessage());
        }
    }

    public int getHighId() {
        return highId;
    }

    public void setHighId(int highId) {
        this.highId = highId;
    }

    public double getHighValue() {
        return highValue;
    }

    public void setHighValue(double highValue) {
        this.highValue = highValue;
    }

    public MatchDAO getMatch() {
        return match;
    }

    public void setMatch(MatchDAO match) {
        this.match = match;
    }

    public BookmakerDAO getBookmaker() {
        return bookmaker;
    }

    public void setBookmaker(BookmakerDAO bookmaker) {
        this.bookmaker = bookmaker;
    }
    public int getBetWinner() {
        return betWinner;
    }

    public void setBetWinner(int betWinner) {
        this.betWinner = betWinner;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "HighDAO{" +
                "highValue=" + highValue +
                ", match=" + match +
                ", bookmaker=" + bookmaker +
                ", betWinner=" + betWinner +
                ", type=" + type +
                '}';
    }
}
