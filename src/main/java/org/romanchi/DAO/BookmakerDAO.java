package org.romanchi.DAO;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

/**
 * Created by Роман on 29.07.2017.
 */
@Entity
@Table(name = "bookmakers_v6")
public class BookmakerDAO{
    @Id
    @Column(name = "bookmaker_name")
    String bookmakerName;
    @Column(name = "hights", nullable = false)
    Integer hights;
    @Column(name = "bank")
    Double bank;

    public BookmakerDAO() {
    }

    public BookmakerDAO(String bookmakerName) {
        this.bookmakerName = bookmakerName;
    }

    public BookmakerDAO(String bookmakerName, Integer hights) {
        this.bookmakerName = bookmakerName;
        this.hights = hights;
    }

    public Integer getHights() {
        return hights;
    }

    public void setHights(Integer hights) {
        this.hights = hights;
    }

    public String getBookmakerName() {
        return bookmakerName;
    }

    public void setBookmakerName(String bookmakerName) {
        this.bookmakerName = bookmakerName;
    }

    public double getBank() {
        return bank;
    }

    public void setBank(double bank) {
        this.bank = bank;
    }

    @Override
    public String toString() {
        return "BookmakerDAO{" +
                "bookmakerName='" + bookmakerName + '\'' +
                ", hights=" + hights +
                ", bank=" + bank +
                '}';
    }
}
