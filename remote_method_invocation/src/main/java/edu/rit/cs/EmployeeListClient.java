package edu.rit.cs;

import java.rmi.*;
import java.util.List;

public class EmployeeListClient {

    public static void main(String args[]){
        System.setSecurityManager(new SecurityManager());

        EmployeeList aEmployeeList = null;
        try{
            aEmployeeList  = (EmployeeList) Naming.lookup("rmi://rmiregistry:4023/EmployeeList");

            Person p = new Person("bob", 23);
            aEmployeeList.newPerson(p, EmployeeList.Role.Engineer);

            System.out.println("Total number of employee: " + aEmployeeList.getEmployeeCount());
        } catch(RemoteException e) {
            System.out.println(e.getMessage());
        }catch(Exception e) {
            System.out.println("Client: " + e.getMessage());
        }
    }

}

