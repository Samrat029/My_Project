package com.db.awmd.challenge.exception;

public class AccountDoesNotExist extends RuntimeException {

  public AccountDoesNotExist(String message) {
    super(message);
  }
}
