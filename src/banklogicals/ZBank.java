package banklogicals;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import persistance.Connector;
import persistance.DbConnector;
import utilities.Account;
import utilities.BankingException;
import utilities.Branch;
import utilities.Customer;
import utilities.Employee;
import utilities.InvalidUserException;
import utilities.Transaction;

import utilities.TransactionType;
import utilities.WrongPasswordException;
import utilities.TransactionReq;

public class ZBank {
	private Connector dbConnector = new DbConnector();
	
	public String getUser(int userId) throws BankingException,InvalidUserException {
		return dbConnector.getRole(userId);
	}
	
	
	public void checkPassword(int userId, String password) throws BankingException, WrongPasswordException{

			String originalPassword = dbConnector.getPassword(userId);
			String enteredPassword = getHash(password);
			
			boolean isCorrect = originalPassword.equals(enteredPassword);
			if(!isCorrect) {
				throw new WrongPasswordException("Incorrect password!! try again ");
			}
	}

	public void addEmployees(Employee emploee,String password) throws BankingException{
		password = getHash(password);
	
		dbConnector.addEmployee(emploee,password);	
	}
	
	public void addBranch(Branch branch) throws BankingException {

        dbConnector.addBranch(branch);	
	}
	
	public void addCustomer(Customer customer,String password) throws BankingException {
		password = getHash(password);
		dbConnector.addCustomer(customer, password);
	}
	
	public void addAccount(Account account) throws BankingException{

		dbConnector.addAccount(account);
	}
	
	public void changePassword(String oldPassword,String newPassword) {
		
	}
	
	public void transferMoney(Transaction transaction,TransactionType type) throws BankingException {
		
		long accountNumber = transaction.getAccountNo();
		boolean state = dbConnector.isActive(accountNumber);
		
		if(!state) {
			
			throw new BankingException("Your account is inactive ");
		}
		
		
		long balance = dbConnector.getBalance(accountNumber);
		int amount = transaction.getAmount();
		
		long closingBalance = balance - amount;
		String transactionType = "DEBIT";
		 transaction.setStatus("SUCCESS");
		transaction.setOpenBalance(balance);
		transaction.setDateTime(System.currentTimeMillis());
	
		if(type != TransactionType.DEPOSIT) {
			if(balance < amount) {	
				
				transaction.setType(transactionType);
		        transaction.setCloseBalance(balance);
		        transaction.setStatus("FAILED");
		        dbConnector.updateTransaction(transaction);
		        
    			throw new BankingException("Insufficient balance");
   			}
		}
        switch(type) {

        case DEPOSIT:
        	transactionType = "CREDIT";
        	closingBalance = balance + amount;
   		  	break;   
   		  	
        case WITHIN_BANK:
        	
        	transaction.setType(transactionType);
            transaction.setCloseBalance(closingBalance);
        	dbConnector.updateTransaction(transaction);
        	
        	long receiverAccount = transaction.getTransactionAccNo();
        	
        	transaction.setUserId(dbConnector.getUserId(receiverAccount));
        	
        	long receiverBalance = dbConnector.getBalance(receiverAccount);
        	transaction.setOpenBalance(receiverBalance);
        	
        	transaction.setTransactionAccNo(accountNumber);
        	transaction.setAccountNo(receiverAccount);
        	
        	transactionType = "CREDIT";
        	closingBalance = receiverBalance + amount;
        	
        	break;
		default:
			break;
        	
   		}
        transaction.setType(transactionType);
        transaction.setCloseBalance(closingBalance);
        dbConnector.updateTransaction(transaction);
   }
		

	
	public void accountDeactivate(long accountNumber) throws BankingException {
		dbConnector.deactivateAccount(accountNumber);
	}
	
	public void userDeactivate(int userId) throws BankingException {
		dbConnector.getAccountDetails(userId);
	}
	
	public Map<Integer, Branch> getAllBranch() throws BankingException {
	
		return dbConnector.getAllBranches();
	}
	
	public  Customer getCustomerDetails(int userId) throws BankingException {

		return dbConnector.getCustomerDetails(userId);
	}
	
	public  List<Account> getAccountDetails(int userId) throws BankingException {

		return dbConnector.getAccountDetails(userId);
		
	}
	public  Map<Long, List<Transaction>> getTransactionDetails(TransactionReq requirement) throws BankingException {
		return dbConnector.getTransactionDetail(requirement);
	}
   public Map<Long, List<Transaction>> getAccountTransaction(TransactionReq requirement) throws BankingException{
	   

	   return dbConnector.getTransactionDetail(requirement);
   }
  
   public long getAccountBalance(long accountNumber) throws BankingException {
	   return dbConnector.getBalance(accountNumber);
   }
   public long getOverAllBalance(int userId) throws BankingException {
	   return dbConnector.getOverAllbalance(userId);
   }

	public String getHash(String password) throws  BankingException {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
			 StringBuilder hexString = new StringBuilder(2 * hash.length);
			    for (int i = 0; i < hash.length; i++) {
			        String hex = Integer.toHexString(0xff & hash[i]);
			        if(hex.length() == 1) {
			            hexString.append('0');
			        }
			        hexString.append(hex);
			    }
			    return hexString.toString();
		}catch(NoSuchAlgorithmException e) {
			throw new BankingException(e.getMessage(),e);
		}
	}
		
}
