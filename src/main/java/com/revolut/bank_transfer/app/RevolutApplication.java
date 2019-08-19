package com.revolut.bank_transfer.app;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.revolut.bank_transfer.dao.AccountDao;

import io.dropwizard.Application;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;

public class RevolutApplication extends Application<RevolutConfiguration> {

    @Override
    public void run(RevolutConfiguration configuration, Environment environment) throws Exception {

        JerseyEnvironment jersey = environment.jersey();
        jersey.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(AccountDao.class).to(AccountDao.class).in(Singleton.class);
            }
        });
    }

}
