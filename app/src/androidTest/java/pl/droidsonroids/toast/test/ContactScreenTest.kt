package pl.droidsonroids.toast.test

import android.support.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import pl.droidsonroids.toast.R
import pl.droidsonroids.toast.app.home.MainActivity
import pl.droidsonroids.toast.function.getString
import pl.droidsonroids.toast.robot.ContactRobot


class ContactScreenTest {
    @JvmField
    @Rule
    val activityRule = ActivityTestRule(MainActivity::class.java, true, true)

    private fun goToContactScreen() {
        with(ContactRobot()) {
            performClickOnElementWithId(R.id.actionContact)
        }
    }

    @Test
    fun isToolbarDisplayed() {
        val toolbarTitle = getString(R.string.contact_title)
        goToContactScreen()
        with(ContactRobot()) {
            checkIfToolbarWithTitleIsDisplayed(toolbarTitle, R.id.toolbar)
        }
    }

    @Test
    fun isEveryFieldInContactFormDisplayed() {
        goToContactScreen()
        with(ContactRobot()) {
            checkIfElementWithIdIsDisplayed(R.id.topicSpinner)
            checkIfElementWithIdIsDisplayed(R.id.contactNameInputLayout)
            checkIfHintOnTextInputLayoutIsCorrect(R.id.contactNameInputLayout, getString(R.string.your_name))
            checkIfElementWithIdIsDisplayed(R.id.contactEmailInputLayout)
            checkIfHintOnTextInputLayoutIsCorrect(R.id.contactEmailInputLayout, getString(R.string.email_address))
            checkIfElementWithIdIsDisplayed(R.id.contactMessageInputLayout)
            checkIfHintOnEditTextIsCorrect(R.id.contactMessageEditText, getString(R.string.your_message))
        }
    }

    @Test
    fun isSendButtonDisplayedInDefaultState() {
        goToContactScreen()
        with(ContactRobot()) {
            checkIfElementWithIdIsDisplayed(R.id.disabledSendButton)
            checkIfElementWithIdIsNotDisplayed(R.id.enabledSendButton)
        }
    }
}