package com.example.facetest.di2;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Benjamin on 15/05/16.
 */
@Scope
@Retention(RUNTIME) public @interface PerApp {}
