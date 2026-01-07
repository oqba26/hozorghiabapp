@file:Suppress("AssignedValueIsNeverRead")

package com.oqba26.hozorghiabapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oqba26.hozorghiabapp.HozorGhiabApplication
import com.oqba26.hozorghiabapp.R
import com.oqba26.hozorghiabapp.AppSettings
import com.oqba26.hozorghiabapp.ui.theme.HozorGhiabAppTheme
import com.oqba26.hozorghiabapp.viewmodel.SettingsViewModel
import com.oqba26.hozorghiabapp.viewmodel.StudentsViewModel
import com.oqba26.hozorghiabapp.viewmodel.AttendanceViewModel
import com.oqba26.hozorghiabapp.viewmodel.FinancialViewModel
import com.oqba26.hozorghiabapp.viewmodel.StudentFinancialDetailsViewModel
import com.oqba26.hozorghiabapp.viewmodel.StudentAttendanceDetailsViewModel
import com.oqba26.hozorghiabapp.viewmodel.StudentsViewModelFactory
import com.oqba26.hozorghiabapp.viewmodel.AttendanceViewModelFactory
import com.oqba26.hozorghiabapp.viewmodel.FinancialViewModelFactory
import com.oqba26.hozorghiabapp.viewmodel.StudentFinancialDetailsViewModelFactory
import com.oqba26.hozorghiabapp.viewmodel.StudentAttendanceDetailsViewModelFactory

import androidx.compose.foundation.ComposeFoundationFlags

sealed interface NavDestination {
    data object Main : NavDestination
    data class FinancialDetails(val studentId: Long) : NavDestination
    data class AttendanceDetails(val studentId: Long) : NavDestination
}

class MainActivity : ComponentActivity() {

    @OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComposeFoundationFlags.isNonComposedClickableEnabled = false
        enableEdgeToEdge()
        setContent {
            val application = applicationContext as HozorGhiabApplication
            val repository = application.repository

            val settingsViewModel: SettingsViewModel by viewModels()
            val settings by settingsViewModel.settingsFlow.collectAsState(initial = AppSettings())

            val studentsViewModel: StudentsViewModel by viewModels {
                StudentsViewModelFactory(repository)
            }
            val attendanceViewModel: AttendanceViewModel by viewModels {
                AttendanceViewModelFactory(repository)
            }
            val financialViewModel: FinancialViewModel by viewModels {
                FinancialViewModelFactory(repository)
            }

            @Suppress("UNUSED_VALUE")
            var navDestination: NavDestination by remember { mutableStateOf(NavDestination.Main) }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                HozorGhiabAppTheme(fontKey = settings.fontKey) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (val dest = navDestination) {
                            is NavDestination.Main -> {
                                MainScreen(
                                    studentsViewModel = studentsViewModel,
                                    settingsViewModel = settingsViewModel,
                                    attendanceViewModel = attendanceViewModel,
                                    financialViewModel = financialViewModel,
                                    onNavigateToFinancialDetails = { navDestination = NavDestination.FinancialDetails(it) },
                                    onNavigateToAttendanceDetails = { navDestination = NavDestination.AttendanceDetails(it) }
                                )
                            }
                            is NavDestination.FinancialDetails -> {
                                val studentFinancialDetailsViewModel: StudentFinancialDetailsViewModel = viewModel(
                                    key = "fin_${dest.studentId}",
                                    factory = StudentFinancialDetailsViewModelFactory(dest.studentId, repository)
                                )
                                StudentFinancialDetailsScreen(viewModel = studentFinancialDetailsViewModel)
                                BackHandler {
                                    navDestination = NavDestination.Main
                                }
                            }
                            is NavDestination.AttendanceDetails -> {
                                val studentAttendanceDetailsViewModel: StudentAttendanceDetailsViewModel = viewModel(
                                    key = "att_${dest.studentId}",
                                    factory = StudentAttendanceDetailsViewModelFactory(dest.studentId, repository)
                                )
                                StudentAttendanceDetailsScreen(viewModel = studentAttendanceDetailsViewModel)
                                BackHandler {
                                    navDestination = NavDestination.Main
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    studentsViewModel: StudentsViewModel,
    settingsViewModel: SettingsViewModel,
    attendanceViewModel: AttendanceViewModel,
    financialViewModel: FinancialViewModel,
    onNavigateToFinancialDetails: (Long) -> Unit,
    onNavigateToAttendanceDetails: (Long) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        stringResource(R.string.tab_students),
        stringResource(R.string.tab_attendance),
        stringResource(R.string.tab_financial),
        stringResource(R.string.tab_settings)
    )

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                )
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Crossfade(targetState = selectedTabIndex, label = "TabTransition") { index ->
                when (index) {
                    0 -> StudentsScreen(studentsViewModel)
                    1 -> AttendanceScreen(attendanceViewModel, onNavigateToAttendanceDetails)
                    2 -> FinancialScreen(financialViewModel, onNavigateToFinancialDetails)
                    3 -> SettingsScreen(settingsViewModel)
                }
            }
        }
    }
}
