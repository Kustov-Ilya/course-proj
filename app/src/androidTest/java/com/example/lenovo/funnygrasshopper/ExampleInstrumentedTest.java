package com.example.lenovo.funnygrasshopper;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertNotNull;


@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);
    private MainActivity mActivity = null;
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(KeyActivity.class.getName(), null, false),
                                    monitor2 = getInstrumentation().addMonitor(EncryptActivity.class.getName(), null, false),
                                    monitor3 = getInstrumentation().addMonitor(DecryptActivity.class.getName(), null, false),
                                    monitor4 = getInstrumentation().addMonitor(DecryptTextActivity.class.getName(), null, false);

    @Before
    public void setUp() throws Exception{
        mActivity = mActivityTestRule.getActivity();
    }

    private void check(int id, Instrumentation.ActivityMonitor mon){
        assertNotNull(mActivity.findViewById(id));
        onView(withId(id)).perform(click());
        Activity Act = getInstrumentation().waitForMonitorWithTimeout(mon, 5000);
        assertNotNull(Act);
        Act.finish();
    }
    @Test
    public void ActWork() throws Exception {
        check(R.id.key, monitor);
        check(R.id.encr, monitor2);
        check(R.id.decr, monitor3);
        check(R.id.decrText, monitor4);
    }



    @After
    public void tearDown() throws Exception{
        mActivity = null;
    }
}
