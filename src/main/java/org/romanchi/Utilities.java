package org.romanchi;


import org.romanchi.DAO.HighDAO;
import org.romanchi.DAO.HighTypes;
import org.romanchi.DAO.MatchDAO;
import org.romanchi.DAO.WinnerTypes;


/**
 * Created by Роман on 28.07.2017.
 */
public class Utilities {
    public static int whoWins(MatchDAO matchDAO, HighDAO highDAO){
        String[] goals = matchDAO.getScore().split(":");
        try{
            Integer first = Integer.parseInt(goals[0]);
            Integer second = Integer.parseInt(goals[1]);
            switch (highDAO.getType()){
                case HighTypes.BET:{
                    Integer diff = first - second;
                    if(diff == 0){
                        return WinnerTypes.DRAW;
                    }else if(diff > 0){
                        return WinnerTypes.FIRST;
                    }else{
                        return WinnerTypes.SECOND;
                    }
                }
                case HighTypes.HANDICAPE:{

                }
                case HighTypes.OVER_UNDER:{
                    int totalGoals = first + second;
                    double total = Double.parseDouble(highDAO.getHandicape_total());

                }
            }
        }catch (NumberFormatException ex){
            System.out.println("NumberFormatException: " + ex.getLocalizedMessage());
        }
        return -1;
    }
    public static int totalGoals(MatchDAO matchDAO){
        String[] goals = matchDAO.getScore().split(":");
        try{
            Integer first = Integer.parseInt(goals[0]);
            Integer second = Integer.parseInt(goals[1]);
            return first + second;
        }catch (NumberFormatException ex){
            ex.printStackTrace();
            return -1;
        }
    }
}
