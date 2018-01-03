package antrix.chopbet.Models;

import java.util.Date;

public class NewMatch {

    private String matchID;
    private String playerOne;
    private String playerTwo;
    private String betAmount;
    private double betFee;
    private long betDate;
    private long index;
    private String betStatus;
    private String betConsole;
    private String betGame;
    private String betInternet;
    private String wonOrLost;
    private Boolean credited;


    public NewMatch(){

    }

    public NewMatch(String matchID, String playerOne, String playerTwo, String betAmount, double betFee, String betConsole, String betGame, String betInternet) {
        this.matchID = matchID;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.betAmount = betAmount;
        this.betFee = betFee;
        this.betConsole = betConsole;
        this.betGame = betGame;
        this.betInternet = betInternet;

        betDate = new Date().getTime();
        index = -1 * betDate;
    }

    public NewMatch(String matchID, String playerOne, String playerTwo, String betAmount, double betFee, String betConsole, String betGame, String betInternet, String wonOrLost) {
        this.matchID = matchID;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.betAmount = betAmount;
        this.betFee = betFee;
        this.betConsole = betConsole;
        this.betGame = betGame;
        this.betInternet = betInternet;
        this.wonOrLost = wonOrLost;

        betDate = new Date().getTime();
        index = -1 * betDate;
    }

    public NewMatch(String matchID, String playerOne, String playerTwo, String betAmount, double betFee, String betConsole, String betGame, String betInternet, String wonOrLost, Boolean credited) {
        this.matchID = matchID;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.betAmount = betAmount;
        this.betFee = betFee;
        this.betConsole = betConsole;
        this.betGame = betGame;
        this.betInternet = betInternet;
        this.wonOrLost = wonOrLost;
        this.credited = credited;

        betDate = new Date().getTime();
        index = -1 * betDate;
    }


    public String getMatchID() {
        return matchID;
    }

    public void setMatchID(String matchID) {
        this.matchID = matchID;
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(String playerOne) {
        this.playerOne = playerOne;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(String playerTwo) {
        this.playerTwo = playerTwo;
    }

    public String getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(String betAmount) {
        this.betAmount = betAmount;
    }

    public double getBetFee() {
        return betFee;
    }

    public void setBetFee(double betFee) {
        this.betFee = betFee;
    }

    public long getBetDate() {
        return betDate;
    }

    public void setBetDate(long betDate) {
        this.betDate = betDate;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getBetStatus() {
        return betStatus;
    }

    public void setBetStatus(String betStatus) {
        this.betStatus = betStatus;
    }

    public String getBetConsole() {
        return betConsole;
    }

    public void setBetConsole(String betConsole) {
        this.betConsole = betConsole;
    }

    public String getBetGame() {
        return betGame;
    }

    public void setBetGame(String betGame) {
        this.betGame = betGame;
    }

    public String getBetInternet() {
        return betInternet;
    }

    public void setBetInternet(String betInternet) {
        this.betInternet = betInternet;
    }

    public String getWonOrLost() {
        return wonOrLost;
    }

    public void setWonOrLost(String wonOrLost) {
        this.wonOrLost = wonOrLost;
    }

    public Boolean getCredited() {
        return credited;
    }

    public void setCredited(Boolean credited) {
        this.credited = credited;
    }
}
