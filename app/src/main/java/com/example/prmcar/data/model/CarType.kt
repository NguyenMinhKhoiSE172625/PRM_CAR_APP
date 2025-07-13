package com.example.prmcar.data.model

data class CarTypeResponse(
    val carTypeId: Int,
    val typeName: String,
    val description: String?
)

data class CarTypeRequest(
    val typeName: String,
    val description: String?
)

data class CarTypesResponse(
    val pageIndex: Int?,
    val pageSize: Int?,
    val items: List<CarTypeResponse>
) 