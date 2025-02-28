package com.whidy.whidyandroid.model

enum class PlaceType(val displayName: String) {
    STUDY_CAFE("스터디 카페"),
    GENERAL_CAFE("개인 카페"),
    FREE_STUDY_SPACE("무료 공부 공간"),
    FREE_PICTURE("면접사진"),
    FREE_CLOTHES_RENTAL("정장 대여"),
    FRANCHISE_CAFE("프랜차이즈 카페");

    companion object {
        fun fromString(value: String): String {
            return entries.find { it.name == value }?.displayName ?: value
        }
    }
}
