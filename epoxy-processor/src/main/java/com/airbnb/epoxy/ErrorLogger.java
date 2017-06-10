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

  void logErrors(List<Exception> exceptions) {
    for (Exception exception : exceptions) {
      logError(exception);
    }
  }

  /**
   * Errors logged with this should describe exactly what is wrong. These won't show the stacktrace
   * in the error output to reduce confusion.
   */
  void logError(String msg, Object... args) {
    logError(buildEpoxyException(msg, args));
  }

  /**
   * Errors are logged and saved until after classes are generating. Otherwise if we throw
   * immediately the models are not generated which leads to lots of other compiler errors which
   * mask the actual issues.
   * <p>
   * If the exception is not an {@link EpoxyProcessorException} then the stacktrace will be shown in
   * the output.
   */
  void logError(Exception e) {
    logError(e, "");
  }

  void logError(Exception e, String message) {
    if (!(e instanceof EpoxyProcessorException)) {
      e = new EpoxyProcessorException(e, message);
    }
    logEpoxyError((EpoxyProcessorException) e);
  }

  private void logEpoxyError(EpoxyProcessorException e) {
    loggedExceptions.add(e);
  }
}
