package ru.chnr.vn.tinkbotservice.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

//@Entity
public class Bot {
   // @GeneratedValue
   // @Id
    long id;
    long token;

    //while dont know how to work with Entity
    static long curID = 0;
    {
        curID++;
    }

    public Bot(){}

    public Bot(long token){
        this.token = token;
        this.id = curID;
    }

    public long getId() {
        return id;
    }
    public long getToken() {
        return token;
    }


}
