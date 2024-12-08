
package com.puyodev.luka.common.ext

import android.util.Patterns
import java.util.regex.Pattern

private const val MAX_USERNAME_LENGTH = 35
private const val MIN_USERNAME_LENGTH = 5
private const val USERNAME_PATTERN = "^[a-zA-Z ]+$"

private const val MIN_PASS_LENGTH = 6
private const val PASS_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"

fun String.isValidUsername(): Boolean {
  return this.isNotBlank() &&
          this.length <= MAX_USERNAME_LENGTH && this.length >= MIN_USERNAME_LENGTH &&
          Pattern.compile(USERNAME_PATTERN).matcher(this).matches()
}

fun String.isValidEmail(): Boolean {
  return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
  return this.isNotBlank() &&
    this.length >= MIN_PASS_LENGTH &&
    Pattern.compile(PASS_PATTERN).matcher(this).matches()
}

fun String.passwordMatches(repeated: String): Boolean {
  return this == repeated
}

fun String.idFromParameter(): String {
    return this // No modificamos nada
//return this.substring(1, this.length - 1)
}
