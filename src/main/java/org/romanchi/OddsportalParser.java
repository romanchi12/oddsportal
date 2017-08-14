package org.romanchi;

import io.webfolder.ui4j.api.browser.BrowserEngine;
import io.webfolder.ui4j.api.browser.BrowserFactory;
import io.webfolder.ui4j.api.browser.Page;
import io.webfolder.ui4j.api.dom.Document;
import org.hibernate.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.romanchi.DAO.*;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Роман on 29.07.2017.
 */
public class OddsportalParser {
    private static BrowserEngine webkit = BrowserFactory.getWebKit();
    private static Page page = null;
    private static Document document = null;
    private static ArrayList<String> linkIds = new ArrayList<String>();
    private static String login = "romanchi";

    /*Login*/
    public static void login(String login, String password) {
        OddsportalParser.login = login;
        page = webkit.navigate("http://www.oddsportal.com/");
        //page.show(true);
        document = page.getDocument();
        // set the searh criteria
        document.query("#login-username").get().setValue(login);
        document.query("#login-password").get().setValue(password);
        document.query("[name='login-submit']").get().click();
    }

    public static boolean isLogined() {
        page = webkit.navigate("http://www.oddsportal.com/");
        document = page.getDocument();
        return document.query("#user-header").get().getInnerHTML().contains(OddsportalParser.login);
    }

    /*Getting rusults links*/
    public static void parseLinkIdsLinkIds() {
        page = webkit.navigate("http://www.oddsportal.com/soccer/spain/laliga/results/");
        document = page.getDocument();
        String data = document.getBody().getOuterHTML();
        org.jsoup.nodes.Document doc = Jsoup.parse(data);
        Elements linkNodes = doc.select(".main-menu-gray .main-filter>li a");
        for (Element linkNode : linkNodes) {
            try {
                String link = "http://www.oddsportal.com" + linkNode.attr("href");
                linkIds.add(link);
            } catch (NullPointerException ex) {

            }
        }
        System.out.println(linkIds);
    }
    public static void initializeLinkIds(){
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
    /*Parse matches*/
    private static boolean parseMatchData(String data, LigaDAO ligaDAO) {
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

    private static boolean parseMatches(String link, LigaDAO ligaDAO) throws InterruptedException {
        System.out.println(link);
        page = webkit.navigate(link);
        document = page.getDocument();
        String data = document.getBody().getOuterHTML();
        //System.out.println(data);
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

    public static boolean parseAllYears() throws InterruptedException {
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

    /*Bet*/
    private static boolean parseBetData(String data, MatchDAO matchDAO) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();
        org.jsoup.nodes.Document document = Jsoup.parse(data);

        Elements tabs = document.select("#tab-nav-main li[style=display: block;]");
        if(tabs.isEmpty()){
            session.flush();
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
            session.flush();
            session.clear();
            session.close();
            return true;
        }
        Elements containers = document.select("#odds-data-table .table-container");
        Elements rows = null;
        if (!containers.isEmpty()) {
            rows = containers.get(0).select("tbody tr");
        } else {
            session.flush();
            session.clear();
            session.close();
            return false;
        }
        if (rows.isEmpty()) {
            session.flush();
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
            if(highestKoefficient >= 1){
                session.flush();
                session.clear();
                session.close();
                return true;
            }
        }catch (NumberFormatException ex){
            session.flush();
            session.clear();
            session.close();
            return true;
        }
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
        session.flush();
        session.getTransaction().commit();
        session.clear();
        session.close();
        return true;
    }

    public static boolean parseMatchBets(MatchDAO matchDAO) {
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


    /*Handicape*/
    private static boolean parseHandicapeData(String data, MatchDAO matchDAO) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        org.jsoup.nodes.Document document = Jsoup.parse(data);
        Elements tabs = document.select("#bettype-tabs li[style=display: block;]");
        if(tabs.isEmpty()){
            session.flush();
            session.clear();
            session.close();
            return false;
        }
        boolean isRightTab = false;
        for (Element tab:tabs){
            if((tab.hasClass("active")&&tab.text().contains("Asian Handicap"))){
                isRightTab = true;
            }
        }
        if(!isRightTab){
            session.flush();
            session.clear();
            session.close();
            return true;
        }
        Elements tables = document.select("#odds-data-table .table-container .table-main");
        HashMap<String, Integer> bookmakersMap = new HashMap<>();
        if(tables.isEmpty()){
            session.flush();
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
                if(highestKoefficient >= 1){
                    continue;
                }
            }catch (NumberFormatException ex){
                session.close();
                return true;
            }
            Elements rows = table.select("tbody .lo");
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
                Element handicapeNode = columns.get(1);
                String handicape = handicapeNode.text();
                Element firstHandicapeNode = columns.get(2);
                String firstHandicape = firstHandicapeNode.text();
                BookmakerDAO bookmakerDAO = Database.getBookmakerByName(bookmakerName);
                if(Database.getBookmakerByName(bookmakerName) == null){
                    bookmakerDAO = new BookmakerDAO(bookmakerName, 0);
                    session.merge(bookmakerDAO);
                    session.flush();
                }
                if(firstHandicapeNode.hasClass("high")){
                    HighDAO highDAO = new HighDAO(firstHandicape, WinnerTypes.FIRST, HighTypes.HANDICAPE,handicape, matchDAO, bookmakerDAO);
                    session.merge(highDAO);
                    session.flush();
                }
                Element secondHandicapeNode = columns.get(3);
                String secondHandicape = secondHandicapeNode.text();
                if(secondHandicapeNode.hasClass("high")){
                    HighDAO highDAO = new HighDAO(secondHandicape,WinnerTypes.SECOND, HighTypes.HANDICAPE,handicape, matchDAO, bookmakerDAO);
                    session.merge(highDAO);
                    session.flush();
                }
                Element payoutNode = columns.get(4);
                String payout = payoutNode.text();
            }
        }
        bookmakersMap.forEach((key,value)->{
            BookmakerDAO bookmakerDAO = Database.getBookmakerByName(key);
            if(bookmakerDAO == null){
                bookmakerDAO = new BookmakerDAO(key, value);
            }else{
                bookmakerDAO.setHights(bookmakerDAO.getHights() + bookmakersMap.get(key));
            }
            session.merge(bookmakerDAO);
        });
        session.flush();
        session.getTransaction().commit();
        session.clear();
        session.close();
        return true;
    }

    public static boolean parseMatchHandicape(MatchDAO match) throws InterruptedException {
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

    /*Over Under*/
    private static boolean parseOverUnderData(String data, MatchDAO matchDAO) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        org.jsoup.nodes.Document document = Jsoup.parse(data);
        Elements tabs = document.select("#tab-nav-main li[style=display: block;]");
        if(tabs.isEmpty()){
            session.flush();
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
            session.flush();
            session.clear();
            session.close();
            return true;
        }
        Elements tables = document.select("#odds-data-table .table-container .table-main");
        HashMap<String, Integer> bookmakersMap = new HashMap<>();
        if(tables.isEmpty()){
            session.flush();
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
                if(highestKoefficient >= 1){
                    continue;
                }
            }catch (NumberFormatException ex){
                session.flush();
                session.clear();
                session.close();
                return true;
            }
            Elements rows = table.select("tbody .lo");
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
        }
        bookmakersMap.forEach((key,value)->{
            BookmakerDAO bookmakerDAO = Database.getBookmakerByName(key);
            if(bookmakerDAO == null){
                bookmakerDAO = new BookmakerDAO(key, value);
            }else{
                bookmakerDAO.setHights(bookmakerDAO.getHights() + bookmakersMap.get(key));
            }
            session.merge(bookmakerDAO);
        });
        session.flush();
        session.getTransaction().commit();
        session.clear();
        session.close();
        return true;
    }

