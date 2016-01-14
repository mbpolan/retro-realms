package com.mbpolan.ws.config

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

/**
  * @author Mike Polan
  */
@Configuration
class MvcConfig extends WebMvcAutoConfigurationAdapter {

  override def configureMessageConverters(converters: util.List[HttpMessageConverter[_]]): Unit = {
    val mapper = new MappingJackson2HttpMessageConverter
    mapper.setObjectMapper(new ObjectMapper() {
      registerModule(DefaultScalaModule)
    })

    converters.add(mapper)
  }
}
