package org.romanchi;

import io.webfolder.ui4j.api.browser.BrowserEngine;
import io.webfolder.ui4j.api.browser.BrowserFactory;
import io.webfolder.ui4j.api.browser.Page;
import org.hibernate.Session;
import org.romanchi.DAO.MatchDAO;

import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by Роман on 14.08.2017.
 */
public class Test {

    public void test(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<MatchDAO> criteriaQuery = criteriaBuilder.createQuery(MatchDAO.class);
        Root<MatchDAO> root = criteriaQuery.from(MatchDAO.class);
        criteriaQuery.select(root);
        TypedQuery<MatchDAO> typedQuery = session.createQuery(criteriaQuery);
        List<MatchDAO> list = typedQuery.setMaxResults(500).getResultList();
        long begin = System.currentTimeMillis();
        Runtime r = Runtime.getRuntime();
        long memoryBegin = r.freeMemory();
        for(int i = 0; i < list.size(); i++){
            Page page = BrowserFactory.getWebKit().navigate("http://spectorsky.ho.ua/cgi-bin/pk0942_spectorsky_final_sa.php");
            System.out.println(page.getDocument().getTitle());
        }
        long end = System.currentTimeMillis();
        long memoryEnd = r.freeMemory();
        System.out.println(String.format("Time: %fs", (end-begin)/1000.0));
        System.out.println(String.format("Memory: %f mb", (memoryEnd - memoryBegin)/1024.0));
    }
}
