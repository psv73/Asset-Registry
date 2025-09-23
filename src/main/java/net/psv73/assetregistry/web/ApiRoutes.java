package net.psv73.assetregistry.web;

public final class ApiRoutes {
    private ApiRoutes() {}
    public static final String API = "/api";

    public static final class Assets {
        public static final String ROOT   = API + "/assets";
        public static final String BY_ID  = "/{id}";
    }

    public static final class Movements {
        public static final String ROOT     = API + "/movements";
        public static final String BY_ASSET = "/asset/{assetId}";
    }

    public static final class Software {
        public static final String ROOT        = API + "/software";
        public static final String PRODUCTS    = "/products";
        public static final String SW_ON_ASSET = "/assets/{assetId}";
        public static final String ASSIGN      = "/assign";
    }

    public static final class Lookups {
        public static final String ROOT          = API + "/lookups";
        public static final String ASSET_TYPES   = "/asset-types";
        public static final String MANUFACTURERS = "/manufacturers";
        public static final String MODELS        = "/models";
        public static final String DEVICE_TYPES  = "/device-types";
        public static final String OSES          = "/oses";
        public static final String LOCATIONS     = "/locations";
        public static final String OFFICES       = "/offices";
        public static final String STATUSES      = "/statuses";
        public static final String EMPLOYEES     = "/employees";
    }
}

