public class Magazine extends LibraryItem implements Borrowable{
    private String issue;

    Magazine(String id , String title , String issue){
        super(id,title);
        this.issue = issue;
    }

    public String getIssue(){
        return issue;
    }

    @Override
    public String getType(){
        return "MAGAZINE";
    }

    @Override
    public int getFine(){
        return 1;
    }

}
