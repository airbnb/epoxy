
package com.airbnb.epoxy;

class EpoxyProcessorException extends Exception {
  EpoxyProcessorException(String message) {
    super(message);
  }

  EpoxyProcessorException(Exception e, String message) {
    super(message, e);
    setStackTrace(e.getStackTrace());
  }

  @Override
  public String toString() {
    String string = "Epoxy Processor Exception: " + getMessage();

    Throwable cause = getCause();
    if (cause != null) {
      string += " Caused by " + cause.getClass().getSimpleName() + ": " + cause.getMessage();

      // We only include the stacktrace if there is an underlying bug (like a NPE), otherwise for
      // errors we purposely throw for misuse of Epoxy we don't need end users seeing the stacktrace
      StackTraceElement[] stackTrace = getStackTrace();
      boolean firstTraceElement = true;
      if (stackTrace.length > 0) {
        for (StackTraceElement stackTraceElement : stackTrace) {
          if (stackTraceElement.getClassName().contains("epoxy")
              && !stackTraceElement.getClassName().contains(ErrorLogger.class.getSimpleName())
              && !stackTraceElement.getClassName().contains("buildEpoxyException")) {

            // We can't include new line characters in the annotation error output (they cause
            // the text to be cut off), so we just use big spaces to separate sections:
            if (firstTraceElement) {
              string += "       Stacktrace:      ";
              firstTraceElement = false;
            } else {
              string += "       ";
            }

            string += stackTraceElement;
          }
        }
      }
    }

    return string;
  }
}
