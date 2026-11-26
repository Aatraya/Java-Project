public abstract class Member {
    private String memberId;
    private int maxItems;
    private int loanPeriod;
    private String memberName;

    Member(String id , int mI , int lP , String mN){
        memberId = id;
        maxItems = mI;
        loanPeriod = lP;
        memberName = mN;
    }

    public String getMemId(){
        return memberId;
    }

    public String getName(){
        return memberName;
    }

    public int getMaxItems(){
        return maxItems;
    }

    public int getLoanPeriod(){
        return loanPeriod;
    }
    
}
