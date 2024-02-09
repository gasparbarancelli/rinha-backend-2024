package io.github.gasparbarancelli;

import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@Startup
@ApplicationScoped
public class StartupBean {

    @Inject
    Database database;

    void onStart(@Observes StartupEvent ev) {
        database.inicializar();
    }

}