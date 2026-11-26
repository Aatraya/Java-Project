public class Book extends LibraryItem implements Borrowable{
    private String author;

    Book(String id , String title , String a){
        super(id,title);
        this.author = a;
    }

    public String getAuthor(){
        return author;
    }

    @Override
    public String getType(){
        return "BOOK";
    }

    @Override
    public int getFine(){
        return 2;
    }
}
