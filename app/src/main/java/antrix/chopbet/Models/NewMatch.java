package antrix.chopbet.Models;

import java.util.Date;

public class NewMatch {

    private String matchID;
    private String playerOne;
    private String playerTwo;
    private String betAmount;
    private String betFee;
    private long betDate;
    private long index;
    private String betStatus;


    public NewMatch(){

    }

    public NewMatch(String matchID, String playerOne, String playerTwo, String betAmount, String betFee) {
        this.matchID = matchID;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.betAmount = betAmount;
        this.betFee = betFee;

        betDate = new Date().getTime();
        index = -1 * betDate;
    }

    public NewMatch(String matchID, String playerOne, String playerTwo, String betAmount, String betFee, String betStatus) {
        this.matchID = matchID;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.betAmount = betAmount;
        this.betFee = betFee;
        this.betStatus = betStatus;

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

    public String getBetFee() {
        return betFee;
    }

    public void setBetFee(String betFee) {
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
}
