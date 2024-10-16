package com.aws.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;

import java.util.Map;
import java.util.HashMap;

public class Handler implements RequestHandler<Map<String,Object>, Map<String, Object>>{
    private final AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
    private final DynamoDB client = new DynamoDB(dynamoDB);
    private final String tableName = "serverless-db";

    LambdaLogger logger; // DEBUGGER

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        Map<String, Object> response = new HashMap<>();
        logger = context.getLogger();
        String methodType = (String) event.get("httpMethod");

        logger.log("Received event: " + event);
        logger.log("HTTP Method: " + methodType);

        if (!methodType.equals("GET")) {
            response.put("statusCode", 405); // Method NOT ALLOWED
            return response;
        }

        try {
            String partitionKey = "counter";
            Table table = client.getTable(tableName);
            GetItemSpec spec = new GetItemSpec().withPrimaryKey("id", partitionKey);
            Item item = table.getItem(spec);

            if (item == null) {
                logger.log("Item with id 'counter' not found");
                response.put("statusCode", 404); // NOT FOUND
            } else {
                logger.log("print5");
                UpdateItemSpec updateSpec = new UpdateItemSpec()
                        .withPrimaryKey("id", partitionKey)
                        .withUpdateExpression("set #c = #c + :val")
                        .withNameMap(new HashMap<String, String>() {{
                            put("#c", "count");
                        }})
                        .withValueMap(new HashMap<String, Object>() {{
                            put(":val", 1);
                        }})
                        .withReturnValues("UPDATED_NEW");

                UpdateItemOutcome outcome = table.updateItem(updateSpec);
                int updatedCount = outcome.getItem().getInt("count");
                logger.log("Count incremented to: " + updatedCount);

                response.put("statusCode", 200); // OK
                response.put("body", String.valueOf(updatedCount));

                // Add CORS headers
                response.put("headers", new HashMap<String, String>() {{
                    put("Content-Type", "application/json");
                    put("Access-Control-Allow-Origin", "*");
                    put("Access-Control-Allow-Methods", "GET, OPTIONS");
                    put("Access-Control-Allow-Headers", "Content-Type");
                }});
            }
        } catch (Exception e) {
            logger.log("Error retrieving the count: " + e.getMessage());
            response.put("statusCode", 500); // Internal Server Error
        }

        return response;
    }
}
