package antrix.chopbet.Models;

public class BetBuddy {

    private String userName;
    private String favourite;
    private String phoneNumber;
    private String status;

    public BetBuddy(){

    }

    public BetBuddy(String userName){
        this.userName = userName;
    }

    public BetBuddy(String userName,String phoneNumber){
        this.userName = userName;
        this.phoneNumber = phoneNumber;
    }

    public BetBuddy(String userName,String phoneNumber, String status){
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public BetBuddy(String userName, String phoneNumber, String status, String favourite){
        this.userName = userName;
        this.favourite = favourite;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
