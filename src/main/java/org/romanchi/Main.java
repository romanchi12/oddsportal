package org.romanchi;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.romanchi.controllers.ControllerManager;
import java.io.*;
/*
*   http://www.oddsportal.com/soccer/england/premier-league/

        http://www.oddsportal.com/soccer/finland/veikkausliiga/
        http://www.oddsportal.com/soccer/france/ligue-1/
        http://www.oddsportal.com/soccer/germany/bundesliga/
        http://www.oddsportal.com/soccer/germany/2-bundesliga/
        http://www.oddsportal.com/soccer/greece/super-league/
        http://www.oddsportal.com/soccer/ireland/premier-division/
        http://www.oddsportal.com/soccer/italy/serie-a/
        http://www.oddsportal.com/soccer/italy/serie-b/
        http://www.oddsportal.com/soccer/netherlands/eredivisie/
        http://www.oddsportal.com/soccer/norway/eliteserien/
        http://www.oddsportal.com/soccer/poland/ekstraklasa/
        http://www.oddsportal.com/soccer/portugal/primeira-liga/
        http://www.oddsportal.com/soccer/scotland/premiership/
        http://www.oddsportal.com/soccer/spain/laliga/
        http://www.oddsportal.com/soccer/spain/laliga2/
        http://www.oddsportal.com/soccer/sweden/allsvenskan/
        http://www.oddsportal.com/soccer/switzerland/super-league/
        http://www.oddsportal.com/soccer/turkey/super-lig/
        http://www.oddsportal.com/soccer/czech-republic/1-liga/
        http://www.oddsportal.com/soccer/denmark/superliga/
        http://www.oddsportal.com/soccer/belgium/jupiler-league/
        http://www.oddsportal.com/soccer/austria/tipico-bundesliga/
*
*
* */

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Scene oddsPortalScene = ControllerManager.changeSceneTo("UserCtrl","UserView");
        primaryStage.setScene(oddsPortalScene);
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        OddsportalParser.login("romanchi", "frdfhtkm12");
        //launch(args);
       OddsportalParser.parseMatchesBetsAndHandicapesAndOverUnder();

    }
}


