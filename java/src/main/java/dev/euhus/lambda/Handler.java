package dev.euhus.lambda;

import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.euhus.lambda.event.TicketCreatedEvent;
import dev.euhus.lambda.repository.TicketRepository;
import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<SQSEvent, Void> {

	private static final Logger LOG = Logger.getLogger(Handler.class);

	private final TicketRepository ticketRepository = new TicketRepository();

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public Void handleRequest(SQSEvent sqsEvent, Context context) {
		try {
			TicketCreatedEvent ticketCreatedEvent = mapper.readValue(sqsEvent.getRecords().get(0).getBody(), TicketCreatedEvent.class);
			String ticketId = ticketCreatedEvent.getTicketId();
			String eventName = ticketCreatedEvent.getEventName();
			LOG.info("Received message with TicketID: " + ticketId);
			PutItemResult result = ticketRepository.putTicket(ticketId, eventName);
			LOG.info("PutItem succeeded:\n" + result);
		} catch (Exception e) {
			LOG.error("Unable to add item: ",e);
		}
		return null;
	}
}
