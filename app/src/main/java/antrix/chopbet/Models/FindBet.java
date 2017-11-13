package antrix.chopbet.Models;

import java.util.Date;

public class FindBet {

    String findID;
    String finderUserName;
    String betAmount;
    String findStatus;
    String internet;
    long findTime;
    long index;


    public FindBet(){
    }

    public FindBet(String findID, String finderName, String betAmount, String findStatus, String internet) {
        this.findID = findID;
        this.finderUserName = finderName;
        this.betAmount = betAmount;
        this.findStatus = findStatus;
        this.internet = internet;

        findTime = new Date().getTime();
        index = -1 * findTime;

    }

    public String getFindID() {
        return findID;
    }

    public void setFindID(String findID) {
        this.findID = findID;
    }

    public String getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(String betAmount) {
        this.betAmount = betAmount;
    }

    public String getFindStatus() {
        return findStatus;
    }

    public void setFindStatus(String findStatus) {
        this.findStatus = findStatus;
    }

    public long getFindTime() {
        return findTime;
    }

    public void setFindTime(long findTime) {
        this.findTime = findTime;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getFinderUserName() {
        return finderUserName;
    }

    public void setFinderUserName(String finderUserName) {
        this.finderUserName = finderUserName;
    }

    public String getInternet() {
        return internet;
    }

    public void setInternet(String internet) {
        this.internet = internet;
    }
}
