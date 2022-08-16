package ru.chnr.vn.tinkbotservice.domain;

import java.util.*;

import ru.chnr.vn.tinkbotservice.connection.CandleSource;
import ru.chnr.vn.tinkbotservice.processor.MoneyQuotationProcessor;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

/**
 * Index class. contains all information to calculate indexes - history of candles
 * with necessary timestamp and depth in past. Also it contains current value of index - for recurrent-calculated indexes
 */
public class Index {
    private final IndexType indexType;
    private double value;
    private final CandleInterval candleInterval;
    private int candleStepsBack;
    private ArrayDeque<HistoricCandle> candleHistory;

    public Index(IndexType indexType, double value, CandleInterval candleInterval, int candleStepsBack) {
        this.indexType = indexType;
        this.value = value;
        this.candleInterval = candleInterval;
        this.candleStepsBack = candleStepsBack;
    }

    /**
     *
     * @return current value of index
     */
    public double getValue() {
        return value;
    }

    /**
     * @return - num of steps in the past index need as history
     */
    public int getCandleStepsBack() {
        return candleStepsBack;
    }

    public void setCandleHistory(Queue<HistoricCandle> candleHistory) {
        this.candleHistory = new ArrayDeque<>(candleHistory);
    }

    /**
     * loads history for index calculation from candleSource
     * @param figi
     * @param candleSource
     */
    public void loadHistory(String figi, CandleSource candleSource) {
        this.candleHistory = new ArrayDeque<>(candleSource.uploadCandles(figi, candleInterval, candleStepsBack));
    }

    /**
     * Updates history if you get new candle. LIFO
     * @param candle
     */
    public void updateHistory(Candle candle) {
        if(candleHistory.size() > 0) this.candleHistory.removeFirst();
        HistoricCandle c = HistoricCandle.newBuilder()
                .setClose(candle.getClose())
                .setHigh(candle.getHigh())
                .setLow(candle.getLow())
                .setOpen(candle.getOpen())
                .setTime(candle.getTime())
                .build();

        this.candleHistory.addLast(c);
    }

    public void setValue(double value) {
        this.value = value;
    }

    public IndexType getIndexType() {
        return indexType;
    }

    public Collection<HistoricCandle> getHistory(){
        return candleHistory;
    }

    /**
     *
     * @return history as list
     */
    public List<HistoricCandle> getHistoryAsList(){
        ArrayList<HistoricCandle> res = new ArrayList<>();

        for(HistoricCandle c : candleHistory)   res.add(c);
        return res;
    }

    /**
     *
     * @return - time of start of last candle in history
     */
    public long getTimeOfLastEl(){
        return candleHistory.getLast().getTime().getSeconds(); // time of last el
    }

    /**
     * prints history
     */
    public void printHistory(){
        System.out.println("History of:" + indexType.name() + "index");
        for(HistoricCandle h : candleHistory){
            System.out.println("Close price" + MoneyQuotationProcessor.convertFromQuation(h.getClose()));
            System.out.println("Start time of candle" + h.getTime().getSeconds());
        }
    }
}
