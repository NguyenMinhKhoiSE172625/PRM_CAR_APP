package com.example.prmcar.data.model

data class CarResponse(
    val carId: Int,
    val make: String,
    val carName: String,
    val model: String,
    val manufactureYear: Int,
    val carTypeId: Int?,
    val carTypeName: String?,
    val color: String?,
    val mileage: Int?,
    val licensePlate: String?,
    val askingPrice: Double,
    val description: String?,
    val status: String?,
    val listingDate: String?,
    val sellerId: Int?,
    val imageUrl: String? = null
)

data class CarRequest(
    val carName: String,
    val make: String,
    val model: String,
    val manufactureYear: Int,
    val carTypeId: Int?,
    val color: String?,
    val mileage: Int?,
    val licensePlate: String?,
    val askingPrice: Double,
    val description: String?,
    val status: String?,
    val listingDate: String?,
    val sellerId: Int?,
    val imageUrl: String? = null
)

data class CarsResponse(
    val pageIndex: Int?,
    val pageSize: Int?,
    val items: List<CarResponse>
) 