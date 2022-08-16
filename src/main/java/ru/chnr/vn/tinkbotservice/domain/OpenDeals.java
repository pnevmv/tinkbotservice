package ru.chnr.vn.tinkbotservice.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class for working with history of openDeals - staff, that bot bought, and should to sell in some cases
 * Deal store price of buying, date, price of stopLoss, number of bought lots
 * Open deals stores concurrent List, in which you can add deal, delete deal, print etc.
 * Class-wrapper for collection of deals
 */
public class OpenDeals {
    private CopyOnWriteArrayList<Deal> openDeals;

    public OpenDeals() {
        this.openDeals = new CopyOnWriteArrayList<>();
    }

    public OpenDeals(Collection<Deal> deals){
        this.openDeals = new CopyOnWriteArrayList<>(deals);
    }

    /**
     * add deal to collection
     * @param deal
     */
    public void addDeal(Deal deal) {
        openDeals.add(deal);
    }

    /**
     * delete deal from collection
     * @param deal
     */
    private void deleteDeal(Deal deal) {
        openDeals.remove(deal);
    }

    /**
     * if deal was sold partly, we replace this deal with new deal with same parameters, besides lots
     * Lots of new deal = Lots of old deal - sold deal
     * @param deal
     * @param numberOfSoldLots
     */
    public void deletePartly(Deal deal, long numberOfSoldLots) {
        if(deal.getLotNumber() - numberOfSoldLots != 0) {
            addDeal(new Deal(deal.getLotNumber() - numberOfSoldLots,
                            deal.getPrice(),
                            deal.getStopPrice(),
                            String.valueOf(Double.valueOf(Math.random()).hashCode())
                    )
            );
        }
        deleteDeal(deal);
    }

    /**
     *
     * @return oopenDeals in List representation
     */
    public List<Deal> getDealsAsList() {
        return this.openDeals;
    }

    /**
     * sort open deals by price
     */
    public void sortByPrices() {
        this.openDeals.sort(Comparator.comparing(Deal::getPrice));
    }

    /**
     *
     * @return - average price of all deals
     */
    public BigDecimal getAveragePrice() {
        BigDecimal price = BigDecimal.ZERO;
        for (Deal deal: openDeals) {
            price = price.add(deal.getPrice());
        }

        return price.divide(BigDecimal.valueOf(openDeals.size()), 9, RoundingMode.HALF_DOWN);
    }

    @Override
    public String toString() {
        return "Кол-во сделок: " + this.openDeals.size()
                + "Средняя стоимость всех акций: " + getAveragePrice();
    }

    /**
     *
     * @param id
     * @return - Optional, that present if openDeals contains Deal with id
     */
    public Optional<Deal> getDealById(String id){
        for(Deal d : openDeals){
            if(id.equals(d.getId())){
                return Optional.of(d);
            }
        }
        return Optional.empty();
    }

    /**
     * prints all Deals
     */
    public void printDeals(){
        System.out.println("\n");
        System.out.println("Открытые сделки:");
        for(Deal d : openDeals) System.out.println(d.toString());
        System.out.println("\n");
    }
}
