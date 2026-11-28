import java.util.*;

public class Library{
    private int time = 0;
    private List<LibraryItem> item = new ArrayList<>();
    private List<Member> members = new ArrayList<>();
    private List<Loan> loans = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();
    private static final String BAD_COMMAND = "Please check the command";

    private static String fieldCountError(String cmd, int expected, int got) {
        return "ERROR " + cmd + " expects " + (expected - 1) + " field(s) but got " + (got - 1);
    }


    public int getMemberSize(){
        return members.size();
    }

    public int getItemSize(){
        return item.size();
    }

    public int getTime(){
        return time;
    }

    public void setTime(int t){
        time = t;
    }

    public Member getMember(int idx){
        if(idx > this.getMemberSize() - 1 || idx<0) return null;

        return members.get(idx);

    }

    

            public void addBook(String id, String title, String author) {
            item.add(new Book(id, title, author));
            System.out.println("OK");
        }

        public void addMagazine(String id, String title, String issue) {
            item.add(new Magazine(id, title, issue));
            System.out.println("OK");
        }

        public void addReference(String id, String title, String author) {
            item.add(new ReferenceBook(id, title, author));
            System.out.println("OK");
        }

        public void addMember(String id, String name, String type) {
            if (type.equals("STUDENT"))      members.add(new Student(id, name));
            else if (type.equals("STAFF"))   members.add(new Staff(id, name));
            else if (type.equals("FACULTY")) members.add(new Faculty(id, name));
            else {
                System.out.println("ERROR invalid member type " + type
                        + " (expected STUDENT, STAFF or FACULTY)");
                return;
            }
            System.out.println("OK");
        }

    private Loan findActiveLoan(String itemId) {
        for (Loan l : loans) {
            if (l.getItemId().equals(itemId)) {
                return l;
            }
        }
        return null;
    }

    public void inventory() {
        if (item.isEmpty()) {
            System.out.println("NONE");
            return;
        }

        List<LibraryItem> sortedItems = new ArrayList<>(item);
        sortedItems.sort(Comparator.comparing(LibraryItem::getId));

        for (LibraryItem it : sortedItems) {
            String status;
            Loan activeLoan = findActiveLoan(it.getId());

            if (activeLoan != null) {
                status = "ON_LOAN|" + activeLoan.getMemId();
            } 
            
            else if (!(it instanceof Borrowable)) {
                    status = "NOT_BORROWABLE";
            } 
            
            else {
                    status = "AVAILABLE";
                }

                System.out.println(it.getId() + "|" + it.getType() + "|" + status);
        }
    }

    private Member findMember(String memberId) {
        for (Member m : members) {
            if (m.getMemId().equals(memberId)) {
                return m;
            }
        }
        return null;
    }

    private LibraryItem findItem(String itemId) {
        for (LibraryItem it : item) {
            if (it.getId().equals(itemId)) {
                return it;
            }
        }
        return null;
    }

    private int countMemberLoans(String memberId) {
        int count = 0;
        for (Loan l : loans) {
            if (l.getMemId().equals(memberId)) {
                count++;
            }
        }
        return count;
    }

    public void borrow(String memberId, String itemId) 
        throws MemberNotFoundException, ItemNotFoundException, 
               ItemNotAvailableException, BorrowLimitExceededException {
    
    // 1. Check if member exists
        Member m = findMember(memberId);
        if (m == null) {
            throw new MemberNotFoundException("member " + memberId + " not found");
        }

        // 2. Check if item exists
        LibraryItem it = findItem(itemId);
        if (it == null) {
            throw new ItemNotFoundException("item " + itemId + " not found");
        }

        // 3. Check if item is borrowable
        if (!(it instanceof Borrowable)) {
            throw new ItemNotAvailableException(itemId + " cannot be borrowed");
        }

        // 4. Check if item is already on loan
        if (findActiveLoan(itemId) != null) {
            throw new ItemNotAvailableException(itemId + " is on loan");
        }

        // 5. Check if member has reached their borrow limit
        if (countMemberLoans(memberId) >= m.getMaxItems()) {
            throw new BorrowLimitExceededException(memberId + " has reached the limit of " + m.getMaxItems());
        }

        // 6. Issue the loan
        int loanPeriod = m.getLoanPeriod();
        int dueDay = this.time + loanPeriod;
        loans.add(new Loan(itemId, this.time, loanPeriod, memberId));
        
        System.out.println("OK due on day " + dueDay);
    }


