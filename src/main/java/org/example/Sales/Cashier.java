package org.example.Sales;

import java.util.concurrent.atomic.AtomicInteger;

public class Cashier {
    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private String name;
    private int id;

    public Cashier(String name){
        if (name.isBlank() || name == null){
            throw new NullPointerException("There must be a name and it can't be null");
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
