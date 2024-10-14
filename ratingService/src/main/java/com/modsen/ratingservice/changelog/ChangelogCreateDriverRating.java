package com.modsen.ratingservice.changelog;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ValidationOptions;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;

import java.util.List;

@ChangeUnit(order = "010", id = "1", author = "loziukvika10@gmail.com")
public class ChangelogCreateDriverRating {
    @Execution
    public void executionMethodName(MongoDatabase mongoDatabase) {
        Document validationRules = new Document()
                .append("$jsonSchema", new Document()
                        .append("bsonType", "object")
                        .append("required", List.of("rideId", "userId", "rating"))
                        .append("properties", new Document()
                                .append("rideId", new Document().append("bsonType", "long"))
                                .append("userId", new Document().append("bsonType", "long"))
                                .append("rating", new Document().append("bsonType", "int"))
                                .append("comment", new Document().append("bsonType", "string").append("description", "может быть null"))
                                .append("deleted", new Document().append("bsonType", "bool"))
                        )
                );
        mongoDatabase.createCollection("driver_ratings",
                new CreateCollectionOptions().validationOptions(
                        new ValidationOptions().validator(validationRules)));
        mongoDatabase.getCollection("driver_ratings").createIndex(
                new Document("rideId", 1),
                new IndexOptions().unique(true));
    }
    @RollbackExecution
    public void rollbackMethodName(MongoDatabase mongoDatabase) {
        mongoDatabase.getCollection("driver_ratings").drop();
    }
}

