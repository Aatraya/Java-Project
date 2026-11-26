public abstract class LibraryItem{
    private String ItemId;
    private String title;

    LibraryItem(String id , String title){
        this.ItemId = id;
        this.title = title;
    }

    public String getId(){
        return this.ItemId;
    }

    public String getTitle(){
        return this.title;
    }

    abstract String getType() ;
}