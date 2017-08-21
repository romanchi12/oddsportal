package org.romanchi;

import io.webfolder.ui4j.api.browser.BrowserEngine;
import io.webfolder.ui4j.api.browser.BrowserFactory;
import io.webfolder.ui4j.api.browser.Page;
import io.webfolder.ui4j.api.dom.Document;
import org.dellroad.stuff.spring.RetryTransaction;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.romanchi.DAO.*;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Роман on 29.07.2017.
 */
public class OddsportalParser extends Thread {
    private BrowserEngine webkit = BrowserFactory.getWebKit();
    private Page page = null;
    private Document document = null;
    private ArrayList<String> linkIds = new ArrayList<String>();
    private String login;
    private int startWith;
    private int howMuch;
    public OddsportalParser(int startWith, int howMuch){
        this.login("romanchi","frdfhtkm12");
        this.startWith = startWith;
        this.howMuch = howMuch;
    }

    /*Starts here*/
    @Override
    public void run() {
        try {
            this.parseMatchesBetsAndHandicapesAndOverUnder();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*Login*/
    public void login(String login, String password) {
        this.login = login;
        if(isLogined()){return;}
        page = webkit.navigate("http://www.oddsportal.com/");
        document = page.getDocument();
        boolean isLogined = false;
        while(!isLogined){
            try{
                System.out.println(document.query("#login-username").get());
                document.query("#login-username").get().setValue(this.login);
                document.query("#login-password").get().setValue(password);
                document.query("[name='login-submit']").get().click();
                isLogined = true;
            }catch (NoSuchElementException exception){}
        }
    }
    public boolean isLogined() {
        page = webkit.navigate("http://www.oddsportal.com/");
        document = page.getDocument();
        return document.query("#user-header").get().getInnerHTML().contains(login);
    }

    /*Getting links and parsing matchs from urls like '%/results/%'
    *
    * 1. initializing links to parse by calling methods (one of them): parseLinkIdsLinkIds, initializeLinkIds
    * 2. Start parsing match by calling method parseAllYears
    *
    * */
    public void parseLinkIdsLinkIds() {
        page = webkit.navigate("http://www.oddsportal.com/soccer/spain/laliga/results/");
        document = page.getDocument();
        String data = document.getBody().getOuterHTML();
        org.jsoup.nodes.Document doc = Jsoup.parse(data);
        Elements linkNodes = doc.select(".main-menu-gray .main-filter>li a");
        for (Element linkNode : linkNodes) {
            try {
                String link = "http://www.oddsportal.com" + linkNode.attr("href");
                linkIds.add(link);
            } catch (NullPointerException ex) {}
        }
        System.out.println(linkIds);
    }
    public void initializeLinkIds(){
        linkIds.add("http://www.oddsportal.com/soccer/england/premier-league-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/finland/veikkausliiga-2016/results/");
        linkIds.add("http://www.oddsportal.com/soccer/france/ligue-1-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/germany/bundesliga-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/germany/2-bundesliga-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/greece/super-league-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/ireland/premier-division-2016/results/");
        linkIds.add("http://www.oddsportal.com/soccer/italy/serie-a-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/italy/serie-b-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/netherlands/eredivisie-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/norway/tippeligaen-2016/results/");
        linkIds.add("http://www.oddsportal.com/soccer/poland/ekstraklasa-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/portugal/primeira-liga-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/scotland/premiership-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/spain/laliga-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/spain/laliga2-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/sweden/allsvenskan-2016/results/");
        linkIds.add("http://www.oddsportal.com/soccer/switzerland/super-league-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/turkey/super-lig-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/czech-republic/1-liga-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/denmark/superliga-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/belgium/jupiler-league-2016-2017/results/");
        linkIds.add("http://www.oddsportal.com/soccer/austria/tipico-bundesliga-2016-2017/results/");

    }
    private boolean parseMatchData(String data, LigaDAO ligaDAO) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.jsoup.nodes.Document d = Jsoup.parse(data);
        Elements rows = d.select("#tournamentTable .table-main tbody tr.deactivate");
        if (rows.isEmpty()) {
            return false;
        }
        session.beginTransaction();
        for (org.jsoup.nodes.Element row : rows) {
            Elements columns = row.select("td");
            Element timeNode = columns.get(0);
            String time = timeNode.text();
            Element matchNode = columns.get(1).select("a").get(0);
            String matchName = matchNode.text();
            String matchLink = "http://www.oddsportal.com" + matchNode.attr("href");
            Element scoreNode = columns.get(2);
            String score = scoreNode.text();
            Element firstNode = columns.get(3);
            String first = firstNode.text();
            Element drawNode = columns.get(4);
            String draw = drawNode.text();
            Element secondNode = columns.get(5);
            String second = secondNode.text();
            Element availablesBookmakersNode = columns.get(6);
            String availableBookmakers = availablesBookmakersNode.text();
            int winner = -1;
            if (firstNode.hasClass("result-ok")) {
                winner = 1;
            } else if (secondNode.hasClass("result-ok")) {
                winner = 2;
            } else if (drawNode.hasClass("result-ok")) {
                winner = 0;
            }
            MatchDAO matchDAO = new MatchDAO(winner, time, matchName, matchLink, score, first, draw, second, availableBookmakers, ligaDAO);
            session.save(matchDAO);
            System.out.println(matchDAO);
        }
        session.flush();
        session.getTransaction().commit();
        return true;
    }
    private boolean parseMatches(String link, LigaDAO ligaDAO) throws InterruptedException {
        System.out.println(link);
        page = webkit.navigate(link);
        document = page.getDocument();
        String data = document.getBody().getOuterHTML();
        int trying = 0;
        while (!parseMatchData(data, ligaDAO)) {
            if (trying >= 5) {
                return false;
            }
            System.out.println("Trying: " + link);
            trying++;
            page = webkit.navigate(link);
            document = page.getDocument();
            data = document.getBody().getOuterHTML();
        }
        System.gc();
        return true;
    }
    public boolean parseAllYears() throws InterruptedException {
        initializeLinkIds();
        for (int linkId = 0; linkId < linkIds.size(); linkId++) {
            LigaDAO ligaDAO = new LigaDAO(linkIds.get(linkId));
            for (int pageNumber = 1; parseMatches(linkIds.get(linkId) + "#/page/" + pageNumber + "/", ligaDAO); pageNumber++) {
            }
            System.out.println("okey, done");
        }
        System.out.println("parsed");
        return true;
    }


    /*      Bet
    * Following methods parse pages 1X2
    * Start point: parseMatchBets which receive matchDAO as parameter
    * method parseBetData provides trying mechanism and is private
    * */
    @RetryTransaction
    @Transactional
    private boolean parseBetData(String data, MatchDAO matchDAO) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.jsoup.nodes.Document document = Jsoup.parse(data);
        Elements tabs = document.select("#tab-nav-main li[style=display: block;]");
        if(tabs.isEmpty()){
            session.clear();
            session.close();
            return false;
        }
        boolean isRightTab = false;
        for (Element tab:tabs){
            if((tab.hasClass("active")&&tab.text().contains("1X2"))){
                isRightTab = true;
            }
        }
        if(!isRightTab){
            session.clear();
            session.close();
            return true;
        }
        Elements containers = document.select("#odds-data-table .table-container");
        Elements rows = null;
        if (!containers.isEmpty()) {
            rows = containers.get(0).select("tbody tr");
        } else {
            session.clear();
            session.close();
            return false;
        }
        if (rows.isEmpty()) {
            session.clear();
            session.close();
            return false;
        }
        Element highestNode = containers.get(0).select("tfoot tr.highest").get(0);
        Elements highestColumns = highestNode.select("td");
        Element highestFirst = highestColumns.get(1);
        Element highestDraw = highestColumns.get(2);
        Element highestSecond = highestColumns.get(3);
        try{
            double highestFirstValue = Double.parseDouble(highestFirst.text());
            double highestDrawValue = Double.parseDouble(highestDraw.text());
            double highestSecondValue = Double.parseDouble(highestSecond.text());
            double highestKoefficient = 1/highestFirstValue + 1/highestDrawValue + 1/highestSecondValue;
            if(highestKoefficient >= 0.995){
                session.clear();
                session.close();
                return true;
            }
        }catch (NumberFormatException ex){
            session.clear();
            session.close();
            return true;
        }
        Transaction transaction = null;
        int trying = 5;
        while(trying > 0){
            try{
                transaction = session.beginTransaction();
                for (Element row : rows) {
                    Elements columns = row.select("td");
                    int hight = row.select(".high").size();
                    if (!columns.isEmpty()) {
                        Element bookmakerNode = columns.get(0).select("a").get(1);
                        String bookmakerName = bookmakerNode.text();
                        BookmakerDAO bookmakerDAO = Database.getBookmakerByName(bookmakerName);
                        if(bookmakerDAO == null){
                            bookmakerDAO = new BookmakerDAO(bookmakerName, hight);
                            session.saveOrUpdate(bookmakerDAO);
                        }else{
                            int hights = (bookmakerDAO.getHights() == null) ? hight : bookmakerDAO.getHights() + hight;
                            bookmakerDAO.setHights(hights);
                            session.merge(bookmakerDAO);
                        }
                        session.flush();
                        Element firstNode = columns.get(1);
                        String first = firstNode.text();
                        if(firstNode.hasClass("high")){
                            HighDAO highDAO = new HighDAO(first, WinnerTypes.FIRST, HighTypes.BET,null, matchDAO, bookmakerDAO);
                            session.merge(highDAO);
                        }
                        Element drawNode = columns.get(2);
                        String draw = drawNode.text();
                        if(drawNode.hasClass("high")){
                            HighDAO highDAO = new HighDAO(draw, WinnerTypes.DRAW, HighTypes.BET,null, matchDAO, bookmakerDAO);
                            session.merge(highDAO);
                        }
                        Element secondNode = columns.get(3);
                        String second = secondNode.text();
                        if(secondNode.hasClass("high")){
                            HighDAO highDAO = new HighDAO(second, WinnerTypes.SECOND, HighTypes.BET,null, matchDAO, bookmakerDAO);
                            session.merge(highDAO);
                        }
                        Element payoutNode = columns.get(4);
                        String payout = payoutNode.text();
                        BetDAO betDAO = new BetDAO(bookmakerName,first, draw, second, payout, matchDAO);
                        session.save(betDAO);
                        session.flush();
                    }
                }
                try{
                    session.flush();
                    transaction.commit();
                }catch (NullPointerException ex){
                    System.out.println("commit failed");
                }
                trying = 0;
            }catch (RuntimeException ex){
                if(transaction != null){
                    transaction.rollback();
                    trying--;
                }
            }
        }

        session.clear();
        session.close();
        return true;
    }
    public boolean parseMatchBets(MatchDAO matchDAO) {
        page = webkit.navigate(matchDAO.getMatchLink());
        document = page.getDocument();
        String data = document.getBody().getOuterHTML();
        page.close();
        int trying = 0;
        while (!parseBetData(data, matchDAO)) {
            if (trying >= 5) {
                return false;
            }
            System.out.println("Trying: " + matchDAO.getMatchLink());
            trying++;
            page = webkit.navigate(matchDAO.getMatchLink());
            document = page.getDocument();
            data = document.getBody().getOuterHTML();
            page.close();
        }
        return true;
    }


