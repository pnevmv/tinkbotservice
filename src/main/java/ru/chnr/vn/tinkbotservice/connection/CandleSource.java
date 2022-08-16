package ru.chnr.vn.tinkbotservice.connection;

import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

import java.util.Queue;

/**
 * Interface for all sources, that gives us information about candles
 */
public interface CandleSource {

    /**
     * @param figi - unique id of instrument
     * @param candleInterval - interval of candle history
     * @param candleStepsBack - number of records back in history
     * @return queue of past candles
     */
    Queue<HistoricCandle> uploadCandles(String figi, CandleInterval candleInterval, int candleStepsBack);
}
