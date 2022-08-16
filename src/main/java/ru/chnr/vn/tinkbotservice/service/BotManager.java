package ru.chnr.vn.tinkbotservice.service;

import org.springframework.stereotype.Component;
import ru.chnr.vn.tinkbotservice.domain.Bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class BotManager {
    HashMap<Long,Bot> bots;

    public BotManager(){
        bots = new HashMap<>();
    }

    public long createBot(long token){
        Bot newBot = new Bot(token);
        bots.put(newBot.getId(), newBot);
        return newBot.getId();
    }

    public boolean deleteBot(long id){
        if (!bots.containsKey(id)) return false;
        bots.remove(id);
        return true;
    }



}
