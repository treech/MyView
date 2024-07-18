package com.example.myview.ui.entity

data class Point(val centerX: Int, val centerY: Int, val index: Int) {

    var state: State = State.NORMAL

    enum class State {
        NORMAL, SELECT, ERROR
    }

    fun setStatusPressed() {
        state = State.SELECT
    }

    fun setStatusNormal() {
        state = State.NORMAL
    }

    fun setStatusError() {
        state = State.ERROR
    }
}
