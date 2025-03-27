import java.util.Scanner;

public class ToDoListMain {

    private static Scanner sc;

    public static void main(String[] args) {

        sc = new Scanner(System.in);

        do {
            System.out.println("To-Do List");
            System.out.println("1. Display List");
            System.out.println("2. Add Item");
            System.out.println("3. Update Item");
            System.out.println("4. Remove Item");
            System.out.println("5. Exit");

            System.out.println("Enter an option:");
            String option = sc.nextLine();

            ToDODAO dao = new ToDODAO();

            try {
                switch (option) {
                    case "1":
                        dao.displayList();
                        break;
                    case "2":
                        System.out.println("Add Item");
                        System.out.println("What is the task?");
                        String task = sc.nextLine();
                        System.out.println("Any additional notes?");
                        String note = sc.nextLine();
                        dao.addItem(task, note);
                        break;
                    case "3":
                        dao.updateItem(sc);
                        break;
                    case "4":
                        System.out.println("Enter item id");
                        int itemId = sc.nextInt();
                        dao.removeItem(itemId);
                        break;
                    case "5":
                        System.out.println("Exiting");
                        System.exit(0);
                    default:
                        System.out.println("I don't understand");
                }
            } catch (Exception ex) {
                System.out.println("Error communicating with database");
                System.out.println(ex.getMessage());
                System.exit(0);
            }

        } while (true);


    }

}
