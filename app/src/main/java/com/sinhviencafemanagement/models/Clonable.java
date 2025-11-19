package com.sinhviencafemanagement.models;

import androidx.annotation.NonNull;

public interface Clonable<T> {
    @NonNull
    T clone() throws CloneNotSupportedException;
}
