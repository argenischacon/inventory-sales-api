package com.argenischacon.inventory_sales_api.config;

public final class OpenApiExamples {

    private OpenApiExamples() {
    }

    /**
     * Common error responses applicable to many endpoints
     */
    public static final class CommonErrors {
        private CommonErrors() {
        }

        public static final String ERROR_400_TYPE_MISMATCH = """
                {
                    "timestamp": "2024-08-01T12:00:00.000Z",
                    "status": 400,
                    "error": "Bad Request",
                    "message": "The parameter 'id' with value 'abc' is invalid. Expected type 'Long'."
                }
                """;

        public static final String ERROR_400_VALIDATION_FAILED = """
                {
                    "timestamp": "2024-08-01T12:00:30.000Z",
                    "status": 400,
                    "error": "Bad Request",
                    "message": "Validation failed.",
                    "details": {
                        "name": "Name is required"
                    }
                }
                """;

        public static final String ERROR_400_MALFORMED_JSON = """
                {
                    "timestamp": "2024-08-01T12:00:45.000Z",
                    "status": 400,
                    "error": "Bad Request",
                    "message": "The request body is malformed or contains invalid data types. Please check the JSON format."
                }
                """;

        public static final String ERROR_401_UNAUTHORIZED = """
                {
                  "timestamp": "2024-08-01T12:01:00.000Z",
                  "status": 401,
                  "error": "Unauthorized",
                  "message": "Authentication is required to access this resource. Please provide a valid token."
                }
                """;

        public static final String ERROR_403_FORBIDDEN = """
                {
                  "timestamp": "2024-08-01T12:04:00.000Z",
                  "status": 403,
                  "error": "Forbidden",
                  "message": "Access Denied: You do not have the required permissions to access this resource."
                }
                """;
    }
}