    /*Handicape
    *
    *
    * Following methods parse pages Asian Handicap
    * Same as Bet methods
    *
    * */
    @RetryTransaction
    @Transactional
    private boolean parseHandicapeData(String data, MatchDAO matchDAO) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.jsoup.nodes.Document document = Jsoup.parse(data);
        Elements tabs = document.select("#bettype-tabs li[style=display: block;]");
        if (tabs.isEmpty()) {
            session.clear();
            session.close();
            return false;
        }
        boolean isRightTab = false;
        for (Element tab : tabs) {
            if ((tab.hasClass("active") && tab.text().contains("Asian Handicap"))) {
                isRightTab = true;
            }
        }
        if (!isRightTab) {
            session.clear();
            session.close();
            return true;
        }
        Elements tables = document.select("#odds-data-table .table-container .table-main");
        HashMap<String, Integer> bookmakersMap = new HashMap<>();
        if (tables.isEmpty()) {
            session.clear();
            session.close();
            return false;
        }
        for (Element table : tables) {
            Elements highestNodes = table.select("tfoot tr.highest");
            if (highestNodes.isEmpty()) {
                continue;
            }
            Elements highestColumns = highestNodes.get(0).select("td");
            Element highestFirst = highestColumns.get(2);
            Element highestSecond = highestColumns.get(3);
            try {
                double highestFirstValue = Double.parseDouble(highestFirst.text());
                double highestSecondValue = Double.parseDouble(highestSecond.text());
                double highestKoefficient = 1 / highestFirstValue + 1 / highestSecondValue;
                if (highestKoefficient >= 0.995) {
                    continue;
                }
            } catch (NumberFormatException ex) {
                session.clear();
                session.close();
                return true;
            }
            Elements rows = table.select("tbody .lo");
            Transaction transaction = null;
            int trying = 5;
            while (trying > 0) {
                try {
                    transaction = session.beginTransaction();
                    for (Element row : rows) {
                        int hight = row.select(".high").size();
                        Elements columns = row.select("td");
                        if (columns.isEmpty()) {
                            continue;
                        }
                        Element bookmakerNode = columns.get(0).select("a").get(1);
                        String bookmakerName = bookmakerNode.text();
                        if (bookmakersMap.get(bookmakerName) == null) {
                            bookmakersMap.put(bookmakerName, hight);
                        } else {
                            bookmakersMap.put(bookmakerName, bookmakersMap.get(bookmakerName) + hight);
                        }
                        Element handicapeNode = columns.get(1);
                        String handicape = handicapeNode.text();
                        Element firstHandicapeNode = columns.get(2);
                        String firstHandicape = firstHandicapeNode.text();
                        BookmakerDAO bookmakerDAO = Database.getBookmakerByName(bookmakerName);
                        if (Database.getBookmakerByName(bookmakerName) == null) {
                            bookmakerDAO = new BookmakerDAO(bookmakerName, 0);
                            session.merge(bookmakerDAO);
                            session.flush();
                        }
                        if (firstHandicapeNode.hasClass("high")) {
                            HighDAO highDAO = new HighDAO(firstHandicape, WinnerTypes.FIRST, HighTypes.HANDICAPE, handicape, matchDAO, bookmakerDAO);
                            session.merge(highDAO);
                            session.flush();
                        }
                        Element secondHandicapeNode = columns.get(3);
                        String secondHandicape = secondHandicapeNode.text();
                        if (secondHandicapeNode.hasClass("high")) {
                            HighDAO highDAO = new HighDAO(secondHandicape, WinnerTypes.SECOND, HighTypes.HANDICAPE, handicape, matchDAO, bookmakerDAO);
                            session.merge(highDAO);
                            session.flush();
                        }
                        Element payoutNode = columns.get(4);
                        String payout = payoutNode.text();
                    }
                    try{
                        session.flush();
                        transaction.commit();
                    }catch (NullPointerException ex){
                        System.out.println("commit failed");
                    }
                    trying = 0;
                } catch (RuntimeException ex) {
                    if (transaction != null) {
                        transaction.rollback();
                        trying--;
                    }
                }
            }
            transaction = session.beginTransaction();
            trying = 5;
            while (trying > 0) {
                try {
                    bookmakersMap.forEach((key, value) -> {
                        BookmakerDAO bookmakerDAO = Database.getBookmakerByName(key);
                        if (bookmakerDAO == null) {
                            bookmakerDAO = new BookmakerDAO(key, value);
                        } else {
                            bookmakerDAO.setHights(bookmakerDAO.getHights() + bookmakersMap.get(key));
                        }
                        session.merge(bookmakerDAO);
                    });
                    try{
                        session.flush();
                        transaction.commit();
                    }catch (NullPointerException ex){
                        System.out.println("commit failed");
                    }
                    trying = 0;
                } catch (RuntimeException ex) {
                    if (transaction != null) {
                        transaction.rollback();
                        trying--;
                    }
                }
            }
        }
        session.clear();
        session.close();
        return true;
    }
    public boolean parseMatchHandicape(MatchDAO match) throws InterruptedException {
        String matchLink = match.getMatchLink();
        page = webkit.navigate(matchLink + "#ah;2");
        document = page.getDocument();
        document.queryAll(".table-container strong a").forEach(strong -> strong.click());
        String data = document.getBody().getOuterHTML();
        page.close();
        int trying = 0;
        while (!parseHandicapeData(data, match)) {
            trying++;
            if (trying >= 5) {
                return false;
            }
            System.out.println("Trying: " + matchLink);
            page = webkit.navigate(matchLink + "#ah;2");
            document = page.getDocument();
            try{
                document.queryAll(".table-container strong a").forEach(strong -> strong.click());
            }catch (NullPointerException ex){
                return false;
            }
            data = document.getBody().getOuterHTML();
            page.close();
        }
        return true;
    }

    /*Over Under
    *
    * Following methods parse pages Over/Under
    * Same as Bet methods
    * */
    @RetryTransaction
    @Transactional
    private boolean parseOverUnderData(String data, MatchDAO matchDAO) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.jsoup.nodes.Document document = Jsoup.parse(data);
        Elements tabs = document.select("#tab-nav-main li[style=display: block;]");
        if(tabs.isEmpty()){
            session.clear();
            session.close();
            return false;
        }
        boolean isRightTab = false;
        for (Element tab:tabs){
            if((tab.hasClass("active")&&tab.text().contains("Over/Under"))){
                isRightTab = true;
            }
        }
        if(!isRightTab){
            session.clear();
            session.close();
            return true;
        }
        Elements tables = document.select("#odds-data-table .table-container .table-main");
        HashMap<String, Integer> bookmakersMap = new HashMap<>();
        if(tables.isEmpty()){
            session.clear();
            session.close();
            return false;
        }
        for(Element table:tables){
            Elements highestNodes = table.select("tfoot tr.highest");
            if(highestNodes.isEmpty()){
                continue;
            }
            Elements highestColumns = highestNodes.get(0).select("td");
            Element highestFirst = highestColumns.get(2);
            Element highestSecond = highestColumns.get(3);
            try{
                double highestFirstValue = Double.parseDouble(highestFirst.text());
                double highestSecondValue = Double.parseDouble(highestSecond.text());
                double highestKoefficient = 1/highestFirstValue + 1/highestSecondValue;
                if(highestKoefficient >= 0.995){
                    continue;
                }
            }catch (NumberFormatException ex){
                session.clear();
                session.close();
                return true;
            }
            Elements rows = table.select("tbody .lo");
            Transaction transaction = null;
            int trying = 5;
            while(trying > 0){
                try{
                    transaction = session.beginTransaction();
                    for(Element row:rows){
                        int hight = row.select(".high").size();
                        Elements columns = row.select("td");
                        if(columns.isEmpty()){
                            continue;
                        }
                        Element bookmakerNode = columns.get(0).select("a").get(1);
                        String bookmakerName = bookmakerNode.text();
                        if(bookmakersMap.get(bookmakerName) == null){
                            bookmakersMap.put(bookmakerName, hight);
                        }else{
                            bookmakersMap.put(bookmakerName, bookmakersMap.get(bookmakerName) + hight);
                        }
                        Element totalNode = columns.get(1);
                        String total = totalNode.text();
                        Element overNode = columns.get(2);
                        String over = overNode.text();
                        BookmakerDAO bookmakerDAO = Database.getBookmakerByName(bookmakerName);
                        if(Database.getBookmakerByName(bookmakerName) == null){
                            bookmakerDAO = new BookmakerDAO(bookmakerName, 0);
                            session.merge(bookmakerDAO);
                            session.flush();
                        }
                        if(overNode.hasClass("high")){
                            HighDAO highDAO = new HighDAO(over, WinnerTypes.OVER, HighTypes.OVER_UNDER,total, matchDAO, bookmakerDAO);
                            session.merge(highDAO);
                            session.flush();
                        }
                        Element underNode = columns.get(3);
                        String under = underNode.text();
                        if(underNode.hasClass("high")){
                            HighDAO highDAO = new HighDAO(under,WinnerTypes.UNDER, HighTypes.OVER_UNDER,total, matchDAO, bookmakerDAO);
                            session.merge(highDAO);
                            session.flush();
                        }
                        Element payoutNode = columns.get(4);
                        String payout = payoutNode.text();
                    }
                    try{
                        session.flush();
                        transaction.commit();
                    }catch (NullPointerException ex){
                        System.out.println("commit failed");
                    }
                    trying = 0;
                }catch (RuntimeException ex){
                    if(transaction != null){
                        transaction.rollback();
                        trying--;
                    }
                }
            }

        }
        Transaction transaction = null;
        int trying = 5;
        while(trying > 0){
            try{
                transaction = session.beginTransaction();
                bookmakersMap.forEach((key,value)->{
                    BookmakerDAO bookmakerDAO = Database.getBookmakerByName(key);
                    if(bookmakerDAO == null){
                        bookmakerDAO = new BookmakerDAO(key, value);
                    }else{
                        bookmakerDAO.setHights(bookmakerDAO.getHights() + bookmakersMap.get(key));
                    }
                    session.merge(bookmakerDAO);
                });
                try{
                    session.flush();
                    transaction.commit();
                }catch (NullPointerException ex){
                    System.out.println("commit failed");
                }
                trying = 0;
            }catch (RuntimeException ex){
                if(transaction != null){
                    transaction.rollback();
                    trying--;
                }
            }
        }
        session.clear();
        session.close();
        return true;
    }
    public boolean parseMatchOverUnder(MatchDAO match) throws InterruptedException {
        String matchLink = match.getMatchLink();
        page = webkit.navigate(matchLink + "#over-under;2");
        document = page.getDocument();
        document.queryAll(".table-container strong a").forEach(strong -> strong.click());
        String data = document.getBody().getOuterHTML();
        page.close();
        int trying = 0;
        while (!parseOverUnderData(data, match)) {
            trying++;
            if (trying >= 5) {
                return false;
            }
            System.out.println("Trying: " + matchLink);
            page = webkit.navigate(matchLink + "#over-under;2");
            document = page.getDocument();
            try{
                document.queryAll(".table-container strong a").forEach(strong -> strong.click());
            }catch (NullPointerException ex){
                return false;
            }
            data = document.getBody().getOuterHTML();
            page.close();
        }
        return true;
    }



    /*Bets and Handicapes and OverUnder

    * Start point method
    *
    * */
    public boolean parseMatchesBetsAndHandicapesAndOverUnder() throws InterruptedException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<MatchDAO> criteriaQuery = criteriaBuilder.createQuery(MatchDAO.class);
        Root<MatchDAO> root = criteriaQuery.from(MatchDAO.class);
        criteriaQuery.select(root);
        TypedQuery<MatchDAO> typedQuery = session.createQuery(criteriaQuery);
        List<MatchDAO> packList = typedQuery.setFirstResult(startWith).setMaxResults(this.howMuch).getResultList();
        for (MatchDAO match : packList) {
            try{
                System.out.println(match.getMatchId() + " " + match.getMatchLink());
                parseMatchBets(match);
                parseMatchHandicape(match);
                parseMatchOverUnder(match);
                System.gc();
            }catch (InterruptedException ex){
                session.clear();
                session.close();
                return true;
            }
        }
        session.clear();
        session.close();
        return true;
    }

}
