package com.github.acesso_io.keycloak.event.provider;

import com.google.cloud.pubsub.v1.Publisher;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class GcPubSubPublisherInstance {
    private static GcPubSubPublisherInstance instance = null;

    public Publisher publisher = null;
    private final Semaphore counterLock = new Semaphore(1, true);
    private int openConnections = 0;


    private GcPubSubPublisherInstance() {}

    public static GcPubSubPublisherInstance getInstance() {
        if(instance == null)
            instance = new GcPubSubPublisherInstance();
        return instance;
    }

    public void usePublisherConnection() throws InterruptedException {
        this.counterLock.acquire();
        this.openConnections++;
        this.counterLock.release();
    }

    public void closePublisherConnection() throws InterruptedException {
        this.counterLock.acquire();
        this.openConnections--;
        if(this.openConnections == 0 && this.publisher != null) {
            this.publisher.shutdown();
            this.publisher.awaitTermination(1, TimeUnit.MINUTES);
            this.publisher = null;
        }
        this.counterLock.release();
    }
}
