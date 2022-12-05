<?php declare(strict_types=1);

require __DIR__ . '/vendor/autoload.php';

use Bref\Context\Context;
use Bref\Event\Sqs\SqsEvent;
use Bref\Event\Sqs\SqsHandler;
use Aws\DynamoDb\DynamoDbClient;
use Aws\DynamoDb\Marshaler;
use Aws\DynamoDb\Exception\DynamoDbException;

class Handler extends SqsHandler
{
    const TABLENAME = "TicketTable";
    private $client;
    private $marshaler;

    public function __construct(){
        $this->client = new DynamoDbClient([
            'region' => 'eu-central-1',
            'version' => 'latest'
        ]);
        $this->marshaler = new Marshaler();
    }

    public function handleSqs(SqsEvent $event, Context $context): void
    {
        $message = $event->getRecords()[0];
        $ticketCreatedEvent = json_decode($message->getBody());

        $eventName = $ticketCreatedEvent->eventName;
        $ticketId = $ticketCreatedEvent->ticketId;

        echo "Received ticket: $ticketId\n";

        $ticketItem = $this->marshaler->marshalJson('
            {
                "TicketId": "' . $ticketId . '-php",
                "Eventname": "' . $eventName . '"
            }
        ');

        try {
            $this->client->putItem([
                'TableName' => self::TABLENAME,
                'Item' => $ticketItem
            ]);
            echo "Added item: $eventName\n";

        } catch (DynamoDbException $e) {
            echo "Unable to add item:\n";
            echo $e->getMessage() . "\n";
        }
    }
}

return new Handler();
