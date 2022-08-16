package ru.chnr.vn.tinkbotservice.domain;

import ru.chnr.vn.tinkbotservice.connection.CandleSource;
import ru.chnr.vn.tinkbotservice.connection.CandleStream;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Class that contains all information about instrument you want bot to trade
 * figi - unique id of instrument
 * moneyToTrade - money you allowed bot can use to trade on instrument, must be less that whole money on your acc
 * lossPercent - how many percent instrument can lose in price before you sell it
 * takeProfit - percent instrument should grow in price, before you sell it
 * lot - number of instrument in 1 lot
 */
public class Company {

    private final String figi;
    private double moneyToTrade;
    private double freeMoney;
    private double lossPercent;
    private double takeProfit;
    private boolean isTrading;
    private int shareNumber;
    private int lot;
    private HashMap<IndexType, Index> companyIndexes;
    private OpenDeals openDeals;

    public Company(String figi, double moneyToTrade, double lossPercent, double takeProfit, int lot) {
        this.figi = figi;
        this.moneyToTrade = moneyToTrade;
        this.freeMoney = moneyToTrade;
        this.lossPercent = lossPercent;
        this.takeProfit = takeProfit;
        this.lot = lot;
        this.companyIndexes = initializeIndexes();
        this.shareNumber = 0;
        this.isTrading = false;
        this.openDeals = new OpenDeals();
    }

    private HashMap<IndexType, Index> initializeIndexes(){
        HashMap<IndexType, Index> companyIndex = new HashMap<>();
        companyIndex.put(IndexType.RSI, new Index(IndexType.RSI, 0, CandleInterval.CANDLE_INTERVAL_1_MIN, 14));
        companyIndex.put(IndexType.NVI, new Index(IndexType.PVI, 0, CandleInterval.CANDLE_INTERVAL_1_MIN, 1));
        companyIndex.put(IndexType.PVI, new Index(IndexType.NVI, 0, CandleInterval.CANDLE_INTERVAL_1_MIN, 1));
        return companyIndex;
    }

    public Company(String figi, double moneyToTrade, double freeMoney, double lossPercent, double takeProfit, boolean isTrading, int shareNumber, int lot, HashMap<IndexType, Index> companyIndexes, OpenDeals openDeals) {
        this.figi = figi;
        this.moneyToTrade = moneyToTrade;
        this.freeMoney = freeMoney;
        this.lossPercent = lossPercent;
        this.takeProfit = takeProfit;
        this.isTrading = isTrading;
        this.shareNumber = shareNumber;
        this.lot = lot;
        this.companyIndexes = companyIndexes;
        this.openDeals = openDeals;
    }

    /**
     * make company reade for trading. to begin trade use candleStream uodateSubscription methoa
     * @param candleSource
     */
    public void startTrade(CandleSource candleSource) {
        for (Index index: companyIndexes.values()) {
            index.loadHistory(figi, candleSource);//TODO:calculating initial indexes
        }
        this.isTrading = true;
    }

    /**
     * stop trading of company
     * @param candleStream
     */
    public void tradeOff(CandleStream candleStream) {
        this.isTrading = false;
        candleStream.updateSubscription();
    }

    /**
     * change company freemoney, openDeals and shareValue after buying some lots
     * @param lotNumber
     * @param price - price of whole deal (lot * price * lotNumber)
     */
    public void buyShares(long lotNumber, BigDecimal price, String id) {
        BigDecimal lossCoefficient = BigDecimal.valueOf(1 - lossPercent / 100 + 0.006); // 0.006 - комиссия в обе стороны
        BigDecimal stopPrice = (price.multiply(lossCoefficient));
        getOpenDeals().addDeal(new Deal(lotNumber, price, stopPrice, id));

        this.freeMoney -= (price.multiply(BigDecimal.valueOf(lotNumber))).doubleValue();
        this.shareNumber += lotNumber * this.lot;
    }

    /**
     * change company free money, openDeals and shareValue after selling some lots
     * @param deal
     * @param lotNumber
     * @param price
     * @param id
     */
    public void sellShares (Deal deal, long lotNumber, BigDecimal price, String id) {
        getOpenDeals().deletePartly(deal, lotNumber);

        this.freeMoney += (price.multiply(BigDecimal.valueOf(lotNumber))).doubleValue();
        this.shareNumber -= lotNumber * this.lot;
    }


    public OpenDeals getOpenDeals() {
        return this.openDeals;
    }

    public double getLossPercent() {
        return this.lossPercent;
    }

    public double getTakeProfit() {
        return this.takeProfit;
    }

    public boolean getIsTrading() {
        return this.isTrading;
    }

    public void setShareNum(int number) {
        this.shareNumber = number;
    }

    public int getShareNumber() {
        return this.shareNumber;
    }

    public String getFigi() {
        return this.figi;
    }

    public void setLossPercent(double value) {
        this.lossPercent = value;
    }

    public void setTakeProfit(double value) {
        this.takeProfit = value;
    }

    public double getMoneyToTrade() {
        return this.moneyToTrade;
    }

    public double getFreeMoney() {
        return this.freeMoney;
    }

    public void setIndexValue(IndexType indexType, double value) {
        companyIndexes.get(indexType).setValue(value);
    }

    public Index getIndexByType(IndexType indexType) {
        return this.companyIndexes.get(indexType);
    }

    public int getLot() {
        return lot;
    }

    @Override
    public String toString() {
        return "Компания:"
                + "\nfigi: " + this.getFigi()
                + "\nКол-во доступных средств на покупку: " + this.getFreeMoney()
                + "\nДопустимый процент потерь: " + this.getLossPercent()
                + "\nЦель-профит: " + this.getTakeProfit()
                + "\nТрейдинг-статус: " + this.getIsTrading()
                + "\nКол-во купленных акций: " + this.getShareNumber();
    }
}
