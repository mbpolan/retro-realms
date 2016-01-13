package com.mbpolan.ws

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
  * Created by Mike on 2016-01-04.
  */
object Application extends App {
  SpringApplication.run(classOf[WebAppConfig], args: _*)
}

@SpringBootApplication
class WebAppConfig