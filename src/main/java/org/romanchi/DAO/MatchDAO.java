package org.romanchi.DAO;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by Роман on 28.07.2017.
 */
@Entity
@Table(name = "test_v4")
public class MatchDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int matchId;
    @Column(name = "winner")
    Integer winner;
    @Column(name = "time")
    String time;
    @Column(name = "match_name")
    String matchName;
    @Column(name = "match_link", length = 500)
    String matchLink;
    @Column(name = "score")
    String score;
    @Column(name = "first")
    double first;
    @Column(name = "draw")
    double draw;
    @Column(name = "second")
    double second;
    @Column(name = "amount_of_available_bookmakers_odds")
    int amountOfAvailableBookmakersOdds;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ligue_id")
    LigaDAO ligaDAO;

    public MatchDAO() {
    }
    public MatchDAO(Integer winner, String time, String matchName, String matchLink, String score, String first, String draw, String second, String amountOfAvailableBookmakersOdds, LigaDAO ligaDAO) {
        try{
            this.winner = winner;
            this.time = time;
            this.matchName = matchName;
            this.matchLink = matchLink;
            this.score = score;
            this.first = Double.valueOf(first);
            this.draw = Double.valueOf(draw);
            this.second = Double.valueOf(second);
            this.amountOfAvailableBookmakersOdds = Integer.valueOf(amountOfAvailableBookmakersOdds);
            this.ligaDAO = ligaDAO;
        }catch (NumberFormatException ex){
            System.out.println("Number format exception " + ex.getLocalizedMessage());
        }
    }
    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public Integer getWinner() {
        return winner;
    }

    public void setWinner(Integer winner) {
        this.winner = winner;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public String getMatchLink() {
        return matchLink;
    }

    public void setMatchLink(String matchLink) {
        this.matchLink = matchLink;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
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

    public int getAmountOfAvailableBookmakersOdds() {
        return amountOfAvailableBookmakersOdds;
    }

    public void setAmountOfAvailableBookmakersOdds(int amountOfAvailableBookmakersOdds) {
        this.amountOfAvailableBookmakersOdds = amountOfAvailableBookmakersOdds;
    }

    public LigaDAO getLigaDAO() {
        return ligaDAO;
    }

    public void setLigaDAO(LigaDAO ligaDAO) {
        this.ligaDAO = ligaDAO;
    }

    @Override
    public String toString() {
        return "MatchDAO{" +
                "matchId=" + matchId +
                ", winner=" + winner +
                ", time='" + time + '\'' +
                ", matchName='" + matchName + '\'' +
                ", matchLink='" + matchLink + '\'' +
                ", score='" + score + '\'' +
                ", first=" + first +
                ", draw=" + draw +
                ", second=" + second +
                ", amountOfAvailableBookmakersOdds=" + amountOfAvailableBookmakersOdds +
                ", ligaDAO=" + ligaDAO +
                '}';
    }
}
