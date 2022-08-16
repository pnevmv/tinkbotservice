package ru.chnr.vn.tinkbotservice.controller;

import io.grpc.stub.StreamObserver;

import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.chnr.vn.common.generated.*;
import ru.chnr.vn.tinkbotservice.service.BotManager;


@GrpcService
public class BotServiceImpl extends BotClientGrpc.BotClientImplBase{
    @Autowired
    BotManager bots;

    @Override
    public void createBot(Token token, StreamObserver<BotID> observer){
        observer.onNext(BotID
                .newBuilder()
                .setBotID(
                bots.createBot(token.getToken())
                ).build()
        );

        observer.onCompleted();
    }
}
