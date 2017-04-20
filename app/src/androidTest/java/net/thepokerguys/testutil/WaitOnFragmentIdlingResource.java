package net.thepokerguys.testutil;

import android.support.test.espresso.IdlingResource;
import android.support.v4.app.FragmentManager;

public class WaitOnFragmentIdlingResource implements IdlingResource {

    private final FragmentManager manager;
    private final String tag;
    private ResourceCallback callback;

    public WaitOnFragmentIdlingResource(FragmentManager manager, String tag) {
        this.manager = manager;
        this.tag = tag;
    }

    @Override
    public String getName() {
        return "fragment with tag " + tag;
    }

    @Override
    public boolean isIdleNow() {
        boolean idle = (manager.findFragmentByTag(tag) != null);
        if (idle) {
            callback.onTransitionToIdle();
        }
        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }

}

