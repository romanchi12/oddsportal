package org.romanchi;

import javafx.application.Platform;

import org.hibernate.Session;
import org.romanchi.DAO.*;
import org.romanchi.controllers.ControllerManager;
import org.romanchi.controllers.UserCtrl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by Роман on 02.08.2017.
 */
public class User extends Thread{
    private double bank;
    private String bookmakerName;
    private double betSize;
    private int type;
    public User(){

    }
    public User(String bookmakerName,double bankSize, double betSize){
        this.bookmakerName = bookmakerName;
        this.betSize = betSize;
        this.type = HighTypes.OVER_UNDER;
        this.bank = bankSize;
        try{
            this.userCtrl = (UserCtrl) ControllerManager.getControllers().get("UserCtrl");
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }
    private int iterations = 0;
    double max = this.bank;
    double sum = 0;
    private UserCtrl userCtrl;
    @Override
    public void run() {
        System.out.println("play");
        play();
    }
    public void play(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<HighDAO> criteriaQuery = criteriaBuilder.createQuery(HighDAO.class);
        Root<HighDAO> root = criteriaQuery.from(HighDAO.class);
        criteriaQuery.select(root);
        root.getModel().getAttributes().forEach(attribute -> {
            System.out.println(attribute.getName());
        });
        Predicate p1 = criteriaBuilder.equal(root.get("type"), this.type);
        Predicate p2 = criteriaBuilder.equal(root.<BookmakerDAO>get("bookmaker"), Database.getBookmakerByName(this.bookmakerName));
       // criteriaQuery.where(p2);
        TypedQuery<HighDAO> highDAOTypedQuery = session.createQuery(criteriaQuery);
        List<HighDAO> highDAOS = highDAOTypedQuery.getResultList();
        //this.bank = highDAOS.size();
        Platform.runLater(()->{
            userCtrl.bankSizeInput.setText(String.valueOf(bank) + "$");
        });
        System.out.println(this.bank + " " + highDAOS.size());
        for(int i = 0; i< highDAOS.size();i++){
            if(highDAOS.get(i).getMatch() != null){
                switch (highDAOS.get(i).getType()){
                    case HighTypes.BET: {
                        doBet(highDAOS.get(i));
                    }
                    case HighTypes.HANDICAPE: {
                        doHandicape(highDAOS.get(i));
                        break;
                    }
                    case HighTypes.OVER_UNDER: {
                        doOverUnder(highDAOS.get(i));
                        break;
                    }
                }
            }
        }

        BigDecimal bigDecimal = new BigDecimal(this.bank);
        bigDecimal = bigDecimal.setScale(2, RoundingMode.CEILING);
        final double bank = bigDecimal.doubleValue();
        try{
            Platform.runLater(() -> userCtrl.resultLabel.setText(String.valueOf(bank) + "$"));
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }
    private void doBet(HighDAO highDAO){
        String[] goals = highDAO.getMatch().getScore().split(":");
        try {
            Integer first = Integer.parseInt(goals[0]);
            Integer second = Integer.parseInt(goals[1]);
            int diff = first - second;
            if(diff == 0){
                //draw
                if(highDAO.getBetWinner() == WinnerTypes.DRAW){
                    bank -= betSize;
                    bank += highDAO.getHighValue()*betSize;
                }else{
                    bank -= betSize;
                }
            }else if(diff > 0){
                //first win
                if(highDAO.getBetWinner() == WinnerTypes.FIRST){
                    bank -= betSize;
                    bank += highDAO.getHighValue()*betSize;
                }else{
                    bank -= betSize;
                }
            }else if(diff < 0){
                //second win
                if(highDAO.getBetWinner() == WinnerTypes.SECOND){
                    bank -= betSize;
                    bank += highDAO.getHighValue()*betSize;
                }
            }
        }catch (NumberFormatException ex){}
        System.out.println(bank);
    }
    private void doHandicape(HighDAO highDAO) {
        MatchDAO matchDAO = highDAO.getMatch();
        try{
            double firstGoals = Double.parseDouble(matchDAO.getScore().split(":")[0]);
            double secondGoals = Double.parseDouble(matchDAO.getScore().split(":")[1]);
            double handicapeValue = Double.parseDouble(highDAO.getHandicape_total().substring(1));
            if(highDAO.getHandicape_total().endsWith(".25")||highDAO.getHandicape_total().endsWith(".75")){
                double firstGoals_v1 = firstGoals;
                double firstGoals_v2 = firstGoals;
                //dividing bet by 2
                if(highDAO.getHandicape_total().startsWith("+")){
                    firstGoals_v1 += (handicapeValue - 0.25);
                    firstGoals_v2 += (handicapeValue + 0.25);
                }else{
                    firstGoals_v1 -= (handicapeValue - 0.25);
                    firstGoals_v2 -= (handicapeValue + 0.25);
                }
                if(firstGoals_v1 == secondGoals){
                    //returning bet
                    //doing nothing
                }else{
                    if(firstGoals_v1 < secondGoals){
                        if(highDAO.getBetWinner() == WinnerTypes.FIRST){
                            //lose
                            bank -= betSize/2;
                        }else{
                            //win
                            bank -= betSize/2;
                            bank += highDAO.getHighValue()*(betSize/2);
                        }
                    }else{
                        if(highDAO.getBetWinner() == WinnerTypes.FIRST){
                            //win
                            bank -= betSize/2;
                            bank += highDAO.getHighValue()*(betSize/2);
                        }else{
                            //lose
                            bank -= betSize/2;
                        }
                    }
                }

                if(firstGoals_v2 == secondGoals){
                    //returning bet
                    //doing nothing
                }else{
                    if(firstGoals_v2 < secondGoals){
                        if(highDAO.getBetWinner() == WinnerTypes.FIRST){
                            //lose
                            bank -= betSize/2;
                        }else{
                            //win
                            bank -= betSize/2;
                            bank += highDAO.getHighValue()*(betSize/2);
                        }
                    }else{
                        if(highDAO.getBetWinner() == WinnerTypes.FIRST){
                            //win
                            bank -= betSize/2;
                            bank += highDAO.getHighValue()*(betSize/2);
                        }else{
                            //lose
                            bank -= betSize/2;
                        }
                    }
                }
            }else{
                if(highDAO.getHandicape_total().startsWith("+")){
                    firstGoals += handicapeValue;
                }else{
                    firstGoals -= handicapeValue;
                }
                if(firstGoals == secondGoals){
                    //returning bet
                    //doing nothing
                }else{
                    if(firstGoals < secondGoals){
                        if(highDAO.getBetWinner() == WinnerTypes.FIRST){
                            //lose
                            bank -= betSize;
                        }else{
                            //win
                            bank -= betSize;
                            bank += highDAO.getHighValue()*betSize;
                        }
                    }else{
                        if(highDAO.getBetWinner() == WinnerTypes.FIRST){
                            //win
                            bank -= betSize;
                            bank += highDAO.getHighValue()*betSize;
                        }else{
                            //lose
                            bank -= betSize;
                        }
                    }
                }
            }
        }catch (NumberFormatException ex){
        }catch (NullPointerException ex){

        }
    }
    private  void doOverUnder(HighDAO highDAO){
        try{
            String [] goals = highDAO.getMatch().getScore().split(":");
            Integer first = Integer.parseInt(goals[0]);
            Integer second = Integer.parseInt(goals[1]);
            int total = first + second;
            double totalValue = Double.parseDouble(highDAO.getHandicape_total().substring(1));
            if(highDAO.getHandicape_total().endsWith(".25")||highDAO.getHandicape_total().endsWith(".75")){
                //dividing by 2
                double total_v1 = totalValue - 0.25;
                double total_v2 = totalValue + 0.25;
                System.out.println(total + " " + total_v1 + " " + total_v2);
                if(total == total_v1){
                    //doing nothing
                }else{
                    if(total < total_v1){
                        if(highDAO.getBetWinner() == WinnerTypes.OVER){
                            //lose
                            bank -= betSize/2;
                        }else{
                            //win
                            bank -= betSize/2;
                            bank += highDAO.getHighValue()*(betSize/2);
                        }
                    }else{ //total > total_v1
                        if(highDAO.getBetWinner() == WinnerTypes.UNDER){
                            //lose
                            bank -= betSize/2;
                        }else{
                            //lose
                            bank -= betSize/2;
                            bank += highDAO.getHighValue()*(betSize/2);
                        }
                    }
                }
                if(total == total_v2){
                    //doing nothing
                }else{
                    if(total < total_v2){
                        if(highDAO.getBetWinner() == WinnerTypes.OVER){
                            //lose
                            bank -= betSize/2;
                        }else{
                            //win
                            bank -= betSize/2;
                            bank += highDAO.getHighValue()*(betSize/2);
                        }
                    }else{
                        if(highDAO.getBetWinner() == WinnerTypes.UNDER){
                            //win
                            bank -= betSize/2;
                        }else{
                            //lose
                            bank -= betSize/2;
                            bank += highDAO.getHighValue()*(betSize/2);
                        }
                    }
                }
            }else{
                if(total == totalValue){
                    //doing nothing
                }else{
                    if(total < totalValue){
                        if(highDAO.getBetWinner() == WinnerTypes.OVER){
                            //lose
                            bank -= betSize;
                        }else{
                            //win
                            bank -= betSize;
                            bank += highDAO.getHighValue()*betSize;
                        }
                    }else{
                        if(highDAO.getBetWinner() == WinnerTypes.UNDER){
                            //win
                            bank -= betSize;
                        }else{
                            //lose
                            bank -= betSize;
                            bank += highDAO.getHighValue()*betSize;
                        }
                    }
                }
            }
        }catch (NumberFormatException ex){}
        System.out.println(bank);
    }
}
