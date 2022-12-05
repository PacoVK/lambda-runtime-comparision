package dev.euhus.lambda.event;

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
