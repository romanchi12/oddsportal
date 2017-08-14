package org.romanchi.DAO;

import org.hibernate.Session;
import org.romanchi.HibernateUtil;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.List;

/**
 * Created by Роман on 31.07.2017.
 */
public class Database {
    public static BookmakerDAO getBookmakerByName(String name){
        Session session = HibernateUtil.getSessionFactory().openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<BookmakerDAO> criteriaQuery = criteriaBuilder.createQuery(BookmakerDAO.class);
        Root<BookmakerDAO> root = criteriaQuery.from(BookmakerDAO.class);
        criteriaQuery.select(root);
        Predicate p1 = criteriaBuilder.like(root.<String>get("bookmakerName"), name);
        criteriaQuery.where(p1);
        TypedQuery<BookmakerDAO> bookmakerDAOTypedQuery = session.createQuery(criteriaQuery);
        List<BookmakerDAO> bookmakers = bookmakerDAOTypedQuery.getResultList();
        session.clear();
        session.close();
        if(bookmakers.isEmpty()){
            return null;
        }else{
            return bookmakers.get(0);
        }
    }
    public static MatchDAO getMatchById(int id){
        Session session = HibernateUtil.getSessionFactory().openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<MatchDAO> criteriaQuery = criteriaBuilder.createQuery(MatchDAO.class);
        Root<MatchDAO> root = criteriaQuery.from(MatchDAO.class);
        criteriaQuery.select(root);
        Predicate p1 = criteriaBuilder.equal(root.get("matchId"), id);
        criteriaQuery.where(p1);
        TypedQuery<MatchDAO> bookmakerDAOTypedQuery = session.createQuery(criteriaQuery);
        List<MatchDAO> matchs = bookmakerDAOTypedQuery.getResultList();
        session.clear();
        session.close();
        if(matchs.isEmpty()){
            return null;
        }else{
            return matchs.get(0);
        }
    }
    public static HighDAO getHighById(int id){
        Session session = HibernateUtil.getSessionFactory().openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<HighDAO> criteriaQuery = criteriaBuilder.createQuery(HighDAO.class);
        Root<HighDAO> root = criteriaQuery.from(HighDAO.class);
        criteriaQuery.select(root);
        Predicate p1 = criteriaBuilder.equal(root.get("highId"), id);
        criteriaQuery.where(p1);
        TypedQuery<HighDAO> bookmakerDAOTypedQuery = session.createQuery(criteriaQuery);
        List<HighDAO> matchs = bookmakerDAOTypedQuery.getResultList();
        session.clear();
        session.close();
        if(matchs.isEmpty()){
            return null;
        }else{
            return matchs.get(0);
        }
    }
    public static List<BookmakerDAO> getAllBookmakers(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<BookmakerDAO> criteriaQuery = criteriaBuilder.createQuery(BookmakerDAO.class);
        Root<BookmakerDAO> root = criteriaQuery.from(BookmakerDAO.class);
        criteriaQuery.select(root);
        TypedQuery<BookmakerDAO> typedQuery = session.createQuery(criteriaQuery);
        List<BookmakerDAO> bookmakerDAOS = typedQuery.getResultList();
        session.clear();
        session.close();
        return bookmakerDAOS;
    }
}
