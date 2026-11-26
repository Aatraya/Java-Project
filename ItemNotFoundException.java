public class ItemNotFoundException extends Exception{
    public ItemNotFoundException(String msg){
        super(msg);
    }
}

class ItemNotAvailableException extends Exception{
    public ItemNotAvailableException(String msg){
        super(msg);
    }
}

class ItemNotOnLoanException extends Exception{
    public ItemNotOnLoanException(String msg){
        super(msg);
    }
}


