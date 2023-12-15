package com.arsahub.backend.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "custom_unit_type")
class CustomUnitType(
    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    var name: String?,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_unit_type_id", nullable = false)
    var id: Long? = null
}

//sealed class UnitType(val name: String) {
//    data object Integer : UnitType("Integer")
//    data object IntegerArray : UnitType("Integer Array")

//    companion object {
//        fun fromString(value: String): CustomUnitType {
//            return when (value.lowercase()) {
//                "integer" -> Integer
//                "integer array" -> IntegerArray
//                else -> throw IllegalArgumentException("Unknown UnitType: $value")
//            }
//        }
//
//        fun toString(type: CustomUnitType): String = type.name
//    }
//}

//@Converter(autoApply = true)
//class UnitTypeConverter : AttributeConverter<CustomUnitType, String> {
//
//    override fun convertToDatabaseColumn(attribute: CustomUnitType?): String? =
//        attribute?.let(CustomUnitType::toString)
//
//    override fun convertToEntityAttribute(dbData: String?): CustomUnitType? =
//        dbData?.let(CustomUnitType::fromString)
//}