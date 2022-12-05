package dev.euhus.lambda.repository;

import org.apache.log4j.Logger;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class TicketRepository {

    private static final Logger LOG = Logger.getLogger(TicketRepository.class);

    public TicketRepository(DynamoDbClient dynamoDB) {
        this.dynamoDB = dynamoDB;
    }
    DynamoDbClient dynamoDB;

    public PutItemResponse putTicket(String ticketId, String eventName) {
        LOG.info("Adding new Ticket " + ticketId);
        Map<String, AttributeValue> ticketItem = new HashMap<>();
        ticketItem.put("TicketId", AttributeValue.builder().s(ticketId + "-quarkus").build());
        ticketItem.put("Eventname", AttributeValue.builder().s(eventName).build());
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName("TicketTable")
                .item(ticketItem)
                .build();
        return dynamoDB.putItem(putItemRequest);
    }
}
