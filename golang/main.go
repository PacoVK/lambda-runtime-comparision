package main

import (
	"context"
	"encoding/json"
	"fmt"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/attributevalue"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"log"
)

func main() {
	lambda.Start(requestHandler)
}

type TicketCreatedEvent struct {
	TicketId  string `json:"ticketId"`
	Eventname string `json:"eventName"`
}

func requestHandler(ctx context.Context, sqsEvent events.SQSEvent) error {
	sdkConfig, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		log.Fatal(err)
	}

	dbClient := *dynamodb.NewFromConfig(sdkConfig)

	ticketCreatedEvent := TicketCreatedEvent{}

	err = json.Unmarshal([]byte(sqsEvent.Records[0].Body), &ticketCreatedEvent)
	if err != nil {
		log.Fatal(err)
	}

	ticketCreatedEvent.TicketId += "-golang"

	result, err := attributevalue.MarshalMap(ticketCreatedEvent)
	if err != nil {
		fmt.Println("Failed to marshall request")
		return err
	}

	input := &dynamodb.PutItemInput{
		Item:      result,
		TableName: aws.String("TicketTable"),
	}

	_, err = dbClient.PutItem(ctx, input)
	if err != nil {
		fmt.Println("Failed to write to DB")
		return err
	}

	return nil
}
