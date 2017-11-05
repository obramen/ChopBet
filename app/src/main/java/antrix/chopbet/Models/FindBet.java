package antrix.chopbet.Models;

import java.util.Date;

public class FindBet {

    String findID;
    String finderName;
    String betAmount;
    String findStatus;
    long findTime;
    long index;


    public FindBet(){
    }

    public FindBet(String findID, String finderName, String betAmount, String findStatus) {
        this.findID = findID;
        this.finderName = finderName;
        this.betAmount = betAmount;
        this.findStatus = findStatus;

        findTime = new Date().getTime();
        index = -1 * findTime;

    }

    public String getFindID() {
        return findID;
    }

    public void setFindID(String findID) {
        this.findID = findID;
    }

    public String getFinderName() {
        return finderName;
    }

    public void setFinderName(String finderName) {
        this.finderName = finderName;
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
}
