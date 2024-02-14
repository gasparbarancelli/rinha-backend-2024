package io.github.gasparbarancelli;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.SqlSherlockBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.time.Clock;
import java.time.Duration;

@ApplicationScoped
public class DistributedLockService {

    @Inject
    DataSource dataSource;

    @Produces
    public DistributedLock lock() {
        Sherlock sherlock= SqlSherlockBuilder.sqlSherlock()
                .withClock(Clock.systemDefaultZone())
                .withLockDuration(Duration.ofMinutes(5))
                .withUniqueOwnerId()
                .withConnectionPool(dataSource)
                .withLocksTable("LOCKS")
                .build();

        return sherlock.createLock("cliente-lock");
    }
}
