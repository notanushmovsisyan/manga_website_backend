package org.manga_backend.trackingms.config;

import org.manga_backend.trackingms.persistence.TrackingEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.model.StreamSpecification;
import software.amazon.awssdk.services.dynamodb.model.StreamViewType;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    // This pulls from the environment variable we set in docker-compose
    @Value("${AWS_DYNAMODB_ENDPOINT:http://localhost:8000}")
    private String endpoint;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                // Local credentials for DynamoDB Local
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("local", "local")))
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient enhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public DynamoDbTable<TrackingEntity> trackingTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table("manga_tracking", TableSchema.fromBean(TrackingEntity.class));
    }

    @Bean
    public CommandLineRunner initializeTable(DynamoDbTable<TrackingEntity> trackingTable) {
        return args -> {
            try {
                System.out.println("Checking if DynamoDB table 'manga_tracking' exists...");

                trackingTable.createTable(builder -> builder
                        .provisionedThroughput(b -> b
                                .readCapacityUnits(5L)
                                .writeCapacityUnits(5L)
                        )
                        .streamSpecification(StreamSpecification.builder()
                                .streamEnabled(true)
                                .streamViewType(StreamViewType.NEW_AND_OLD_IMAGES)
                                .build()
                        )
                        .globalSecondaryIndices(gsi -> gsi
                                .indexName("HistoryIndex")
                                .projection(p -> p.projectionType(ProjectionType.ALL))
                                .provisionedThroughput(ProvisionedThroughput.builder()
                                        .readCapacityUnits(5L)
                                        .writeCapacityUnits(5L)
                                        .build())
                        )
                );

                System.out.println("Table 'manga_tracking' created successfully with HistoryIndex.");
            } catch (ResourceInUseException e) {
                System.out.println("Table 'manga_tracking' already exists. Skipping creation.");
            } catch (Exception e) {
                System.err.println("Error creating DynamoDB table: " + e.getMessage());
            }
        };
    }
}