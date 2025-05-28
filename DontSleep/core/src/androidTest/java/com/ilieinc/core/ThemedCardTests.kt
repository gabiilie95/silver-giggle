package com.ilieinc.core

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilieinc.core.ui.components.ThemedCard
import com.ilieinc.core.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ThemedCardTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun themedCard_displaysTitleAndBody() {
        val titleText = "Test Card Title"
        val bodyText = "This is the body content of the card."

        composeTestRule.setContent {
            AppTheme { // Assuming AppTheme is available and sets up MaterialTheme
                ThemedCard(title = titleText) {
                    Text(bodyText)
                }
            }
        }

        // Verify title is displayed
        composeTestRule.onNodeWithText(titleText).assertIsDisplayed()

        // Verify body content is displayed
        composeTestRule.onNodeWithText(bodyText).assertIsDisplayed()
    }
}
