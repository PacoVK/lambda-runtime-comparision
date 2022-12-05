import { putTicket } from "./repository/ticketRepository";
import { TicketCreatedEvent } from "./types";

import { SQSEvent } from "aws-lambda";

export const handler: AWSLambda.Handler = async (event: SQSEvent) => {
  const { ticketId, eventName } = JSON.parse(
    event.Records[0].body
  ) as TicketCreatedEvent;

  console.log("Received ticket: ", ticketId);
  await putTicket(ticketId, eventName);
  return null;
};
