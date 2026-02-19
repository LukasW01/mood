package dev.wigger.mood.util

/**
 *  Password strength is getting interpreted as follows:
 *
 *  - Length: >= 8
 *  - Number
 *  - UPPERCASE
 *  - lowercase
 *  - Special character
 */
class Password {
    companion object {
        fun hasSufficientStrength(password: String): Boolean =
            password.length >= 8 &&
                    password.any { it.isDigit() } &&
                    password.any { it.isUpperCase() } &&
                    password.any { it.isLowerCase() } &&
                    password.any { "!@#$%^&*()_+-=[]{}|;:'\",.<>?".contains(it) }
    }
}
