package org.romanchi.DAO;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Created by Роман on 14.08.2017.
 */
@Entity
@Table(name = "ligues_v4")
public class LigaDAO {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ligue_id")
    int ligueId;
    @Column(name = "ligue_name")
    String ligueName;

    public LigaDAO() {
    }

    public LigaDAO(String ligueName) {
        this.ligueName = ligueName;
    }

    public int getLigueId() {
        return ligueId;
    }

    public void setLigueId(int ligueId) {
        this.ligueId = ligueId;
    }

    public String getLigueName() {
        return ligueName;
    }

    public void setLigueName(String ligueName) {
        this.ligueName = ligueName;
    }
}