    public static boolean parseMatchOverUnder(MatchDAO match) throws InterruptedException {
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
    /*Bets and Handicapes*/
    public static boolean parseMatchesBetsAndHandicapesAndOverUnder(int startWith) throws InterruptedException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<MatchDAO> criteriaQuery = criteriaBuilder.createQuery(MatchDAO.class);
        Root<MatchDAO> root = criteriaQuery.from(MatchDAO.class);
        criteriaQuery.select(root);
        TypedQuery<MatchDAO> typedQuery = session.createQuery(criteriaQuery);
        List<MatchDAO> packList = null;
        for (int pack = startWith; !(packList = typedQuery.setFirstResult(pack).setMaxResults(100).getResultList()).isEmpty(); pack += 100) {
            for (MatchDAO match : packList) {
                System.out.println(match.getMatchId() + " " + match.getMatchLink());
                parseMatchBets(match);
                parseMatchHandicape(match);
                parseMatchOverUnder(match);
                System.gc();
            }
        }
        session.clear();
        session.close();
        return true;
    }

    public static boolean parseMatchesBetsAndHandicapesAndOverUnder() throws InterruptedException {
        return parseMatchesBetsAndHandicapesAndOverUnder(0);
    }


    public static boolean parseMatchesOverUnder(int startWith) throws InterruptedException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<MatchDAO> criteriaQuery = criteriaBuilder.createQuery(MatchDAO.class);
        Root<MatchDAO> root = criteriaQuery.from(MatchDAO.class);
        criteriaQuery.select(root);
        TypedQuery<MatchDAO> typedQuery = session.createQuery(criteriaQuery);
        List<MatchDAO> packList = null;
        for (int pack = startWith; !(packList = typedQuery.setFirstResult(pack).setMaxResults(100).getResultList()).isEmpty(); pack += 100) {
            for (MatchDAO match : packList) {
                System.out.println(match.getMatchId() + " " + match.getMatchLink());
                parseMatchOverUnder(match);
                System.gc();
            }
        }
        session.clear();
        session.close();
        return true;
    }
}
