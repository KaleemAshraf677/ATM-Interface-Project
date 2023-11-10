package com.atm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class Banking {

	private static Connection conn;
	private static Statement stmt;
	private static ResultSet set;
	static Scanner sc;
	

public static void deposit(int userId) throws SQLException, ClassNotFoundException {
		
		conn=DatabaseConnection.getConnection();
		
		   stmt=conn.createStatement();
		   String query="select * from bankUsers where id="+userId;
		   set=stmt.executeQuery(query);

		if(set.next()) {

			sc=new Scanner(System.in);
			System.out.println("Enter you Security Pin:");
			int Pin=sc.nextInt();

			if(set.getInt("pin")==Pin) {
			       System.out.println("Enter Your Deposit Amount:");
			       float depositAmount=sc.nextFloat();

				if(depositAmount<=0) 
					   System.out.println("Oops... Deposited Amount value should be greater than 0");	   
				else{
					  stmt.executeUpdate("update bankusers set balance=balance+"+depositAmount+" where id="+userId);
					  set=stmt.executeQuery(query);

					  if(set.next()) 
						System.out.println("Rs. "+depositAmount+" deposited And  Your current balance is Rs. "+set.getFloat("balance")+"\n");
			    }
		    }
			else 
				System.out.println("Incorrect Pin");
		}

}
public static void withdraw(int userId) throws SQLException, ClassNotFoundException {
		
		conn=DatabaseConnection.getConnection();
		
		stmt=conn.createStatement();
		String query="select * from bankUsers where id="+userId;
		set=stmt.executeQuery(query);

		if(set.next()) {
			 
			System.out.println("Enter you Pin:");
			sc=new Scanner(System.in);
			int Pin=sc.nextInt();

			  if(set.getInt("pin")==Pin) {

				    System.out.println("Enter your Withdrawal Amount :");
				    float withdrawAmount=sc.nextFloat();

				        if(withdrawAmount<=0) 
					           System.out.println("Oops.... Withdrawal Amount should be greater than 0 ");
				        else if(withdrawAmount>set.getFloat("balance")) 
						        System.out.println("You have Insufficient Funds for this withdrawal Amount");
					    else {
						      stmt.executeUpdate("update bankUsers set balance= balance -"+withdrawAmount+"where id="+userId);
						      set=stmt.executeQuery(query);
					          if(set.next()) {
							       System.out.println("Rs. "+withdrawAmount+" Successfully Withdrawal to your Account\n click button Y to check Available balance otherwise return  to Main Menu. \n");
							       char c=sc.next().toUpperCase().charAt(0);     
							       if(c=='Y') 
								        System.out.println("Available balance is Rs. "+set.getFloat("balance")+"\n");
							  }	
					    }
			}
			else
				System.out.println("Incorrect Pin:");
	   }	
}
public static void transfer(int userId) throws SQLException {
	
	conn=DatabaseConnection.getConnection();
	sc=new Scanner(System.in);
	stmt=conn.createStatement();

	System.out.println("Enter the Reciever Id to Transfer Money");
	int recieverId=sc.nextInt();

	String query="select * from bankUsers where id ="+userId;
	set=stmt.executeQuery(query);

	if(set.next()) {
		System.out.println("Enter you Pin");
		int Pin=sc.nextInt();

		if(set.getInt("pin")==Pin) {	

			  System.out.println("Enter the Transfer Amount:");
			  float transferAmount=sc.nextInt();

			  if(transferAmount<=0) 
				    System.out.println("Oops...... Transfer Amount should be greater than 0");
			  else if(transferAmount>set.getFloat("balance")) 
				    System.out.println("You have Insufficient Funds for Transfer Money");
			  else {
				    stmt.executeUpdate("update bankusers set balance=balance -"+transferAmount+" where id="+userId);
				    stmt.executeUpdate("update bankusers set balance=balance + "+transferAmount+" where id="+recieverId);
				    stmt.executeUpdate("insert into ministatement (user_id,txn_name,reference,amount) values(" +userId+ ",'Transfer','to " +recieverId+ "'," +transferAmount+ ")");
				    stmt.executeUpdate("insert into ministatement (user_id,txn_name,reference,amount) values(" +recieverId+ ",'Transfer','by "+userId+"'," +transferAmount+ ")");
				    set=stmt.executeQuery(query);
				 
					while(set.next()) {
					      System.out.println("Rs. "+transferAmount+" was suscessfully Transfered.\n Click Button Y to check Available balance, otherwise return to Main Menu.........\n ");
				    
			              char c=sc.next().toUpperCase().charAt(0);
						  if(c=='Y') {
					           set=stmt.executeQuery(query);
					           if(set.next())
						        System.out.println("Available balance is Rs. "+set.getFloat("balance")+ "\n");
				          }
			        }
	         }
	    
		}
		else 
			System.out.println("Incorrect Pin");
		
	}
	 else 
		System.out.println("Oops...... Receiver not found");
	
}
public static void miniStatement(int userId) throws SQLException {
	
	conn=DatabaseConnection.getConnection();
	sc=new Scanner(System.in);
	stmt=conn.createStatement();
	set=stmt.executeQuery("select * from bankUsers where id="+userId);
	
	if(set.next()) {	
		System.out.println("Enter your Pin:");
		int Pin=sc.nextInt();
		if(Pin==set.getInt("pin")) {

			set=stmt.executeQuery("select * from ministatement where user_id= "+userId+" order by txn_date desc limit 5");
			
			System.out.println("MiniStatement:\n");
            System.out.println("+----------------+--------------------+----------------+----------------+-------------------------+");
            System.out.println("| user_id        | transaction_name   |    amount      | reference      | transaction_date        |");
            System.out.println("+----------------+--------------------+----------------+----------------+-------------------------+");
           
            while(set.next()) {
				
				int user_id= set.getInt("user_id");
				String transaction_name= set.getString("txn_name");
				float amount= set.getFloat("amount");
				String reference= set.getString("reference");
			    String transaction_date =set.getTimestamp("txn_date").toString();
			    
			    // Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13f | %-20s | %-19s   |\n",user_id, transaction_name, amount, reference, transaction_date);         
            }
            System.out.println("+----------------+--------------------+----------------+----------------+-------------------------+");
            
		}
		else
		    System.out.println("Envalid Pin:");			
	}
}
public static void checkBalance(int userId) throws SQLException {
	
	conn= DatabaseConnection.getConnection();
	sc=new Scanner(System.in);
	stmt=conn.createStatement();;
	set=stmt.executeQuery("select * from bankUsers where id="+userId);
	
	if(set.next()) {			
		System.out.println("Enter your Pin :");
		int Pin=sc.nextInt();
		if(Pin==set.getInt("pin")) {
			try {
				 set=stmt.executeQuery("select balance from bankUsers where id="+userId);
				 while(set.next()) {
					System.out.println("Your Current balance is Rs. "+set.getFloat("balance")+"\n");
				}
			}catch(SQLException e) {
			    e.printStackTrace();
			}
		}
		else 
			System.out.println("Incorrect Pin");

		
	}
}
public static void changePin(int userId) throws SQLException {
	conn=DatabaseConnection.getConnection();
	sc=new Scanner(System.in);
	stmt=conn.createStatement();
	set=stmt.executeQuery("select * from bankUsers where id="+userId);
	
	if(set.next()) {
		System.out.println("Enter your Pin:");
		int Pin=sc.nextInt();

		if(Pin==set.getInt("pin")){
			System.out.println("Enter your New pin :");
			int newPin=sc.nextInt();

			if(newPin==set.getInt("pin")) 
				System.out.println("Oops...New Pin should not be same as old Pin");
			else if(newPin>9999 || newPin<1000)
				System.out.println("Pin "+newPin+" Credentials is not valid");
			else{				
				 int effectedrows=stmt.executeUpdate("update bankUsers set pin="+newPin+" where id="+userId);
				 
				 if(effectedrows>0)
				 System.out.println(" Your Security Pin changed Succesfully\n");
			}
		}
		else 
			System.out.println("Incorrect Pin");	
	}
}
public static void customStatement(int userId) throws SQLException {

	conn=DatabaseConnection.getConnection();
	sc=new Scanner(System.in);
	stmt=conn.createStatement();
	set=stmt.executeQuery("select * from bankUsers where id="+userId);
	
	if(set.next()) {
		System.out.println("Enter your pin");
		int pin=sc.nextInt();
		
		if(pin==set.getInt("pin")){
			System.out.println("Enter Statement Start Date(in YYYY-MM-DD): ");
			String start=sc.next();
			System.out.println("Enter Statement End date(in YYYY-MM-DD ): ");
			String end=sc.next();
			set=stmt.executeQuery("select * from ministatement where user_id= "+userId+" and txn_date between '" +start+ "' and '" +end+ "' ");
			
			System.out.println("Custom Statement:\n");
            System.out.println("+----------------+--------------------+----------------+-------------------------+");
            System.out.println("| user_id        | transaction_name   |    amount      | transaction_date        |");
            System.out.println("+----------------+--------------------+----------------+-------------------------+");
			
			while(set.next()) {
				
				int user_id= set.getInt("user_id");
				String transaction_name= set.getString("txn_name");
				float amount= set.getFloat("amount");
				String transaction_date = set.getTimestamp("txn_date").toString();
				
				  // Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-20s | %-13f | %-21s |\n",user_id, transaction_name, amount, transaction_date);         
            }
			System.out.println("+----------------+--------------------+----------------+-------------------------+");
            
		}
		else 
			System.out.println("Incorrect Pin");				
		
	}
}

   public static void exitAtmMachine() throws InterruptedException {
	
	System.out.print("Exiting ATM Machine");
     int i = 1;
     while(i<=20){
         System.out.print(".");
         Thread.sleep(300);
         i++;
     }
     System.out.println("Thank You: For Using our services.See You Soon..................");
 }

   
}






