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
    Init init;

    void onStart(@Observes StartupEvent ev) {
        // Método a ser executado quando a aplicação inicia
        init.incluirClientes();
    }
}