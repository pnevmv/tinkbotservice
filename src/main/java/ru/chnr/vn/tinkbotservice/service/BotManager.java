package ru.chnr.vn.tinkbotservice.service;

import org.springframework.stereotype.Component;
import ru.chnr.vn.common.generated.CompanyInfo;
import ru.chnr.vn.tinkbotservice.domain.Bot;
import ru.chnr.vn.tinkbotservice.domain.Company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class BotManager {
    HashMap<Long,Bot> bots;

    public BotManager(){
        bots = new HashMap<>();
    }

    public HashMap<Long, Bot> getBots() {
        return this.bots;
    }

    public long createBot(String token){
        Bot newBot = new Bot(token);

        bots.put(newBot.getId(), newBot);

        return newBot.getId();
    }

    public boolean deleteBot(long id){
        if (!bots.containsKey(id)) return false;

        bots.remove(id);

        return true;
    }

    public boolean addCompany(long id, CompanyInfo companyInfo) {
        if (!bots.containsKey(id)) return false;

        Company company = new Company(
                companyInfo.getFigi(),
                companyInfo.getMoneyToTrade(),
                companyInfo.getLossPercent(),
                companyInfo.getTakeProfit(),
                companyInfo.getLot()
        );
        bots.get(id).getCompanies().put(company.getFigi(), company);

        return true;
    }

    public boolean changeCompany(long id, CompanyInfo companyInfo) {
        if (!bots.containsKey(id)) return false;

        Company company = new Company(
                companyInfo.getFigi(),
                companyInfo.getMoneyToTrade(),
                companyInfo.getLossPercent(),
                companyInfo.getTakeProfit(),
                companyInfo.getLot()
        );

        return bots.get(id).getCompanies().replace(companyInfo.getFigi(), bots.get(id).getCompanies().get(companyInfo.getFigi()), company);
    }

    public boolean deleteCompany(long id, CompanyInfo companyInfo) {
        if (!bots.containsKey(id)) return false;

        bots.get(id).getCompanies().remove(companyInfo.getFigi());

        return true;
    }



}
