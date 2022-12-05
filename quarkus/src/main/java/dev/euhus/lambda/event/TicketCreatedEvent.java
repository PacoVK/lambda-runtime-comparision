package dev.euhus.lambda.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
@RegisterForReflection
public class TicketCreatedEvent {
    private String ticketId;
    private String eventName;

    public String getTicketId() {
        return ticketId;
    }

    public String getEventName() {
        return eventName;
    }
}
