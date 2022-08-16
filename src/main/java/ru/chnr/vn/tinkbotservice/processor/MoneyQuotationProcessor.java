package ru.chnr.vn.tinkbotservice.processor;

import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.function.BiFunction;

/**
 * class for work with money. You can compare quantities, or translate them to Big Decimal
 */
public class MoneyQuotationProcessor implements Comparator<Quotation> {

    /**
     * Compares money by units
     */
    public BiFunction<Quotation, Quotation, Integer> compareByUnits = (q1, q2) ->
            Comparator.comparing(Quotation::getUnits).compare(q1, q2) ;

    /**
     * Compares money by nanos
     */
    public BiFunction<Quotation, Quotation, Integer> compareByNanos = (q1, q2) ->
            Comparator.comparing(Quotation::getNano).compare(q1, q2) ;

    /**
     * Compares money
     */
    @Override
    public int compare(Quotation o1, Quotation o2) {
        BiFunction<Quotation, Quotation, Integer> compare = (q1, q2) ->
                Comparator.comparing(Quotation::getUnits).thenComparing(Quotation::getNano).compare(q1, q2);
        return compare.apply(o1, o2);
    }

    /**
     * Converts Quotation in Big Decimal with scale 9
     */
    public static BigDecimal convertFromQuation(Quotation quotation ){
        return quotation.getUnits() == 0 && quotation.getNano() == 0 ?
                BigDecimal.ZERO : BigDecimal.valueOf(quotation.getUnits()).add(BigDecimal.valueOf(quotation.getNano(), 9));
    }

    /**
     * Converts MoneyValue in Big Decimal with scale 9
     */
    public static BigDecimal convertFromMoneyValue(MoneyValue value ){
        return value.getUnits() == 0 && value.getNano() == 0 ?
                BigDecimal.ZERO : BigDecimal.valueOf(value.getUnits()).add(BigDecimal.valueOf(value.getNano(), 9));
    }

}
