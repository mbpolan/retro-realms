package com.mbpolan.ws

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

/** Configuration for the web application.
  *
  * @author Mike Polan
  */
object Application extends App {
  SpringApplication.run(classOf[WebAppConfig], args: _*)
}

@SpringBootApplication
@EnableScheduling
class WebAppConfig