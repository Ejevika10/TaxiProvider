package com.modsen.ratingservice.changelog;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;

@ChangeUnit(order = "001", id = "0", author = "loziukvika10@gmail.com")
public class ChangelogCreatePassengerRating {
    @Execution
    public void executionMethodName(final MongoTemplate mongoTemplate) {
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
                .ensureIndex(new Index("rideId", Sort.Direction.ASC)
                        .unique());
    }
    @RollbackExecution
    public void rollbackMethodName(final MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps("passenger_ratings").dropIndex("rideId");
        mongoTemplate.dropCollection("passenger_ratings");
    }
}
