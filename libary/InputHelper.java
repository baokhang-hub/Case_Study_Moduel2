package libary;
import java.util.Scanner;

public class InputHelper {
    private static Scanner input = new Scanner(System.in);
    
    public static int inputInt(){
        do{
            try{
                int data = Integer.parseInt(input.nextLine());
                return data;
            } catch (NumberFormatException e){
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        } while (true);
    }

    public static long inputLong(){
        do{
            try{
                long data = Long.parseLong(input.nextLine());
                return data;
            } catch (NumberFormatException e){
                System.out.println("Invalid input. Please enter a valid long.");
            }
        } while (true);
    }

    public static double inputDouble(){
        do{
            try{
                double data = Double.parseDouble(input.nextLine());
                return data;
            } catch (NumberFormatException e){
                System.out.println("Invalid input. Please enter a valid double.");
            }
        } while (true);
    }

    public static String inputString(){
        return input.nextLine();
    }
}