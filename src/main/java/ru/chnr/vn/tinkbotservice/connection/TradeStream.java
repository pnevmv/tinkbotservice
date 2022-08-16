package ru.chnr.vn.tinkbotservice.connection;

import ru.chnr.vn.tinkbotservice.domain.Bot;
import ru.chnr.vn.tinkbotservice.domain.Company;
import ru.chnr.vn.tinkbotservice.domain.Deal;
import ru.chnr.vn.tinkbotservice.exceptions.CompanyNotFoundException;
import ru.chnr.vn.tinkbotservice.processor.MoneyQuotationProcessor;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderExecutionReportStatus;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.OrdersService;
import ru.tinkoff.piapi.core.stream.OrdersStreamService;

/**
 * class for trade orders. in current realization operates with unary calls.
 */
public class TradeStream {
    private final OrdersStreamService orderStreamService;
    private final OrdersService tradeServ;
    private static long orderId = 0;
    private String accountId;
    private Bot bot;

    public TradeStream(InvestApi api, String accountId, Bot bot) {
        orderStreamService = api.getOrdersStreamService();
        tradeServ = api.getOrdersService();
        this.accountId = accountId;
        this.bot = bot;
    }

   /* public void initialize(TradeStreamProcessor processor) {
        Consumer<Throwable> streamError = e -> System.out.println(e.toString());
        orderStreamService.subscribeTrades(processor::responseProcess
                , streamError
                , List.of(accountId));
    }*/

    /**
     * makes synchronized buy order, and if order is complete call buyShare method from Company class,
     * @param lots - number of lots
     * @param price
     * @param figi
     * @throws CompanyNotFoundException
     */
    public void buyStock(long lots, Quotation price, String figi) throws CompanyNotFoundException {
        System.out.println("going to buy");
        orderId = Double.valueOf(Math.random()).hashCode();

        //todo: verification of params
        var orderResponse = tradeServ.postOrderSync(
                figi,
                lots,
                price,
                OrderDirection.ORDER_DIRECTION_BUY,
                accountId,
                OrderType.ORDER_TYPE_MARKET,
                String.valueOf(orderId)
        );

        if(orderResponse.getExecutionReportStatus().equals(OrderExecutionReportStatus.EXECUTION_REPORT_STATUS_FILL) ||
                orderResponse.getExecutionReportStatus().equals(OrderExecutionReportStatus.EXECUTION_REPORT_STATUS_PARTIALLYFILL)){
            Company curComp = bot.getByFigi(figi);
            System.out.println("Buy lot^: " + orderResponse.getLotsExecuted() + " price: " +
                    orderResponse.getExecutedOrderPrice().toString() + " \n");

            curComp.buyShares(
                    orderResponse.getLotsExecuted(),
                    MoneyQuotationProcessor.convertFromMoneyValue(orderResponse.getExecutedOrderPrice()),
                    String.valueOf(orderId));

            curComp.getOpenDeals().printDeals();
        }

    }

    /**
     * Makes synchronized sell order, and if order is complete call sellShares method from Company class,
     * @param lots
     * @param price
     * @param figi
     * @param deal
     * @throws CompanyNotFoundException
     */
    public void sellStock(long lots, Quotation price, String figi, Deal deal) throws CompanyNotFoundException {
        System.out.println("Going to sell");
        //todo: verification of params
        var orderResponse = tradeServ.postOrderSync(
                figi,
                lots,
                price,
                OrderDirection.ORDER_DIRECTION_SELL,
                accountId,
                OrderType.ORDER_TYPE_MARKET,
                deal.getId() + "s"
        );

        if(orderResponse.getExecutionReportStatus().equals(OrderExecutionReportStatus.EXECUTION_REPORT_STATUS_FILL) ||
                orderResponse.getExecutionReportStatus().equals(OrderExecutionReportStatus.EXECUTION_REPORT_STATUS_PARTIALLYFILL))
        {
            Company curComp = bot.getByFigi(figi);
            System.out.println("Amount of felled lots " + orderResponse.getLotsExecuted() + " by price " +
                    orderResponse.getExecutedOrderPrice() + " \n");

            curComp.sellShares(
                    deal,
                    orderResponse.getLotsExecuted(),
                    MoneyQuotationProcessor.convertFromMoneyValue(orderResponse.getExecutedOrderPrice()),
                    deal.getId()
            );
            curComp.getOpenDeals().printDeals();
            System.out.println("end of selling");
        }

    }
}
