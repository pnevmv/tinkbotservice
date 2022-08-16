package ru.chnr.vn.tinkbotservice.domain;

import ru.chnr.vn.tinkbotservice.connection.CandleSource;
import ru.chnr.vn.tinkbotservice.connection.CandleStream;
import ru.chnr.vn.tinkbotservice.exceptions.CompanyNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class-wrapper for hashmap of all companies that bot can trade.
 * Hashmap<figi, company>. Figi is unique id of stock
 */
//@Entity
public class Bot {
   // @GeneratedValue
   // @Id
    long id;
    long token;

    private final HashMap<String, Company> companies;

    //while dont know how to work with Entity
    static long curID = 0;
    {
        curID++;
    }

    public Bot(long token){
        this.token = token;
        this.id = curID;
        this.companies = new HashMap<>();
    }

    public long getId() {
        return id;
    }

    public long getToken() {
        return token;
    }

    /**
     * puts company in Map
     * @param figi
     * @param company
     */
    public void putCompanyByFigi(String figi, Company company) {
        this.companies.put(figi, company);
    }

    /**
     * Gets company by figi and call tradeOff method (look in Company class)
     * @param figi
     * @param candleStream
     */
    public void stopTradingByFigi(String figi, CandleStream candleStream) {
        this.companies.get(figi).tradeOff(candleStream);
    }

    /**
     * Get all companies and call tradeOff method (look in Company class)
     * @param candleStream
     */
    public void stopTradingForAll(CandleStream candleStream) {
        for (Company company: companies.values()) {
            company.tradeOff(candleStream);
        }
    }

    /**
     * Get all companies and call startTrade method (look in Company class)
     * @param candleSource
     */
    public void startTradingForAll(CandleSource candleSource) {
        for (Company company: this.companies.values()) {
            company.startTrade(candleSource);
        }
    }

    /**
     *
     * @param figi
     * @return company with inputed figi
     * @throws CompanyNotFoundException - if there is not company with such figi
     */
    public Company getByFigi(String figi) throws CompanyNotFoundException {
        if (!companies.containsKey(figi)) throw new CompanyNotFoundException("There's no company with figi: " + figi);
        return companies.get(figi);
    }

    public HashMap<String, Company> getCompanies() {
        return this.companies;
    }

    /**
     * @return list of companies figis
     */
    public List<String> getFigis() {
        List<String> figis = new ArrayList<>(List.of());
        for (Company company :companies.values()) {
            figis.add(company.getFigi());
        }
        return figis;
    }

    /**
     *
     * @return figis of companies with flag isTrade = true
     * This companies candles are loaded to candleStream and processed in DataStreamProcessor
     */
    public List<String> getFigisOfTradingCompanies() {
        List<String> figis = new ArrayList<>();
        for (Company company : companies.values()) {
            if (company.getIsTrading()) figis.add(company.getFigi());
        }
        try {
            if (figis.isEmpty()) throw new CompanyNotFoundException("There's no companies yet");
        } catch (CompanyNotFoundException exception) {
            System.out.println("info: " + exception.getMessage());
        }
        return figis;
    }

    /**
     *
     * @param figi
     * @return company with current figi
     */
    public Company getCompanyByFigi(String figi) {
        return this.companies.get(figi);
    }

    public int getNumberOfCompanies() {
        return this.companies.size();
    }

    /**
     *
     * @return num of companies where isTrade = true
     */
    public int getNumberOfTradingCompanies() {
        int count = 0;
        for (Company company : companies.values()) {
            if (company.getIsTrading()) count++;
        }
        return count;
    }

    /**
     * checking if there is figi as key in companies Map
     * @param figi
     * @return
     */
    public boolean isContainsFigi(String figi){
        return companies.containsKey(figi);
    }

    @Override
    public String toString() {
        return "Кол-во выбранных компаний: " + getNumberOfCompanies()
                + "Кол-во трейдящих компаний: " + getNumberOfTradingCompanies();
    }

    /**
     * Delete company from map by figi
     * @param figi
     */
    public void removeByFigi(String figi){
        companies.remove(figi);
    }


}
