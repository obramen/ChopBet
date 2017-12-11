package antrix.chopbet.Models;


import java.util.Date;

public class NewCard {

    String phoneNumber;
    String channel;
    String cardID;
    long saveDate;
    long index;

    public NewCard(){

    }

    public NewCard(String phoneNumber, String channel, String cardID) {
        this.phoneNumber = phoneNumber;
        this.channel = channel;
        this.cardID = cardID;

        saveDate = new Date().getTime();
        index = -1 * saveDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public long getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(long saveDate) {
        this.saveDate = saveDate;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getCardID() {
        return cardID;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }
}
