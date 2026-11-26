public class ReferenceBook extends LibraryItem{
    private String author;

    ReferenceBook(String id, String title , String auth){
        super(id,title);
        author = auth;
    }

    public String getAuthor(){
        return author;
    }

    @Override
    public String getType(){
        return "REFERENCE";
    }
}
