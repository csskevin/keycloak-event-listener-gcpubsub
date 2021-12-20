package com.github.acesso_io.keycloak.event.provider;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.jboss.logging.Logger;

import java.util.Map;

public class GcPubSubEventListenerPublisherThread extends Thread {
    private static Logger logger = Logger.getLogger(GcPubSubEventListenerPublisherThread.class);
    private GcPubSubConfig cfg;
    private GcPubSubPublishConfig publishConfig;

    public GcPubSubEventListenerPublisherThread(GcPubSubConfig cfg, GcPubSubPublishConfig publishConfig) {
        this.cfg = cfg;
        this.publishConfig = publishConfig;
    }

    @Override
    public void run() {
        try {
            this.publishNotification(publishConfig.getTopicId(), publishConfig.getMessageString(), publishConfig.getAttributes());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void publishNotification(String topicId, String messageString, Map<String, String> attributes) throws InterruptedException {

        TopicName topicName = TopicName.of(cfg.getProjectId(), topicId);

        GcPubSubPublisherInstance publisherInstance = GcPubSubPublisherInstance.getInstance();
        publisherInstance.usePublisherConnection();
        try {
            // Create a publisher instance with default settings bound to the topic
            if(publisherInstance.publisher == null)
                publisherInstance.publisher = Publisher.newBuilder(topicName).build();


            String message = messageString;
            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).putAllAttributes(attributes).build();

            // Once published, returns a server-assigned message id (unique within the
            // topic)
            ApiFuture<String> messageIdFuture = publisherInstance.publisher.publish(pubsubMessage);
            String messageId = messageIdFuture.get();
            logger.info("Published message ID: " + messageId);
        } catch (Exception ex) {
            logger.error("keycloak-to-gcpubsub ERROR sending message: " + attributes, ex);
        } finally {
            try {
                // When finished with the publisher, shutdown to free up resources.
                publisherInstance.closePublisherConnection();
            } catch (InterruptedException ex) {
                logger.error("keycloak-to-gcpubsub ERROR shutting down publisher: " + attributes, ex);
            }
        }

    }
}
