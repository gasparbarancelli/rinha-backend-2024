package io.github.gasparbarancelli;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.MongoSherlockBuilder;
import com.coditory.sherlock.Sherlock;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.bson.Document;

@ApplicationScoped
public class DistributedLockService {

    @Inject
    MongoClient mongoClient;

    @Produces
    public DistributedLock lock() {
        MongoCollection<Document> collection = mongoClient
                .getDatabase("sherlock")
                .getCollection("locks");

        Sherlock sherlock = MongoSherlockBuilder.mongoSherlock()
                .withLocksCollection(collection)
                .build();

        return sherlock.createLock("cliente-lock");
    }
}
