package info.sayederfanarefin.location_sharing;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import info.sayederfanarefin.location_sharing.authentication.LogInEmail;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static junit.framework.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class LogInInstrumentedTest {
//    @Test
//    public void useAppContext() throws Exception {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getTargetContext();
//
//        assertEquals("info.sayederfanarefin.location_sharing", appContext.getPackageName());
//    }

    @Rule
    public ActivityTestRule<LogInEmail> mActivityRule = new ActivityTestRule(LogInEmail.class);
    private LogInEmail mainActivity;

    @Before
    public void setActivity() {
        mainActivity = mActivityRule.getActivity();
    }

    @Test
    public void clicklogin_waitforloginscreen() throws Exception {

        onView(withId(R.id.login_email)).perform(typeText("erfan.arefin@hotmail.com"),
                closeSoftKeyboard());

        onView(withId(R.id.login_pass)).perform(typeText("123456"),
                closeSoftKeyboard());

        // Click on the add note button
        onView(withId(R.id.button_login)).perform(click());

    }

}