    public void returnItem(String itemId) 
        throws ItemNotFoundException, ItemNotOnLoanException {
    
        // 1. Check if item exists
        LibraryItem it = findItem(itemId);
        if (it == null) {
            throw new ItemNotFoundException("item " + itemId + " not found");
        }

        // 2. Check if item is currently on loan
        Loan activeLoan = findActiveLoan(itemId);
        if (activeLoan == null) {
            throw new ItemNotOnLoanException(itemId + " is not on loan");
        }

        // 3. Calculate fine and close the loan
        int fine = activeLoan.calculateFine((Borrowable) it, this.time);
        loans.remove(activeLoan);

        // 4. Check reservation queue 
        Iterator<Reservation> iter = reservations.iterator();
        while (iter.hasNext()) {
            Reservation res = iter.next();
            if (res.getItem().equals(itemId)) {
                Member nextMember = findMember(res.getMember());
                if (nextMember != null && countMemberLoans(nextMember.getMemId()) < nextMember.getMaxItems()) {
                    iter.remove();
                    int loanPeriod = nextMember.getLoanPeriod();
                    int dueDay = this.time + loanPeriod;
                    loans.add(new Loan(itemId, this.time, loanPeriod, nextMember.getMemId()));
                    
                    System.out.println("OK fine " + fine + ", issued to " + nextMember.getMemId() + " due on day " + dueDay);
                    return;
                }
            }
        }

        System.out.println("OK fine " + fine);
    }


    public void addReservation(String memberId, String itemId) 
        throws MemberNotFoundException, ItemNotFoundException, 
               ItemNotAvailableException, ReservationNotAllowedException {
    
        // 1. Check if member exists
        Member m = findMember(memberId);
        if (m == null) {
            throw new MemberNotFoundException("member " + memberId + " not found");
        }

        // 2. Check if item exists
        LibraryItem it = findItem(itemId);
        if (it == null) {
            throw new ItemNotFoundException("item " + itemId + " not found");
        }

        // 3. Check if item is borrowable
        if (!(it instanceof Borrowable)) {
            throw new ItemNotAvailableException(itemId + " cannot be borrowed");
        }

        // 4. Check if item is currently on loan (cannot reserve available items)
        Loan activeLoan = findActiveLoan(itemId);
        if (activeLoan == null) {
            throw new ReservationNotAllowedException(itemId + " is available");
        }

        // 5. Check if member already holds the item
        if (activeLoan.getMemId().equals(memberId)) {
            throw new ReservationNotAllowedException(memberId + " already has " + itemId);
        }

        // 6. Check if member has already reserved the item
        for (Reservation res : reservations) {
            if (res.getMember().equals(memberId) && res.getItem().equals(itemId)) {
                throw new ReservationNotAllowedException(memberId + " already reserved " + itemId);
            }
        }

        // 7. Add to reservation queue
        reservations.add(new Reservation(memberId, itemId));
        System.out.println("OK reserved");
    }


    public void search(String text) {
        String query = text.toLowerCase();
        List<LibraryItem> matches = new ArrayList<>();

        for (LibraryItem it : item) {
            if (it.getTitle().toLowerCase().contains(query)) {
                matches.add(it);
            }
        }

        if (matches.isEmpty()) {
            System.out.println("NONE");
            return;
        }

        matches.sort(Comparator.comparing(LibraryItem::getTitle));

        for (LibraryItem it : matches) {
            System.out.println(it.getId() + "|" + it.getType() + "|" + it.getTitle());
        }
    }

    public void memberLoans(String memberId) throws MemberNotFoundException {
        if (findMember(memberId) == null) {
            throw new MemberNotFoundException("member " + memberId + " not found");
        }

        List<Loan> active = new ArrayList<>();
        for (Loan l : loans) {
            if (l.getMemId().equals(memberId)) {
                active.add(l);
            }
        }

        if (active.isEmpty()) {
            System.out.println("NONE");
            return;
        }

        active.sort(
            Comparator.comparingInt((Loan l) -> l.startDay() + l.getmaxDays())
                    .thenComparing(Loan::getItemId)
        );

        for (Loan l : active) {
            LibraryItem it = findItem(l.getItemId());
            int dueDay = l.startDay() + l.getmaxDays();
            System.out.println(l.getItemId() + "|" + it.getTitle() + "|" + dueDay);
        }
    }

