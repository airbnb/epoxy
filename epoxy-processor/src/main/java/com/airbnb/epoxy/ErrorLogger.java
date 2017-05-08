package com.airbnb.epoxy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

import static com.airbnb.epoxy.Utils.buildEpoxyException;

class ErrorLogger {
  private final List<Exception> loggedExceptions = new ArrayList<>();

  void writeExceptions(Messager messager) {
    for (Exception loggedException : loggedExceptions) {
      messager.printMessage(Diagnostic.Kind.ERROR, loggedException.toString());
    }
  }

  /**
   * Errors are logged and saved until after classes are generating. Otherwise if we throw
   * immediately the models are not generated which leads to lots of other compiler errors which
   * mask the actual issues.
   */
  void logError(Exception e) {
    loggedExceptions.add(e);
  }

  void logError(Exception e, String message) {
    if (!(e instanceof EpoxyProcessorException)) {
      e = new EpoxyProcessorException(e, message + " : " + e);
    }
    loggedExceptions.add(e);
  }

  void logError(String msg, Object... args) {
    logError(buildEpoxyException(msg, args));
  }

  void logErrors(List<Exception> exceptions) {
    for (Exception exception : exceptions) {
      logError(exception);
    }
  }
}
