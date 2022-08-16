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
    public void deleteBot(BotID id, StreamObserver<Success> observer){
        observer.onNext(Success
                .newBuilder()
                .setIsDeleted(bots.deleteBot(id.getBotID()))
                .build()
        );
        observer.onCompleted();
    }
}
