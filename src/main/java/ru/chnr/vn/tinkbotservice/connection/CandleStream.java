package ru.chnr.vn.tinkbotservice.connection;

import ru.chnr.vn.tinkbotservice.domain.Bot;
import ru.chnr.vn.tinkbotservice.exceptions.CompanyNotFoundException;
import ru.chnr.vn.tinkbotservice.exceptions.OutNumberOfReconnectAttemptsException;
import ru.chnr.vn.tinkbotservice.processor.DataStreamProcessor;

import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.SubscriptionInterval;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.stream.MarketDataStreamService;
import ru.tinkoff.piapi.core.stream.MarketDataSubscriptionService;

import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * Class-wrapper for stream of candles
 */
public class CandleStream implements CandleSource{
    private final Bot bot;
    private final MarketDataStreamService marketStreamServ;
    private MarketDataSubscriptionService stream;
    private final InvestApi api;

    public CandleStream(InvestApi api, Bot bot){
        this.api = api;
        this.marketStreamServ = api.getMarketDataStreamService();
        this.bot = bot;
    }

    /**
     * Creates stream
     * @param processor
     * @throws OutNumberOfReconnectAttemptsException
     * @throws CompanyNotFoundException
     */
    public void initialize(DataStreamProcessor processor) throws OutNumberOfReconnectAttemptsException, CompanyNotFoundException {

        Consumer<Throwable> streamError = e -> {
            System.out.println("something is wrong");
            System.out.println(e.toString());
            e.printStackTrace();
        };

        stream = marketStreamServ.newStream("Candles", processor::process, streamError);
        if (!bot.getFigisOfTradingCompanies().isEmpty()) stream.subscribeCandles(bot.getFigisOfTradingCompanies(), SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE);
    }

    /**
     * Updates subscription
     * Call every time you change some company isTrades flag, or delete company!
     */
    public void updateSubscription() {
        if(!bot.getFigisOfTradingCompanies().isEmpty()) {
            stream.subscribeCandles(bot.getFigisOfTradingCompanies(), SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE);
        }
    }

    /**
     * Class stream of source of candles from market data service
     */
    @Override
    public Queue<HistoricCandle> uploadCandles(String figi, CandleInterval candleInterval, int candleStepsBack) {
        return new LinkedList<>(api.getMarketDataService().getCandlesSync(figi,
                Instant.now().minusSeconds((long) secondsInCandleInterval(candleInterval) * candleStepsBack),
                Instant.now(),
                candleInterval));
    }

    /**
     * Calculate how many seconds in every candle Interval
     * @param candleInterval
     * @return econds in every candle Interval
     */
    private int secondsInCandleInterval(CandleInterval candleInterval){
        switch(candleInterval){
            case CANDLE_INTERVAL_DAY:
                return 60 * 60 * 24;
            case CANDLE_INTERVAL_HOUR:
                return 60 * 60;
            case CANDLE_INTERVAL_5_MIN:
                return 60 * 5;
            case CANDLE_INTERVAL_15_MIN:
                return 60 * 15;
            default:
                return 60;
        }
    }

}
