
package com.airbnb.epoxy;

class EpoxyProcessorException extends Exception {
  EpoxyProcessorException(String message) {
    super(message);
  }

  EpoxyProcessorException(Exception e, String message) {
    super(message, e);
  }
}
