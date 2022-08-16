package ru.chnr.vn.tinkbotservice.processor.calculators;

import ru.chnr.vn.tinkbotservice.domain.Company;
import ru.chnr.vn.tinkbotservice.domain.IndexType;
import ru.chnr.vn.tinkbotservice.processor.MoneyQuotationProcessor;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for calculation RSI
 * RSI = 100 - 100 / (1 + RS)
 * RS = AverageOf(P)/ AverageOf(N)
 * P - sum of differences between close of neighbour candles, when close(i) > close(i-1)
 * N - sum of differences between close of neighbour candles, when close(i - 1) > close(i)
 * RSI used for detecting moments to buy/sell stocks
 */
public final class RSICalculator implements IndexCalculator {

    @Override
    public double calculateIndex(Company company, Candle candle) {
        BigDecimal positive = BigDecimal.ZERO;
        BigDecimal negative = BigDecimal.ZERO;
        BigDecimal curCandleCLose = MoneyQuotationProcessor.convertFromQuation(candle.getClose());
        List<BigDecimal> historyInNum = new ArrayList<>();

        // translate History List to BigDecimal  List of Close prices
        for(HistoricCandle c : company.getIndexByType(IndexType.RSI).getHistoryAsList()){
            historyInNum.add(MoneyQuotationProcessor.convertFromQuation(c.getClose()));

        }

        //calculate P and N on history
        for(int i = 1; i < historyInNum.size(); i++) {
            if (historyInNum.get(i).compareTo(historyInNum.get(i - 1)) > 0) // pos += close(i) - close(i - 1)
                positive = positive.add(
                        historyInNum.get(i)
                                .subtract(historyInNum.get(i - 1))
                );
            if (historyInNum.get(i).compareTo(historyInNum.get(i - 1)) < 0) // neg += close(i - 1) - close(i)
                negative = negative.add(
                        historyInNum.get(i - 1)
                                .subtract(historyInNum.get(i))
                );
        }

        //add new candle to P or N
        if(curCandleCLose.compareTo(historyInNum.get(historyInNum.size() - 1)) > 0) //pos += candle.close - close(last)
            positive = positive.add(
                    curCandleCLose
                            .subtract(historyInNum.get(historyInNum.size() - 1)));

        if(curCandleCLose.compareTo(historyInNum.get(historyInNum.size() - 1)) < 0) //neg += close(last) - candle.close
            negative = negative.add(
                    historyInNum.get(historyInNum.size() - 1)
                            .subtract(curCandleCLose)
            );

        // Make new candle part of history
        company.getIndexByType(IndexType.RSI).updateHistory(candle); //update history of candles

        //zero dividing protection
        if(negative.equals(BigDecimal.ZERO)) return 100; //case N = 0; RSI = 100

        return  RSICalcFromNegAndPos(positive, negative).doubleValue();
    }


    /**
     * calculate rsi as RSI = 100 - 100 / (1 + RS) , RS = P/N
     * @param pos - sum of positive changes in close price for period
     * @param neg - sum of negative changes in close price for period
     * @return
     */
    private BigDecimal RSICalcFromNegAndPos(BigDecimal pos, BigDecimal neg){
        return BigDecimal.valueOf(100).subtract(
                BigDecimal.valueOf(100).divide(
                        BigDecimal.ONE.add(
                                pos.divide(neg, 9, RoundingMode.HALF_DOWN)  // (pos/size) / (neg/size) = pos/neg
                        ), 9, RoundingMode.HALF_DOWN
                )
        );
    }

    //Tests RSI Calc on hand inputted sample of close prices
    public void testRSICalc(List<Long> closes) {
        //candle = last element of sample, has no influence on result
        Candle fakeCandle = Candle.newBuilder().setClose(
                Quotation.newBuilder().setUnits(
                        closes.get(closes.size() - 1)).build())
                .build();
        Company fakeCompany = CreateFakeTestHistory(closes);

        //calculate by method
        double resOfCalculator = calculateIndex(fakeCompany, fakeCandle);

        //calculate by hands
        double neg = 0;
        double pos = 0;
        for(int i = 1; i < closes.size(); i++){
            if(closes.get(i) > closes.get(i - 1))  pos += closes.get(i) - closes.get(i - 1);
            if(closes.get(i - 1) > closes.get(i))  neg += closes.get(i - 1) - closes.get(i);
        }
        double resOfHandCalc = 100 - 100 / (1 + (pos / neg));

    }

    /**
     *
     * @param closes - List of close prices to create history for testing calculations
     * @return fake company to test calculating
     */
    public Company CreateFakeTestHistory(List<Long> closes){
        Company c = new Company("test", 1, 1, 1, 1);
        LinkedList<HistoricCandle> hist = new LinkedList<>();
        for(Long close : closes){
            hist.add(HistoricCandle.newBuilder().setClose(
                    Quotation.newBuilder().setUnits(close).build())
                    .build());
        }
        c.getIndexByType(IndexType.RSI).setCandleHistory(hist);
        return c;
    }
}
