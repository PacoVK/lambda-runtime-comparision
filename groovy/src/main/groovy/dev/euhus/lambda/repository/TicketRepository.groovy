package dev.euhus.lambda.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.Table
import com.amazonaws.services.dynamodbv2.model.PutItemResult
import org.apache.log4j.Logger

class TicketRepository {

  private static final Logger LOG = Logger.getLogger(TicketRepository.class)

  private Table ticketTable

  TicketRepository() {
    AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.standard().withRegion("eu-central-1").build()
    this.ticketTable = new DynamoDB(dynamoDBClient).getTable("TicketTable")
  }

  PutItemResult putTicket(String ticketId, String eventName) throws Exception{
    LOG.info("Adding new Ticket " + ticketId)
    return ticketTable.putItem(new Item().withPrimaryKey("TicketId", ticketId + "-groovy")
            .with("Eventname", eventName)).getPutItemResult()
  }
}
