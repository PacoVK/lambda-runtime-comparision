import { DynamoDB } from "aws-sdk";

const dynamoDbClient = new DynamoDB.DocumentClient({ region: "eu-central-1" });

export const putTicket = (ticketId: string, eventName: string) => {
  var params = {
    TableName: "TicketTable",
    Item: {
      TicketId: `${ticketId}-typescript`,
      Eventname: eventName,
    },
  };
  return dynamoDbClient.put(params, (err, data) => {
    var response = {
      status: true,
      message: "Successfully inserted",
      error: "",
    };
    if (err) {
      response.status = false;
      response.message = "Failed to insert into DynamoDB";
      response.error = err.message;
    }
    return response;
  });
};
