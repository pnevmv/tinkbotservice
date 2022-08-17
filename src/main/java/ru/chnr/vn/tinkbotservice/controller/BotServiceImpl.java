package ru.chnr.vn.tinkbotservice.controller;

import io.grpc.stub.StreamObserver;

import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import ru.chnr.vn.common.generated.*;
import ru.chnr.vn.tinkbotservice.service.BotManager;


@GrpcService
public class BotServiceImpl extends BotClientGrpc.BotClientImplBase{
    @Autowired
    BotManager bots;

    @Override
    public void createBot(Token token, StreamObserver<BotID> observer){
        BotID response = BotID
                .newBuilder()
                .setBotID(
                        bots.createBot(token.getToken())
                ).build();

        observer.onNext(response);
        observer.onCompleted();
    }

    @Override
    public void deleteBot(BotID id, StreamObserver<Result> observer){
        observer.onNext(Result
                .newBuilder()
                .setValue(
                        bots.deleteBot(id.getBotID()))
                .build()
        );
        observer.onCompleted();
    }

    @Override
    public  void addCompany(CompanyInfo companyInfo, StreamObserver<Result> observer) {
        observer.onNext(Result
                .newBuilder()
                .setValue(
                        bots.addCompany(companyInfo.getBotID(), companyInfo))
                .build()
        );

        observer.onCompleted();
    }

    @Override
    public  void changeCompany(CompanyInfo companyInfo, StreamObserver<Result> observer) {
        observer.onNext(Result
                .newBuilder()
                .setValue(
                        bots.changeCompany(companyInfo.getBotID(), companyInfo))
                .build()
        );

        observer.onCompleted();
    }

    @Override
    public  void deleteCompany(CompanyInfo companyInfo, StreamObserver<Result> observer) {
        observer.onNext(Result
                .newBuilder()
                .setValue(
                        bots.deleteCompany(companyInfo.getBotID(), companyInfo))
                .build()
        );

        observer.onCompleted();
    }
}
