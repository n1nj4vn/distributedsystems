package edu.rit.cs;

import java.rmi.*;
import java.util.List;

interface Employee extends Remote {

    int getEmployeeID() throws RemoteException;

    EmployeeList.Role getEmployeeRole() throws RemoteException;

    Person getPersonObject() throws RemoteException;

}

public interface EmployeeList extends Remote {
    enum Role {
        Researcher, Engineer, Sale, Undefined
    }

    Employee newPerson(Person p, Role r) throws RemoteException;

    List<Employee> getEmployeeList() throws RemoteException;

    int getEmployeeCount() throws RemoteException;

}
