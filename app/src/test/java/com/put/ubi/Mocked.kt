package com.put.ubi

import java.io.File

class Mocked {
    companion object {
        fun getBankier(): String {
            return ClassLoader.getSystemResource("bankier.html").readText()
        }
    }
}