package ru.chnr.vn.tinkbotservice.processor;

import ru.chnr.vn.tinkbotservice.domain.Company;
import ru.chnr.vn.tinkbotservice.domain.IndexType;

/**
 * Class that calculates probability to buy/sell stocks from indexes.
 * Solution to buy/sell represented as double in range [-1; 1] where '-' is to sell,
 * '+' is to buy and value is probability to do this
 * In future you can use ml to find optional coefficients for each index, or another model to combine them
 */
public class Solver {

    /**
     * @param company
     * @return Solution to buy/sell as double in range [-1; 1] where '-' is to sell,
            * '+' is to buy and value is probability to do this thing
     */
    public static double  solution(Company company){
        return RSIOnlyVersion(company);
    }


    /**
     * Version for calculate probability only with RSI, in your version you can add another indexes
     * @param company
     * @return
     */
    private static double RSIOnlyVersion(Company company){
        // Up and down limit of rsi, intersection of them are signal fo buying/selling. Standart is 70 up 30 down
        // We change it to give bot to trade frequently, but accuracy is bad
        double high = 65;
        double low = 35;
        double rsi = company.getIndexByType(IndexType.RSI).getValue();
        if(rsi > high){
            return (rsi - high) / (100 - high); // if rsi = 100 probability 100%, if 70 probability 0%
        }
        if(rsi < low){
            return (rsi - low) / (low); // if rsi = 0 probability -100% (negative is signal to sell), if 30 probability 0%
        }
        return 0;
    }
}
