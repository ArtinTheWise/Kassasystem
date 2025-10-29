package org.example.Sales;

import java.util.concurrent.atomic.AtomicInteger;

public class Cashier {
    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private String name;
    private int id;

    public Cashier(String name){
        if (name == null){
            throw new NullPointerException("name can't be null");
        }
        if (name.isEmpty()){
            throw new NullPointerException("Name can't be empty");
        }
        this.name = name;
        this.id = SEQ.getAndIncrement();

    }

    public String getName(){
        return name;
    }

    public int getId(){
        return id;
    }




    
}
