package de.reiss.thepokerguys.testutil;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import de.reiss.thepokerguys.TestApp;

public class AppTestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, TestApp.class.getCanonicalName(), context);
    }

}
