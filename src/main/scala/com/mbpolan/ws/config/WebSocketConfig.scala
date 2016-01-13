package com.mbpolan.ws.config

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.{AbstractWebSocketMessageBrokerConfigurer, EnableWebSocketMessageBroker, StompEndpointRegistry}

/**
  * @author Mike Polan
  */
@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

  override def configureMessageConverters(messageConverters: util.List[MessageConverter]): Boolean = {
    val result = super.configureMessageConverters(messageConverters)

    val mapper = new MappingJackson2HttpMessageConverter
    mapper.setObjectMapper(new ObjectMapper() {
      registerModule(DefaultScalaModule)
    })

    result
  }

  override def configureMessageBroker(registry: MessageBrokerRegistry): Unit = {
    registry.enableSimpleBroker("/topic")
    registry.setApplicationDestinationPrefixes("/api")
  }

  override def registerStompEndpoints(registry: StompEndpointRegistry): Unit = {
    registry.addEndpoint("/topic").withSockJS()
  }
}
