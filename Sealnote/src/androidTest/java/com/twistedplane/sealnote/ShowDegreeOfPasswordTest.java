package com.twistedplane.sealnote;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ShowDegreeOfPasswordTest {

    @Rule
    public ActivityTestRule<SealnoteActivity> mActivityTestRule = new ActivityTestRule<>(SealnoteActivity.class);

    @After
    public void tearDown(){
            try {
                // clearing app data
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear com.twistedplane.sealnote");

            } catch (Exception e) {
//                e.printStackTrace();
            }
    }
    @Test
    public void showDegreeOfPasswordTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction editText = onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText.perform(click());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText2.perform(replaceText("123"), closeSoftKeyboard());

        ViewInteraction textView = onView(withId(R.id.password_meter_text));
        textView.check(matches(withText("Weak")));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction editText3 =  onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText3.perform(replaceText("123456788"), closeSoftKeyboard());

        ViewInteraction textView2 = onView(withId(R.id.password_meter_text));
        textView2.check(matches(withText("So-so")));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction editText4 =  onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText4.perform(replaceText("1234567889abc"), closeSoftKeyboard());

        ViewInteraction textView3 = onView(withId(R.id.password_meter_text));
        textView3.check(matches(withText("Good")));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction editText5 =  onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText5.perform(replaceText("1234567889abcdefg1234@&%="), closeSoftKeyboard());

        ViewInteraction textView4 = onView(withId(R.id.password_meter_text));
        textView4.check(matches(withText("Strong")));

        ViewInteraction button = onView(
                allOf(withId(R.id.password_action_button), withText("Get Started"), isDisplayed()));
        button.perform(click());

        ViewInteraction imageView = onView(withId(R.id.layout_empty_grid));
        imageView.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
