package com.ilieinc.dontsleep.ui.layout

import android.view.LayoutInflater
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.ui.tooling.preview.Preview
import com.ilieinc.dontsleep.R
import com.ilieinc.dontsleep.resource.TestComposeActivityTheme

@Composable
fun ServiceCard(name: String) {
    val context = ContextAmbient.current
    val customView = remember {
        LayoutInflater.from(context).inflate(R.layout.spinner_time_picker, null)
    }
    MaterialTheme {
        Card {
            ConstraintLayout {
                val (titleText, helpButton, getStartedButton, bodyLayout) = createRefs()
                Text(text = name,
                    modifier = Modifier.constrainAs(titleText) {
                        top.linkTo(helpButton.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(helpButton.bottom)
                    })
                Button(
                    onClick = {},
                    modifier = Modifier.constrainAs(helpButton) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }) {
                    Text(text = stringResource(R.string.help))
                }
//                Button(
//                    onClick = {},
//                    modifier = Modifier.constrainAs(getStartedButton) {
//                        top.linkTo(helpButton.bottom)
//                        end.linkTo(parent.end)
//                    }) {
//                    Text(text = Strings.get_started)
//                }
                ConstraintLayout(
                    Modifier.constrainAs(bodyLayout) {
                        top.linkTo(helpButton.bottom)
                    }) {
                    val (statusText, timeoutSwitch, timeoutText, miscDetailText, timeoutTimePicker) = createRefs()
                    Text(text = stringResource(R.string.status),
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
                    Text(text = stringResource(R.string.timeout),
                        modifier = Modifier.constrainAs(timeoutText) {
                            top.linkTo(miscDetailText.top)
                            start.linkTo(parent.start)
                            bottom.linkTo(timeoutTimePicker.top)
                        })
                    Text(text = stringResource(R.string.hours_minutes),
                        modifier = Modifier.constrainAs(miscDetailText) {
                            top.linkTo(timeoutText.bottom)
                            start.linkTo(parent.start)
                            bottom.linkTo(timeoutTimePicker.top)
                        })
//                    AndroidView(viewBlock = ::TimePicker,
//                        modifier = Modifier.constrainAs(timeoutTimePicker) {
//                            top.linkTo(timeoutSwitch.bottom)
//                            end.linkTo(parent.end)
//                        })
                    AndroidView({ customView }) { view ->

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestComposeActivityTheme {
        ServiceCard(stringResource(R.string.don_t_sleep_timer))
    }
}