    public void overdue() {
        List<Loan> overdueList = new ArrayList<>();
        for (Loan l : loans) {
            int dueDay = l.startDay() + l.getmaxDays();
            if (this.time > dueDay) {
                overdueList.add(l);
            }
        }

        if (overdueList.isEmpty()) {
            System.out.println("NONE");
            return;
        }

        overdueList.sort(
            Comparator.comparingInt((Loan l) -> this.time - (l.startDay() + l.getmaxDays()))
                    .reversed()
                    .thenComparing(Loan::getItemId)
        );

        for (Loan l : overdueList) {
            int dueDay = l.startDay() + l.getmaxDays();
            int daysLate = this.time - dueDay;
            LibraryItem it = findItem(l.getItemId());
            int fine = l.calculateFine((Borrowable) it, this.time);
            System.out.println(l.getItemId() + "|" + l.getMemId() + "|" + daysLate + "|" + fine);
        }
    }

    

        private static int expectedFields(String cmd) {
            switch (cmd) {
                case "ADD_BOOK":
                case "ADD_MAGAZINE":
                case "ADD_REFERENCE":
                case "ADD_MEMBER":
                    return 4;
                case "BORROW":
                case "RESERVE":
                    return 3;
                case "RETURN":
                case "ADVANCE":
                case "SEARCH":
                case "LOANS":
                    return 2;
                case "OVERDUE":
                case "INVENTORY":
                    return 1;
                default:
                    return -1;   
            }
        }





    public static void main(String[] args){
        Library lib = new Library();
        Scanner sc = new Scanner(System.in);

        while(sc.hasNextLine()){
            String command = sc.nextLine().replace("\uFEFF", "");

            if (command.trim().isEmpty()) continue;

            String[] part = command.split("\\|", -1);
            for (int i = 0; i < part.length; i++) {
                part[i] = part[i].trim();
            }

            int expected = expectedFields(part[0]);
            if (expected == -1) {
                System.out.println("ERROR unknown command " + part[0]);
                continue;
            }
            if (part.length != expected) {
                System.out.println(fieldCountError(part[0], expected, part.length));
                continue;
            }

            // every field must be non-empty, except SEARCH text (empty matches all items)
            if (!part[0].equals("SEARCH")) {
                boolean emptyField = false;
                for (int i = 1; i < part.length; i++) {
                    if (part[i].isEmpty()) { emptyField = true; break; }
                }
                if (emptyField) {
                    System.out.println("ERROR " + part[0] + " has an empty field");
                    continue;
                }
            }

          

            
            try{
                switch(part[0]){
                    case "ADD_BOOK" : {
                        lib.addBook(part[1], part[2], part[3]); 
                        break;
                    }

                    case "ADD_MAGAZINE":{
                        lib.addMagazine(part[1],part[2],part[3]);
                        break;
                    }

                    case "ADD_REFERENCE":{
                        lib.addReference(part[1],part[2],part[3]);
                        break;
                    }

                    case "ADD_MEMBER" : {
                        lib.addMember(part[1] , part[2] , part[3]);
                        break;
                    }

                    case "INVENTORY" : {
                        lib.inventory();
                        break;
                    }

                    case "BORROW": {
                        lib.borrow(part[1], part[2]);
                        break;
                    }

                    case "ADVANCE": {
                        int days;
                        try {
                            days = Integer.parseInt(part[1]);
                        } catch (NumberFormatException e) {
                            System.out.println("ERROR ADVANCE needs a whole number, got " + part[1]);
                            break;
                        }
                        if (days < 0) {
                            System.out.println("ERROR ADVANCE cannot move time backwards");
                            break;
                        }
                        if ((long) lib.getTime() + days > Integer.MAX_VALUE) {
                            System.out.println("ERROR ADVANCE value is too large");
                            break;
                        }
                        lib.setTime(lib.getTime() + days);
                        System.out.println("OK today is day " + lib.getTime());
                        break;
                    }

                    case "RETURN": {
                        lib.returnItem(part[1]);
                        break;
                    }

                    case "RESERVE": {
                        lib.addReservation(part[1], part[2]);
                        break;
                    }

                    case "SEARCH": {
                        lib.search(part[1]);
                        break;
                    }

                    case "LOANS": {
                    lib.memberLoans(part[1]);
                    break;
                }

                    case "OVERDUE": {
                        lib.overdue();
                        break;
                    }

                    default : {
                        System.out.println("ERROR unknown command " + part[0]);
                    }
                
            }
        }

            catch(Exception e){
                System.out.println("ERROR " + e.getMessage());
            }

    }

    sc.close();


    }
}