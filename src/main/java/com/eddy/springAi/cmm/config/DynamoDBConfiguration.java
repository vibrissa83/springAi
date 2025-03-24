package com.eddy.springAi.cmm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.regions.Region;

@Configuration
public class DynamoDBConfiguration {

    @Value("${aws.dynamodb.region}") // application.yml에 설정된 Region
    private String region;

    @Value("${aws.dynamodb.access-key}") // AWS Access Key
    private String accessKey;

    @Value("${aws.dynamodb.secret-key}") // AWS Secret Key
    private String secretKey;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        // 자격증명을 설정하여 DynamoDbClient 생성
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        return DynamoDbClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
}