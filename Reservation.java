public class Reservation {
    private String memId;
    private String itemId;

    Reservation(String memId , String itemId){
        this.memId = memId;
        this.itemId = itemId;
    }

    public String getDetails(){
        return "Member " + memId + " has reserved the item " + itemId;
    }

    public String getMember(){
        return memId;
    }

    public String getItem(){
        return itemId;
    }
}
