package com.example.recipe_app_compose.features.location.domain.model.location

data class GeoCodingResponse(
    val results: List<GeoCodingResult>,
    val status: String
)

data class GeoCodingResult(
    val formatted_address: String
)

/*
{
    "plus_code": {
    "global_code": "64VWXQMC+HJ9"
},
    "results": [
    {
        "address_components": [
        {
            "long_name": "64VWXQMC+HJ",
            "short_name": "64VWXQMC+HJ",
            "types": [
            "plus_code"
            ]
        }
        ],
        "formatted_address": "64VWXQMC+HJ",
        "geometry": {
        "bounds": {
        "northeast": {
        "lat": 7.984000000000001,
        "lng": -121.228375
    },
        "southwest": {
        "lat": 7.983874999999999,
        "lng": -121.2285
    }
    },
        "location": {
        "lat": 7.983899999999999,
        "lng": -121.2284
    },
        "location_type": "GEOMETRIC_CENTER",
        "viewport": {
        "northeast": {
        "lat": 7.985286480291502,
        "lng": -121.2270885197085
    },
        "southwest": {
        "lat": 7.982588519708497,
        "lng": -121.2297864802915
    }
    }
    },
        "place_id": "GhIJOSNKe4PvH0ARApoIG55OXsA",
        "plus_code": {
        "global_code": "64VWXQMC+HJ"
    },
        "types": [
        "plus_code"
        ]
    }
    ],
    "status": "OK"
}

*/