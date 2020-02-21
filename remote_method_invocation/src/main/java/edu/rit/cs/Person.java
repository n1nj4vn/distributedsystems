package edu.rit.cs;

public class Person {
    private String name;
    private String address;
    private int age;

    public Person(String name, int age){
        this.name = name;
        this.age = age;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public void show() {
        System.out.println(this.name + " is " + this.age + " years old!");
    }

}
