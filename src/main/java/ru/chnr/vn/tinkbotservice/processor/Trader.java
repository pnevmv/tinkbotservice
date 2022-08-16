package ru.chnr.vn.tinkbotservice.processor;

import ru.chnr.vn.tinkbotservice.connection.Connector;
import ru.chnr.vn.tinkbotservice.connection.TradeStream;
import ru.chnr.vn.tinkbotservice.domain.Bot;
import ru.chnr.vn.tinkbotservice.domain.Company;
import ru.chnr.vn.tinkbotservice.domain.Deal;
import ru.chnr.vn.tinkbotservice.exceptions.CompanyNotFoundException;
import ru.chnr.vn.tinkbotservice.exceptions.NotEnoughMoneyToTradeException;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.core.InvestApi;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Class that calculate from probability, how many stock it can buy/sell and call TradeStream methods for buying/selling
 * if user hasn't got enough money/stocks for trading, trader will give a signal
 */

public class Trader {
    private final TradeStream tradeStream;
    private final Bot bot;
    private final InvestApi api;


    public Trader(Connector connector, InvestApi api){
        this.tradeStream = connector.getTradeStream();
        this.api = api;
        this.bot = connector.getCompanies();
    }

    /**
     * trade company stock base on probability
     * @param company - company wich instrument you trade
     * @param candle - candle with last information about instrument
     * @param probability - Solution to buy/sell represented as double in range [-1; 1] where '-' is to sell, '+' is to buy and value is probability to do this
     * @throws NotEnoughMoneyToTradeException - if person hasnt got enough money to buy even 1 lot
     * @throws CompanyNotFoundException - problems with programm storage
     */
    public void trade(Company company, Candle candle, double probability) throws NotEnoughMoneyToTradeException, CompanyNotFoundException {
        if(probability > 0) buyLots(Math.abs(probability), candle, company);
        if(probability < 0) sellLots(Math.abs(probability), candle, company);

    }


    /**
     * Sells deals whole lots when close price of current candle less then stopprice of deal
     * @param company
     * @param candle
     * @throws CompanyNotFoundException
     */
    public void sellIfStopPrice(Company company, Candle candle) throws CompanyNotFoundException {
        BigDecimal close = MoneyQuotationProcessor.convertFromQuation(candle.getClose());
        for (Deal d  : company.getOpenDeals().getDealsAsList()) {
            //if current price < stop price, sell lots
            if(d.getStopPrice().compareTo(close) >= 0){
                tradeStream.sellStock((d.getLotNumber()), candle.getClose(), company.getFigi(), d);
            }
        }
    }

    /**
     * Calculate how many lots to buy. Num of lots base on probability and free money, you have to trade on current instrument
     * @param probability - [0, 1]
     * @param candle  - candle with last information about instrument
     * @param company - company wich instrument you trade
     * @throws NotEnoughMoneyToTradeException  - if person hasnt got enough money to buy even 1 lot
     * @throws CompanyNotFoundException - problems with programm storage
     */
    public void buyLots(double probability, Candle candle, Company company) throws NotEnoughMoneyToTradeException, CompanyNotFoundException {
        long lots;

        BigDecimal closePrice = MoneyQuotationProcessor.convertFromQuation(candle.getClose());
        // lot price = close price * lotsOfInstrument
        BigDecimal lotPrice = closePrice.multiply(BigDecimal.valueOf(company.getLot()));

        BigDecimal freeMoney =  BigDecimal.valueOf(company.getFreeMoney());
        BigDecimal probab = BigDecimal.valueOf(probability);

        //if you have money only for 1 lot, and pobability > 60% you buy it
        if(freeMoney.divide(lotPrice, 9, RoundingMode.HALF_DOWN).intValue() == 1 && probability > 0.6) lots =  1;

        /* Available money / lot price = available lot count. Then multiply probability.
         For example: probability = 50%, bot will buy lots for 50% of all free money
        */
        else lots = freeMoney.divide(lotPrice, 9, RoundingMode.HALF_DOWN).multiply(probab).intValue();

       /*
       if you cant buy any of lots, and you hadnt buy any - bot will notify user
        */

        if(company.getFreeMoney() < (MoneyQuotationProcessor.convertFromQuation(candle.getClose()).doubleValue() * company.getLot())
                && company.getOpenDeals().getDealsAsList().isEmpty()) throw new NotEnoughMoneyToTradeException();

        // bot buy lots
        if(lots > 0) tradeStream.buyStock(
                lots,
                candle.getClose(),
                company.getFigi()
        );


    }

    /**
     * Calculate how many lots to sell. You sell stocks, if close price reached take-profit of deal. You sell not whole lots of deal,
     * but part proportional probability
     * @param probability - [0, 1]
     * @param candle  - candle with last information about instrument
     * @param company - company wich instrument you trade
     * @throws NotEnoughMoneyToTradeException  - if person hasn't got enough money to buy even 1 lot
     * @throws CompanyNotFoundException - problems with program storage
     */
    public void sellLots(double probability, Candle candle, Company company) throws CompanyNotFoundException {
        BigDecimal close = MoneyQuotationProcessor.convertFromQuation(candle.getClose());
        // coefficient to calculate price of takeprofit
        BigDecimal  companyTakeprofit = BigDecimal.valueOf(1 + (company.getTakeProfit() / 100));


        for(Deal d : company.getOpenDeals().getDealsAsList()){
            //if instruments close price more than takeprofit in some deal
            if( (d.getPrice().multiply(companyTakeprofit)).compareTo(close) <= 0){

                // if you have only 1 lot, sell it. If, lots calculates with probability

                if(d.getLotNumber() == 1)  tradeStream.sellStock(1, candle.getClose(), company.getFigi(), d);
                if ((d.getLotNumber() * probability) >= 1){
                    tradeStream.sellStock(
                            (long)(d.getLotNumber() * probability),
                            candle.getClose(),
                            company.getFigi(),
                            d
                    );
                }

            }
        }
    }

}
