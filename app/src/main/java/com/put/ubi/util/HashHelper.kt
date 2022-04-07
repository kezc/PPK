package com.put.ubi.util

import java.security.MessageDigest

fun sha512(input: String): String {
    return MessageDigest.getInstance("SHA-512")
        .digest(input.toByteArray())
        .joinToString(separator = "") {
            ((it.toInt() and 0xff) + 0x100)
                .toString(16)
                .substring(1)
        }
}