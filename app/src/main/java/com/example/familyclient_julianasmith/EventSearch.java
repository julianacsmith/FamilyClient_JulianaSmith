package com.example.familyclient_julianasmith;

public class EventSearch {
    private final String eventType;
    private final String description;
    private final String personName;

    public EventSearch(String eventType, String description, String personName) {
        this.eventType = eventType;
        this.description = description;
        this.personName = personName;
    }

    public String getEventType() {
        return eventType;
    }

    public String getDescription() {
        return description;
    }

    public String getPersonName() {
        return personName;
    }
}
