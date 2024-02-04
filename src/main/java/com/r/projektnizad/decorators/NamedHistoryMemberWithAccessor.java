package com.r.projektnizad.decorators;

import com.r.projektnizad.models.change.ChangeAccessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NamedHistoryMemberWithAccessor {
  String value();
}