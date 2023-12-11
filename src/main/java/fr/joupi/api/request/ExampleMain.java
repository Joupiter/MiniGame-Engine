package fr.joupi.api.request;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ExampleMain {

    public static void main(String[] args) {
        RequestManager<AnotherRequest> requestManager = new AnotherRequestManager();
        UUID uuid = UUID.randomUUID();
        AnotherRequest request = new AnotherRequest(uuid, UUID.randomUUID(), "Field");

        requestManager.addRequest(request);
        requestManager.addRequest(request, 10, TimeUnit.SECONDS);
        requestManager.getOutgoingRequests(uuid).forEach(System.out::println);
        requestManager.removeRequest(request);
    }

    static class AnotherRequestManager extends RequestManager<AnotherRequest> {

        public AnotherRequestManager() {
            super(1, TimeUnit.SECONDS);
        }

        @Override
        protected void onRequestExpire(AnotherRequest request) {
            System.out.printf("[@] Request '%s' has been expired (%s)\n", request.getId(), request.getField());
        }

    }

}