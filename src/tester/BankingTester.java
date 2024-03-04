package tester;


import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import banklogicals.ZBank;
import utilities.BankingException;

import utilities.InvalidUserException;

import utilities.WrongPasswordException;


public class BankingTester {
	Logger logger = Logger.getLogger(BankingTester.class.getName());
	
	public static void main(String... args) {

		Logger logger = Logger.getLogger(BankingTester.class.getName());
		try(Scanner bankScanner = new Scanner(System.in)){
			
		
			boolean loop = true;
			while(loop) {

				try{
	
					ZBank zBank = new ZBank();

					System.out.println( "Welcome to Z Bank");

					System.out.println( "Enter the user id: ");
					int userId = bankScanner.nextInt();
					bankScanner.nextLine();

					String user = zBank.getUser(userId);
					boolean passwordLoop = true;
					do {
						try {
							System.out.println( "Enter the password ");
							String password = bankScanner.nextLine();

							zBank.checkPassword(userId, password);
							passwordLoop = false;
						} catch (WrongPasswordException e) {
							e.getMessage();
						}

					} while (passwordLoop);

					System.out.println( "welcome to ZBank.\nYou successfully logged in ");
					BankTester bankTester = new BankTester();
					bankTester.setUserId(userId);
					switch (user) {

					case "ADMIN":
						 bankTester.adminAccess();
						break;
						
					case "EMPLOYEE":
						bankTester.employeeAccess();
						break;
					
					case "CUSTOMER":
						bankTester.customerAccess();
						break;
					}
					
				
				  } catch (BankingException e) {
					logger.log(Level.WARNING,e.getMessage());
				  } catch (InvalidUserException e) {
				
					e.printStackTrace();
				}
			}
			
		}
	}
}


