package com.ilieinc.dontsleep

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilieinc.core.ui.theme.AppTheme
import com.ilieinc.dontsleep.ui.MainScreen
import com.ilieinc.dontsleep.ui.component.ActionCard
import com.ilieinc.dontsleep.ui.model.CardUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExpressiveUITests {

    // Use createAndroidComposeRule for tests that need an Activity context, like MainScreen
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun actionCard_displaysTitleAndStatusSwitch() {
        val cardTitle = "Test Action Card"
        val statusText = "Status" // Assuming this is the actual string resource, or use R.string.status

        val initialState = CardUiState(
            title = cardTitle,
            isLoading = false,
            permissionRequired = false,
            // ... other state properties if needed
        )

        composeTestRule.setContent {
            AppTheme {
                ActionCard(state = initialState, onEvent = {})
            }
        }

        // Verify title is displayed
        composeTestRule.onNodeWithText(cardTitle).assertIsDisplayed()

        // Verify "Status" switch text is displayed
        // Note: If "Status" comes from a string resource, it's better to load it
        // For simplicity here, using the literal string.
        composeTestRule.onNodeWithText(statusText, useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun mainScreen_displaysCloseButton() {
        val closeButtonText = "Close" // Assuming this is the actual string

        composeTestRule.setContent {
            // Minimal setup for MainScreen, assuming it can render with defaults
            // or mocked view models if necessary for more complex scenarios.
            // For this test, we only need to ensure it sets its content.
            AppTheme {
                MainScreen(
                    hasNotificationPermission = true, // Assume permission granted
                    notificationPermissionResult = composeTestRule.activity.registerForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) {}
                )
            }
        }

        // Verify "Close" button is displayed
        composeTestRule.onNodeWithText(closeButtonText).assertIsDisplayed()

        // Optionally, verify one of the cards to check padding (visual inspection is more reliable for padding)
        // This requires knowing the title of one of the cards.
        // Example: If WakeLockTimerCard has a default or known title:
        // composeTestRule.onNodeWithText("Wake Lock Timer").assertIsDisplayed() // Replace with actual title
    }
}
