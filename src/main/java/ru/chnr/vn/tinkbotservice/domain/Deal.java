package ru.chnr.vn.tinkbotservice.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 *  Deal is record of bought stock.
 *  Use it to detect moment to sell stock(when price will be more than 110% of bought price in deal, for example
 *  Deal store price of buying, date, price of stopLoss, number of bought lots
 */
public class Deal {
    private final String id;
    private final long lotNumber;
    private final BigDecimal price;
    private final Date date;
    private final BigDecimal stopPrice;

    public Deal(long lotNumber, BigDecimal price, BigDecimal stopPrice, String id) {
        this.id = id;
        this.lotNumber = lotNumber;
        this.price = price;
        this.date = new Date(System.currentTimeMillis());
        this.stopPrice = stopPrice;
    }

    public long getLotNumber() {
        return this.lotNumber;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public BigDecimal getStopPrice() {
        return this.stopPrice;
    }

    public Date getDate() {
        return this.date;
    }

    public String getId(){return this.id;}

    @Override
    public String toString() {
        return "Сделка стоимостью: " + this.getPrice()
                + "\nЦена экстренной продажи:" + this.getStopPrice()
                + "\nКол-во приобретенных лотов:" + this.getLotNumber()
                + "\nДата сделки: " + this.getDate();
    }
}
