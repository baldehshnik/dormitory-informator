package com.firstapplication.dormapp.enums

enum class ConfirmedRegistration(_value: Int) {
    EMPTY(0),
    CONFIRMED(1),
    NOT_CONFIRMED(-1);

    val value = _value
}