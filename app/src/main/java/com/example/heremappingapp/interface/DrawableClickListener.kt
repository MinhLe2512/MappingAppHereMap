package com.example.heremappingapp.`interface`

interface DrawableClickListener {
    enum class DrawablePosition {
        TOP, BOTTOM, LEFT, RIGHT
    }

    fun onClick(target: DrawablePosition?)
}