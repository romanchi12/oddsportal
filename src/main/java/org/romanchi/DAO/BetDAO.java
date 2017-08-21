package org.romanchi.DAO;



import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

/**
 * Created by Роман on 29.07.2017.
 */
@Entity
@Table(name = "bets_v6")
public class BetDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bet_id")
    private long betId;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "bookmaker_name")
    BookmakerDAO bookmaker;
    @Column(name = "first")
    double first;
    @Column(name = "draw")
    double draw;
    @Column(name = "second")
    double second;
    @Column(name = "payout")
    double payout;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "match_id")
    MatchDAO matchDAO;
    public BetDAO() {
    }
    public BetDAO(String bookmakerName, String first, String draw, String second, String payout, MatchDAO matchDAO) {
        try{
            this.bookmaker = new BookmakerDAO(bookmakerName);
            this.first = Double.valueOf(first);
            this.draw = Double.valueOf(draw);
            this.second = Double.valueOf(second);
            this.payout = Double.valueOf(payout.substring(0, payout.length() - 1))/100;
            this.matchDAO = matchDAO;
        }catch (NumberFormatException ex){

        }
    }

    public long getBetId() {
        return betId;
    }

    public void setBetId(long betId) {
        this.betId = betId;
    }

    public BookmakerDAO getBookmaker() {
        return bookmaker;
    }

    public void setBookmaker(BookmakerDAO bookmaker) {
        this.bookmaker = bookmaker;
    }

    public double getFirst() {
        return first;
    }

    public void setFirst(double first) {
        this.first = first;
    }

    public double getDraw() {
        return draw;
    }

    public void setDraw(double draw) {
        this.draw = draw;
    }

    public double getSecond() {
        return second;
    }

    public void setSecond(double second) {
        this.second = second;
    }

    public double getPayout() {
        return payout;
    }

    public void setPayout(double payout) {
        this.payout = payout;
    }

    public MatchDAO getMatchDAO() {
        return matchDAO;
    }

    public void setMatchDAO(MatchDAO matchDAO) {
        this.matchDAO = matchDAO;
    }
    @Override
    public String toString() {
        return "BetDAO{" +
                /*"bookmakerName=" + bookmakerName +*/
                ", first=" + first +
                ", draw=" + draw +
                ", second=" + second +
                ", payout=" + payout +
                '}';
    }
}
