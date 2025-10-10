package net.psv73.assetregistry.web;

public final class ApiPaths {
    private ApiPaths() {}

    public static final String API_V1 = "/api/v1";

    public static final class Health {
        private Health() {}
        public static final String ROOT = API_V1 + "/health";
    }

    public static final class Assets {
        private Assets() {}
        public static final String ROOT = API_V1 + "/assets";
        public static final String BY_ID = ROOT + "/{id}";
    }
}
