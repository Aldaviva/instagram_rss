package org.glassfish.jersey.client;

public class ClientlessClientConfig extends ClientConfig {

    public ClientlessClientConfig() {
        super(new AlwaysOpenJerseyClient());
    }

    @Override
    void checkClient() throws IllegalStateException {
        //test clients don't have a parent instance
    }

}