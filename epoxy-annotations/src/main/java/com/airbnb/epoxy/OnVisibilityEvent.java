package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be used to annotate methods inside classes with a {@link ModelView}
 * annotation. Methods with this annotation will be called when the viewport is
 * changed.
 *
 * Inspired from Litho : https://fblitho.com/docs/visibility-handling
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface OnVisibilityEvent {
  enum Event {
    /**
     * Event triggered when a Component enters the Visible Range. This happens
     * when at least a pixel of the Component is visible.
     */
    Visible,
    /**
     * Event triggered when a Component becomes invisible. This is the same with
     * exiting the Visible Range, the Focused Range and the Full Impression
     * Range. All the code that needs to be executed when a component leaves
     * any of these ranges should be written in the handler for this event.
     */
    Invisible,
    /**
     * Event triggered when a Component enters the Focused Range. This happens
     * when either the Component occupies at least half of the viewport or,
     * if the Component is smaller than half of the viewport, when the it is
     * fully visible.
     */
    FocusedVisible,
    /**
     * Event triggered when a Component exits the Focused Range. The Focused
     * Range is defined as at least half of the viewport or, if the Component is
     * smaller than half of the viewport, when the it is fully visible.
     */
    UnfocusedVisible,
    /**
     * Event triggered when a Component enters the Full Impression Range. This
     * happens, for instance in the case of a vertical RecyclerView, when both
     * the top and bottom edges of the component become visible.
     */
    FullImpressionVisible,
    /**
     * Event triggered when the visible rect of a Component changes.
     *
     * VisibilityChangedEvents should be used with particular care since they
     * will be dispatched on every frame while scrolling. No heavy work should
     * be done inside the VisibilityChangedEvents handlers. Visible, Invisible,
     * Focused, Unfocused and Full Impression events are the recommended over
     * VisibilityChanged events whenever possible.
     */
    Changed,
  }

  Event value() default Event.Changed;
}
