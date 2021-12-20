package com.github.acesso_io.keycloak.event.provider;

import java.util.Map;

public class GcPubSubPublishConfig {
    private String topicId;
    private String messageString;
    private Map<String, String> attributes;

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getMessageString() {
        return messageString;
    }

    public void setMessageString(String messageString) {
        this.messageString = messageString;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

}
