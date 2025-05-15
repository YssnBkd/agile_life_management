package com.example.agilelifemanagement.data.mappers

interface Mapper<S, T> {
    fun map(input: S): T
}
