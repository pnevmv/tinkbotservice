package ru.chnr.vn.tinkbotservice.processor.calculators;

import ru.chnr.vn.tinkbotservice.domain.Company;
import ru.tinkoff.piapi.contract.v1.Candle;

/**
 * Class for calculate NVI. Hadnt realized yet. to-do staff
 */
public class NVICalculator implements IndexCalculator {

    @Override
    public double calculateIndex(Company company, Candle candle) {
        return 0;
    }
}
