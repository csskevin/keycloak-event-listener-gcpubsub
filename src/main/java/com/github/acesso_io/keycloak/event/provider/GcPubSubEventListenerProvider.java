package com.github.acesso_io.keycloak.event.provider;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.jboss.logging.Logger;

import java.util.Map;

public class GcPubSubEventListenerProvider implements EventListenerProvider {

	private static Logger logger = Logger.getLogger(GcPubSubEventListenerProvider.class);
	private GcPubSubConfig cfg;
	// private ConnectionFactory factory;

	public GcPubSubEventListenerProvider(GcPubSubConfig cfg) {
		this.cfg = cfg;
	}

	@Override
	public void close() {
	}

	@Override
	public void onEvent(Event event) {
		EventClientNotificationGcpsMsg msg = EventClientNotificationGcpsMsg.create(event);
		Map<String, String> messageAttributes = GcPubSubAttributes.createMap(event);
		String messageString = GcPubSubConfig.writeAsJson(msg, true);
		String topicId = cfg.getAdminEventTopicId();

		this.publishNotification(topicId, messageString, messageAttributes);
	}

	@Override
	public void onEvent(AdminEvent event, boolean includeRepresentation) {
		EventAdminNotificationGcpsMsg msg = EventAdminNotificationGcpsMsg.create(event);
		Map<String, String> messageAttributes = GcPubSubAttributes.createMap(event);
		String messageString = GcPubSubConfig.writeAsJson(msg, true);
		String topicId = cfg.getAdminEventTopicId();

		this.publishNotification(topicId, messageString, messageAttributes);
	}

	private void publishNotification(String topicId, String messageString, Map<String, String> attributes) {
		GcPubSubPublishConfig publishConfig = new GcPubSubPublishConfig();
		publishConfig.setTopicId(topicId);
		publishConfig.setMessageString(messageString);
		publishConfig.setAttributes(attributes);
		GcPubSubEventListenerPublisherThread publisher = new GcPubSubEventListenerPublisherThread(this.cfg, publishConfig);
		publisher.start();
	}

}
