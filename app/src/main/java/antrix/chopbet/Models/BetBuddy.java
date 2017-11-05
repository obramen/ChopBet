package antrix.chopbet.Models;

public class BetBuddy {

    private String userName;
    private String favourite;

    public BetBuddy(){

    }

    public BetBuddy(String userName){
        this.userName = userName;
    }


    public BetBuddy(String userName, String favourite){
        this.userName = userName;
        this.favourite = favourite;
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
}
