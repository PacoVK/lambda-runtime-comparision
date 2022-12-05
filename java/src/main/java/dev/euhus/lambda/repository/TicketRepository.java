package dev.euhus.lambda.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import org.apache.log4j.Logger;

public class TicketRepository {

    private static final Logger LOG = Logger.getLogger(TicketRepository.class);

    private Table ticketTable;

    public TicketRepository() {
        AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.standard().withRegion("eu-central-1").build();
        this.ticketTable = new DynamoDB(dynamoDBClient).getTable("TicketTable");
    }

    public PutItemResult putTicket(String ticketId, String eventName) throws Exception{
        LOG.info("Adding new Ticket " + ticketId);
        return ticketTable.putItem(new Item().withPrimaryKey("TicketId", ticketId + "-java")
                    .with("Eventname", eventName)).getPutItemResult();
    }
}
