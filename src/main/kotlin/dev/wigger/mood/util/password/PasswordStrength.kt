package dev.wigger.mood.util.password

import com.nulabinc.zxcvbn.Zxcvbn

/**
 *  Password strength is getting interpreted in Zxcvbn as follows:
 *
 * # Integer from 0-4 (useful for implementing a strength bar)
 * # 0 Weak        （guesses < 10^3 + 5）
 * # 1 Fair        （guesses < 10^6 + 5）
 * # 2 Good        （guesses < 10^8 + 5）
 * # 3 Strong      （guesses < 10^10 + 5）
 * # 4 Very strong （guesses >= 10^10 + 5）
 * strength.score
 */
class PasswordStrength {
    companion object {
        fun hasSufficientStrength(password: String): Boolean {
            return Zxcvbn().measure(password).score >= 3
        }
    }
}
