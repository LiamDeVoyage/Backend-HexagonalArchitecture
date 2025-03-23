package com.jaeseung.coffeedelivery.application.port.out;

public class OrderNotFound extends RuntimeException {
  public OrderNotFound(String message) {
    super(message);
  }
}
