package antrix.chopbet.Models;

import java.util.Date;

public class NewTransaction {

    String transactionID;
    String transactionType;
    String amount;
    String fee;
    String merchant;
    String merchantTransactionID;
    String phoneNumber;
    String result;
    long date;
    long index;
    String channel;


    public NewTransaction(){
    }

    public NewTransaction(String transactionID, String transactionType, String amount, String fee, String merchant, String merchantTransactionID, String phoneNumber, String result, String channel) {
        this.transactionID = transactionID;
        this.transactionType = transactionType;
        this.amount = amount;
        this.fee = fee;
        this.merchant = merchant;
        this.merchantTransactionID = merchantTransactionID;
        this.phoneNumber = phoneNumber;
        this.result = result;
        this.channel = channel;

        date = new Date().getTime();
        index = -1 * date;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getMerchantTransactionID() {
        return merchantTransactionID;
    }

    public void setMerchantTransactionID(String merchantTransactionID) {
        this.merchantTransactionID = merchantTransactionID;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
