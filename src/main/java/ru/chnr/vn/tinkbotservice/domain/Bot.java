package ru.chnr.vn.tinkbotservice.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Bot {
    @GeneratedValue
    @Id
    long id;


}
