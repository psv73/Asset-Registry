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

    public static final class Ref {
        private Ref() {}
        public static final String STATUSES       = API_V1 + "/statuses";
        public static final String OSES           = API_V1 + "/oses";
        public static final String CLIENTS        = API_V1 + "/clients";
        public static final String MODELS         = API_V1 + "/models";
        public static final String MANUFACTURERS  = API_V1 + "/manufacturers";
        public static final String DEVICE_TYPES   = API_V1 + "/device-types";
    }
}
