package controllers.greenfossil.jumpstart.day1

import com.greenfossil.data.mapping.Mapping
import com.greenfossil.data.mapping.Mapping.*
import com.greenfossil.thorium.{*, given}
import com.linecorp.armeria.server.annotation.{Default, Get, Param, Post}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object JumpStartDay1Controller:

  /*
   * Implement this method to returns:
   * - "Hi, {name}!" if name starts with a vowel
   * - "Hey, {name}!" if name starts with consonant
   * - "Hello stranger!" if name is empty
   */
  @Get("/greetMe")
  def greetMe(@Param @Default("") name: String) = {
    if name.trim.isEmpty then "Hello stranger!"
    else {
      val firstChar = name.charAt(0)
      val vowels = List('a','e','i','o','u', 'A', 'E', 'I','O',  'U')
      if vowels.contains(firstChar) then s"Hi, ${name}!"
      else
        s"Hey, ${name}!"
    }
  }


  /*
   * Implement this method to bind to the following fields:
   * 1. firstname : String, mandatory
   * 2. lastname: String, optional
   * 3. dob: java.time.LocalDate with format (dd/MM/yyyy)
   */
  private def signupMapping: Mapping[(String, Option[String], LocalDate)] = {
    tuple(
      "firstname" -> nonEmptyText,
      "lastname" -> optional(text),
      "dob" -> localDateUsing("dd/MM/yyyy")
    )
  }

  /*
   * Implement this method to bind the HTTP request's body to `signupMapping`.
   * If the data mapping has validation errors, return a BadRequest with text "Invalid Data".
   * If has no validation error, return an OK with text
   *    "Welcome {firstname} {lastname}! You were born on {dob<dd/MM/yyy>}."
   */
  @Post("/signup")
  def signup(using request: Request) = {
    signupMapping.bindFromRequest().fold(
      error =>
        BadRequest("Invalid Data"),
      (firstname, lastname, dob) => {
        val dobFormat = dob.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        lastname match
          case None => Ok(s"Welcome $firstname! You were born on ${dobFormat}.")
          case Some(value) => Ok(s"Welcome $firstname $value! You were born on ${dobFormat}.")
      })
  }
