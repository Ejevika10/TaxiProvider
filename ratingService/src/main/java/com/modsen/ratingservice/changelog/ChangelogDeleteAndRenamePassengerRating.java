package com.modsen.ratingservice.changelog;

import com.mongodb.MongoNamespace;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty;

@ChangeUnit(order = "110", id = "6", author = "loziukvika10@gmail.com")
public class ChangelogDeleteAndRenamePassengerRating {
    @Execution
    public void executionMethodName(final MongoTemplate mongoTemplate) {
        mongoTemplate.dropCollection("passenger_ratings");

        mongoTemplate.getDb().getCollection("new_passenger_ratings").renameCollection(new MongoNamespace(mongoTemplate.getDb().getName(), "passenger_ratings"));
    }

    @RollbackExecution
    public void rollbackMethodName(final MongoTemplate mongoTemplate) {
        mongoTemplate.getDb().getCollection("passenger_ratings").renameCollection(new MongoNamespace(mongoTemplate.getDb().getName(), "new_passenger_ratings"));

        mongoTemplate.createCollection("passenger_ratings", CollectionOptions.empty()
                .validator(Validator.schema(MongoJsonSchema.builder()
                        .required("rideId", "userId", "rating")
                        .properties(
                                JsonSchemaProperty.int64("rideId"),
                                JsonSchemaProperty.int64("userId"),
                                JsonSchemaProperty.int32("rating"),
                                JsonSchemaProperty.string("comment").description("may be null"),
                                JsonSchemaProperty.bool("deleted")
                        ).build())
                )
        );
        mongoTemplate.indexOps("passenger_ratings")
                .ensureIndex(new Index("rideId", Direction.ASC)
                        .unique());
    }
}
