package org.manga_backend.notifms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.manga_backend.notifms.persistence.NotificationEntity;
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
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    @Value("${AWS_DYNAMODB_ENDPOINT:http://localhost:8000}")
    private String endpoint;

    @Value("${dynamodb.table.notifications:notifications-table}")
    private String tableName;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        return mapper;
    }

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
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
    public DynamoDbTable<NotificationEntity> notificationTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table(tableName, TableSchema.fromBean(NotificationEntity.class));
    }

    @Bean
    public CommandLineRunner initializeTable(DynamoDbTable<NotificationEntity> notificationTable) {
        return args -> {
            try {
                notificationTable.createTable(builder -> builder
                        .provisionedThroughput(b -> b.readCapacityUnits(5L).writeCapacityUnits(5L))
                );
                System.out.println("Table '" + tableName + "' created successfully.");
            } catch (ResourceInUseException e) {
                System.out.println("Table '" + tableName + "' already exists. Skipping creation.");
            } catch (Exception e) {
                System.err.println("Error creating table: " + e.getMessage());
            }
        };
    }
}