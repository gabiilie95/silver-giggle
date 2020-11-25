package com.ilieinc.dontsleep.ui.layout

import android.view.LayoutInflater
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.ui.tooling.preview.Preview
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.resource.TestComposeActivityTheme
import com.ilieinc.dontsleep.resource.typography
import java.util.*

@Composable
fun ServiceCard(name: String, bodyVisible: Boolean, timeout) {
    val context = ContextAmbient.current
    val customView = remember {
        LayoutInflater.from(context).inflate(R.layout.spinner_time_picker, null)
    }
    MaterialTheme {
        Card(
            elevation = 4.dp,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            ConstraintLayout(
                modifier = Modifier.padding(10.dp)
            ) {
                val (titleText, helpButton, getStartedButton, bodyLayout) = createRefs()
                Text(text = name,
                    style = typography.h6,
                    modifier = Modifier.constrainAs(titleText) {
                        top.linkTo(helpButton.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(helpButton.bottom)
                    })
                TextButton(
                    onClick = {},
                    modifier = Modifier.constrainAs(helpButton) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }) {
                    Text(text = stringResource(R.string.help).toUpperCase(Locale.getDefault()))
                }
                if (!bodyVisible) {
                    Button(
                        onClick = {},
                        modifier = Modifier.constrainAs(getStartedButton) {
                            top.linkTo(helpButton.bottom)
                            end.linkTo(parent.end)
                        }) {
                        Text(text = stringResource(id = R.string.get_started))
                    }
                } else {
                    ConstraintLayout(
                        Modifier.constrainAs(bodyLayout) {
                            top.linkTo(helpButton.bottom)
                        }) {
                        val (statusText, timeoutSwitch, timeoutText, timeoutTimePicker) = createRefs()
                        Text(text = stringResource(R.string.status),
                            style = TextStyle(fontWeight = FontWeight.Bold),
                            modifier = Modifier.constrainAs(statusText) {
                                top.linkTo(timeoutSwitch.top)
                                start.linkTo(parent.start)
                                bottom.linkTo(timeoutSwitch.bottom)
                            })
                        Switch(checked = false, onCheckedChange = {

                        },
                            modifier = Modifier.constrainAs(timeoutSwitch) {
                                top.linkTo(parent.top)
                                end.linkTo(parent.end)
                            })
                        Column(modifier = Modifier.constrainAs(timeoutText) {
                            top.linkTo(timeoutSwitch.bottom)
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                        }) {
                            Text(
                                text = stringResource(R.string.timeout),
                                style = TextStyle(fontWeight = FontWeight.Bold)
                            )
                            Text(text = stringResource(R.string.hours_minutes))
                        }
                        AndroidView({ customView },
                            modifier = Modifier.constrainAs(timeoutTimePicker) {
                                top.linkTo(timeoutSwitch.bottom)
                                start.linkTo(timeoutText.end)
                                end.linkTo(parent.end)
                            }) { view ->

                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Box(modifier = Modifier.preferredWidth(450.dp)) {
        TestComposeActivityTheme {
            ServiceCard(stringResource(R.string.don_t_sleep_timer), true)
        }
    }
}