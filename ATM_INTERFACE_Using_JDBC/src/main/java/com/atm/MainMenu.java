
package com.atm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MainMenu {

public static void main(String[] args) throws ClassNotFoundException, SQLException{
		
     try {
    	  System.out.println("Welcome to the ATM Machine.............");
          Connection conn = DatabaseConnection.getConnection();
          Scanner input = new Scanner(System.in);
		  System.out.println("Enter userId");
          int userId = input.nextInt();
		  Statement stmt = conn.createStatement();
	      ResultSet set = stmt.executeQuery("select * from bankusers where id = "+userId);

		  if (set.next()) {       //if user founds in the bank database, interface shows welcome user 
				 System.out.println("You are now Successfully login to your Account: " +set.getString("firstname").toUpperCase()+" " +set.getString("lastname").toUpperCase()+ "\n");
                 boolean flag=true;
                 
				 while(flag){           // this loop runs until you call exit method.
					  		  
					   System.out.println("Press 1 for Deposit Amount" );
					   System.out.println("Press 2 for Withdraw Cash");
			           System.out.println("Press 3 for Transfer Money");
			           System.out.println("Press 4 for Check Account Balance");
		     	       System.out.println("Press 5 Mini Statment");
					   System.out.println("Press 6 for Custom Statement");
					   System.out.println("Press 7 for Change Security Pin");
					   System.out.println("Press 8 Exit ATM Machine\n");
					   System.out.println("click the Button");
				       int choice=input.nextInt();

					   switch(choice) {        //calls methods as per your choice
						                    
					           case 1:Banking.deposit(userId);
					                  break;
					           case 2: Banking.withdraw(userId);
					                  break;
					           case 3:Banking.transfer(userId);                        
				                	  break;
					           case 4:Banking.checkBalance(userId);                        
					                  break;
					           case 5:Banking.miniStatement(userId);
				                	  break;
					           case 6:Banking.customStatement(userId);
					                  break;
					           case 7:Banking.changePin(userId);                        
					                  break;
					           case 8:                            // Exit the program when the user choice: 8
						              Banking.exitAtmMachine();
					            	  flag=false;
					            	  break;

					           default:
						               System.out.println("Invalid choice: Please Choose Right Button\n");
					    }
				 }

	    }
		else 
		    System.out.println("Invalid userId");
			
	}catch (Exception e) {
			  e.printStackTrace();
	    }

		
	}
}



