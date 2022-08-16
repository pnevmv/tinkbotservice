package ru.chnr.vn.tinkbotservice.processor;

import ru.chnr.vn.tinkbotservice.domain.IndexType;
import ru.chnr.vn.tinkbotservice.processor.calculators.IndexCalculator;
import ru.chnr.vn.tinkbotservice.processor.calculators.*;

import java.util.HashMap;

/**
 * Class For storing all calculators. You can add your own if wants
 */
public class IndexCalculators {
    HashMap<IndexType, IndexCalculator> calcMaps = new HashMap<>() ;

    public IndexCalculators (){
        calcMaps.put(IndexType.RSI, new RSICalculator());
        calcMaps.put(IndexType.NVI, new NVICalculator());
        calcMaps.put(IndexType.PVI, new PVICalculator());
    }

    public IndexCalculator getCalcByIndex(IndexType t){
        return calcMaps.get(t);}

    public  void addCalculator(IndexType t, IndexCalculator c){
        calcMaps.put(t, c);
    }
}
