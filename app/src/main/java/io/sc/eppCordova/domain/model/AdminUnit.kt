package io.sc.eppCordova.domain.model

data class AdminUnit(
    val code: String,
    val name: String,
    val nameEn: String = "",
    val type: AdminUnitType
)

enum class AdminUnitType {
    DIVISION, DISTRICT, TALUKA, VILLAGE
}
