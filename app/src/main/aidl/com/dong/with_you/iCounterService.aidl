// iCounterService.aidl
package com.dong.with_you;

// Declare any non-default types here with import statements

interface iCounterService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int getCount();

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

}
