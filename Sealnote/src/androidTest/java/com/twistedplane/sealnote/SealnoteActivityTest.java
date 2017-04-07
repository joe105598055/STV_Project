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
public class SealnoteActivityTest {

    @Rule
    public ActivityTestRule<SealnoteActivity> mActivityTestRule = new ActivityTestRule<>(SealnoteActivity.class);

    @Test
    public void sealnoteActivityTest() {
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
        editText2.perform(click());

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText3.perform(click());

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText4.perform(click());

        ViewInteraction editText5 = onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText5.perform(click());

        ViewInteraction editText6 = onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText6.perform(click());

        ViewInteraction editText7 = onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText7.perform(click());

        ViewInteraction editText8 = onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText8.perform(click());

        ViewInteraction editText9 = onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText9.perform(click());

        ViewInteraction editText10 = onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText10.perform(click());

        ViewInteraction editText11 = onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText11.perform(click());

        ViewInteraction editText12 = onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText12.perform(click());

        ViewInteraction editText13 = onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText13.perform(click());

        ViewInteraction editText14 = onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText14.perform(click());

        ViewInteraction editText15 = onView(
                allOf(withId(R.id.password_meter_input),
                        withParent(withId(R.id.password_input)),
                        isDisplayed()));
        editText15.perform(replaceText("123"), closeSoftKeyboard());

        ViewInteraction button = onView(
                allOf(withId(R.id.password_action_button), withText("Get Started"), isDisplayed()));
        button.perform(click());

        ViewInteraction imageView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.layout_empty_grid),
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        1)),
                        0),
                        isDisplayed()));
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
