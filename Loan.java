public class Loan {
    private String itemId;
    private int dayofLoan; // start day of laon 
    private int maxDays;
    private String memId;

    Loan(String iid , int startday , int md , String mid){
        itemId = iid;
        dayofLoan = startday;
        maxDays = md;
        memId = mid;
    }

    public String getItemId(){
        return itemId;
    }

    public int startDay(){
        return dayofLoan;
    }

    public String getMemId(){
        return memId;
    }

    public int getmaxDays(){
        return maxDays;
    }
    
    public int calculateFine(Borrowable item , int day){
        int dueDay = this.dayofLoan + this.maxDays;
        
        if (day <= dueDay) {
            return 0;
        }
        
        int overdueDays = day - dueDay;
        return overdueDays * item.getFine();
    }
}
