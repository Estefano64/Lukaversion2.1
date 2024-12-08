package com.puyodev.luka.screens.sign_up

//Clase de estado - Representa y almacena el estado de UI pantalla
data class SignUpUiState(
  val email: String = "Prueba12345@gmail.com",
  val password: String = "PruebaLuka123",
  val repeatPassword: String = "PruebaLuka123",
  val username: String = "TestLab"
)
