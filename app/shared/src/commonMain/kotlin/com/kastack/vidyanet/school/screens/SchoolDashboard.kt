package com.kastack.vidyanet.school.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kastack.vidyanet.commonUi.components.AppText
import com.kastack.vidyanet.school.components.StatusBadge
import com.kastack.vidyanet.school.viewModels.SchoolDashboardViewModel
import com.kastack.vidyanet.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SchoolDashboard(
    schoolId: String,
    viewModel: SchoolDashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(schoolId) {
        viewModel.loadDashboardData(schoolId)
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Top Welcome Section
            WelcomeHeader(
                schoolName = uiState.school?.schoolName ?: "School Admin"
            )

            // Statistics Cards (First Row)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                maxItemsInEachRow = 6
            ) {
                val itemModifier = Modifier.widthIn(min = 160.dp).weight(1f)
                StatCard("Total Students", uiState.studentCount, Icons.Default.Group, "+2.4%", AcademicSuccess, itemModifier)
                StatCard("Total Teachers", uiState.teacherCount, Icons.Default.School, null, null, itemModifier)
                StatCard("Total Staff", uiState.staffCount, Icons.Default.Badge, null, null, itemModifier)
                StatCard("Parents", uiState.parentCount, Icons.Default.FamilyRestroom, null, null, itemModifier)
                StatCard("Pending Admissions", uiState.pendingAdmissions, Icons.Default.HowToReg, null, null, itemModifier)
                StatCard("Active Classes", uiState.activeClasses, Icons.Default.Class, null, null, itemModifier)
            }

            // Second Row - Attendance & Fees
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                maxItemsInEachRow = 4
            ) {
                val itemModifier = Modifier.widthIn(min = 240.dp).weight(1f)
                AttendanceStatCard("Student Attendance", uiState.studentAttendance, "High Stability", MaterialTheme.colorScheme.primary, itemModifier)
                AttendanceStatCard("Teacher Attendance", uiState.teacherAttendance, "Near Optimal", AcademicSuccess, itemModifier)
                FinanceStatCard("Today's Fee Collection", uiState.todaysFee, Icons.Default.AccountBalanceWallet, MaterialTheme.colorScheme.primary, itemModifier)
                FinanceStatCard("Pending Fee Amount", uiState.pendingFee, Icons.Default.PendingActions, AcademicError, itemModifier)
            }

            // Charts Grid & Main Content
            BoxWithConstraints {
                if (maxWidth > 1100.dp) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Column(modifier = Modifier.weight(0.72f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            // Charts Grid
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                ChartCard("Monthly Admissions", Modifier.weight(1.5f)) { BarChartMock() }
                                ChartCard("Gender Distribution", Modifier.weight(1f)) { DonutChartMock() }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                ChartCard("Student Attendance Trend", Modifier.weight(1f)) { LineChartMock() }
                                ChartCard("Fee Collection", Modifier.weight(1f)) { AreaChartMock() }
                            }
                            
                            // Quick Actions
                            QuickActionsSection()

                            // Recent Tables
                            RecentTablesSection()
                        }

                        // Right Sidebar Widgets
                        Column(modifier = Modifier.weight(0.28f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            ActivityFeedSection()
                            
                            WidgetCard("Quick Info") {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    InfoItem("Upcoming Events", "Annual Sports Day", "Oct 30, 2023", Icons.Default.Event, MaterialTheme.colorScheme.primary)
                                    InfoItem("Birthdays Today", "3 Students, 1 Staff", "Tap to view", Icons.Default.Cake, AcademicSuccess)
                                    InfoItem("Pending Approvals", "12 Documents", "Requires review",
                                        Icons.AutoMirrored.Filled.FactCheck, AcademicWarning)
                                    InfoItem("Leave Requests", "2 Teachers", "Urgent", Icons.Default.HourglassEmpty, AcademicError)
                                    InfoItem("Upcoming Exams", "Mid-Term Session", "Starts Nov 5", Icons.Default.Quiz, MaterialTheme.colorScheme.tertiary)
                                    InfoItem("Recent Notifications", "Fee reminder sent", "1 hour ago", Icons.Default.NotificationsActive, MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        // Stack Charts
                        ChartCard("Monthly Admissions", Modifier.fillMaxWidth()) { BarChartMock() }
                        ChartCard("Gender Distribution", Modifier.fillMaxWidth()) { DonutChartMock() }
                        ChartCard("Student Attendance Trend", Modifier.fillMaxWidth()) { LineChartMock() }
                        ChartCard("Fee Collection", Modifier.fillMaxWidth()) { AreaChartMock() }
                        
                        // Quick Actions
                        QuickActionsSectionAdaptive()

                        // Recent Tables
                        RecentTablesSection()

                        // Feed
                        ActivityFeedSection()
                        
                        // Quick Info
                        WidgetCard("Quick Info") {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                InfoItem("Upcoming Events", "Annual Sports Day", "Oct 30, 2023", Icons.Default.Event, MaterialTheme.colorScheme.primary)
                                InfoItem("Birthdays Today", "3 Students, 1 Staff", "Tap to view", Icons.Default.Cake, AcademicSuccess)
                                InfoItem("Pending Approvals", "12 Documents", "Requires review",
                                    Icons.AutoMirrored.Filled.FactCheck, AcademicWarning)
                                InfoItem("Leave Requests", "2 Teachers", "Urgent", Icons.Default.HourglassEmpty, AcademicError)
                                InfoItem("Upcoming Exams", "Mid-Term Session", "Starts Nov 5", Icons.Default.Quiz, MaterialTheme.colorScheme.tertiary)
                                InfoItem("Recent Notifications", "Fee reminder sent", "1 hour ago", Icons.Default.NotificationsActive, MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsSectionAdaptive() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AppText("Quick Actions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            maxItemsInEachRow = 3
        ) {
            val itemModifier = Modifier.widthIn(min = 120.dp).weight(1f)
            QuickActionBtn("Add Student", Icons.Default.PersonAdd, itemModifier)
            QuickActionBtn("Add Teacher", Icons.Default.PersonAddAlt, itemModifier)
            QuickActionBtn("Create Class", Icons.Default.GroupAdd, itemModifier)
            QuickActionBtn("Record Fee", Icons.AutoMirrored.Filled.ReceiptLong, itemModifier)
            QuickActionBtn("Send Announcement", Icons.Default.Campaign, itemModifier)
            QuickActionBtn("Create Timetable", Icons.AutoMirrored.Filled.EventNote, itemModifier)
        }
    }
}

@Composable
private fun WelcomeHeader(schoolName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            AppText(
                "Welcome back, $schoolName",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            AppText(
                "Today is Monday, Oct 23, 2023",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Surface(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Column {
                    AppText("TERM", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                    AppText("Mid-Semester 1", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: ImageVector, trend: String?, trendColor: Color?, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primary.copy(0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                trend?.let { AppText(it, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = trendColor ?: Color.Unspecified) }
            }
            Spacer(modifier = Modifier.height(12.dp))
            AppText(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            AppText(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AttendanceStatCard(title: String, percentage: String, status: String, color: Color, modifier: Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.size(48.dp).border(4.dp, color, CircleShape), contentAlignment = Alignment.Center) {
                AppText(percentage, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
            Column {
                AppText(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                AppText(status, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun FinanceStatCard(title: String, amount: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.size(48.dp).background(color.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Column {
                AppText(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                AppText(amount, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ChartCard(title: String, modifier: Modifier, content: @Composable () -> Unit) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(modifier = Modifier.padding(20.dp)) {
            AppText(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { content() }
        }
    }
}

@Composable
private fun QuickActionsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AppText("Quick Actions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionBtn("Add Student", Icons.Default.PersonAdd, Modifier.weight(1f))
            QuickActionBtn("Add Teacher", Icons.Default.PersonAddAlt, Modifier.weight(1f))
            QuickActionBtn("Create Class", Icons.Default.GroupAdd, Modifier.weight(1f))
            QuickActionBtn("Record Fee", Icons.AutoMirrored.Filled.ReceiptLong, Modifier.weight(1f))
            QuickActionBtn("Send Announcement", Icons.Default.Campaign, Modifier.weight(1f))
            QuickActionBtn("Create Timetable", Icons.AutoMirrored.Filled.EventNote, Modifier.weight(1f))
        }
    }
}

@Composable
private fun QuickActionBtn(label: String, icon: ImageVector, modifier: Modifier) {
    Surface(onClick = {}, modifier = modifier, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), color = MaterialTheme.colorScheme.surfaceContainerLowest) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            AppText(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun RecentTablesSection() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        TableCard("Recent Admissions") {
            AdmissionRow("John Doe", "Grade 10-A", "Oct 22, 2023", "Approved", AcademicSuccess)
            AdmissionRow("Alice Smith", "Grade 8-B", "Oct 21, 2023", "Pending", AcademicWarning)
        }
        TableCard("Latest Fee Payments") {
            FeePaymentRow("Mark Johnson", "Grade 5-C", "₹4,500", "Success", AcademicSuccess)
            FeePaymentRow("Sarah Williams", "Grade 12-A", "₹12,000", "Pending", AcademicWarning)
        }
        TableCard("Recent Staff Activities") {
            StaffActivityRow("Teacher Sarah Parker", "Attendance submitted", "10:30 AM")
            StaffActivityRow("Admin Mark Lewis", "Inventory updated", "09:45 AM")
        }
    }
}

@Composable
private fun TableCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column {
            Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                AppText(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton(onClick = {}) { AppText("View All") }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            content()
        }
    }
}

@Composable
private fun AdmissionRow(name: String, grade: String, date: String, status: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.secondaryContainer, CircleShape), contentAlignment = Alignment.Center) { AppText(name.take(1)) }
            AppText(name, fontWeight = FontWeight.Medium)
        }
        AppText(grade, modifier = Modifier.weight(0.8f))
        AppText(date, modifier = Modifier.weight(0.8f), color = MaterialTheme.colorScheme.outline)
        StatusBadge(text = status, color = color)
    }
}

@Composable
private fun FeePaymentRow(name: String, grade: String, amount: String, status: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), contentAlignment = Alignment.Center) { AppText(name.take(1)) }
            AppText(name, fontWeight = FontWeight.Medium)
        }
        AppText(grade, modifier = Modifier.weight(0.8f))
        AppText(amount, modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
        StatusBadge(text = status, color = color)
    }
}

@Composable
private fun StaffActivityRow(staff: String, activity: String, time: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp)) }
            AppText(staff, fontWeight = FontWeight.Medium)
        }
        AppText(activity, modifier = Modifier.weight(1.2f))
        AppText(time, modifier = Modifier.weight(0.5f), color = MaterialTheme.colorScheme.outline, textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}

@Composable
private fun ActivityFeedSection() {
    WidgetCard("Recent Activity") {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            ActivityItem("Student John Doe admitted", "2 hours ago", Icons.Default.Person, MaterialTheme.colorScheme.primaryContainer)
            ActivityItem("Fee received from Alice Smith", "4 hours ago", Icons.Default.Payments, AcademicSuccess.copy(0.1f), AcademicSuccess)
            ActivityItem("Attendance submitted: Grade 10-A", "5 hours ago",
                Icons.AutoMirrored.Filled.FactCheck, AcademicWarning.copy(0.1f), AcademicWarning)
            ActivityItem("New Exam created: Mathematics", "6 hours ago", Icons.Default.Quiz, MaterialTheme.colorScheme.tertiaryContainer)
            ActivityItem("Leave approved: Sarah Parker", "8 hours ago", Icons.Default.DoneAll, AcademicSuccess.copy(0.1f), AcademicSuccess)
        }
    }
}

@Composable
private fun ActivityItem(text: String, time: String, icon: ImageVector, bgColor: Color, iconColor: Color = MaterialTheme.colorScheme.primary) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(modifier = Modifier.size(32.dp).background(bgColor, CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(16.dp))
        }
        Column {
            AppText(text, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            AppText(time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String, subValue: String, icon: ImageVector, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(36.dp).background(color.copy(0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            AppText(label.uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
            AppText(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            AppText(subValue, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun WidgetCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            AppText(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

// Chart Mocks
@Composable private fun BarChartMock() { Box(Modifier.fillMaxSize(0.8f).background(MaterialTheme.colorScheme.primary.copy(0.1f), RoundedCornerShape(8.dp))) { AppText("Bar Chart", Modifier.align(Alignment.Center)) } }
@Composable private fun DonutChartMock() { Box(Modifier.size(120.dp).border(8.dp, MaterialTheme.colorScheme.primary, CircleShape)) { AppText("58%", Modifier.align(Alignment.Center)) } }
@Composable private fun LineChartMock() { Box(Modifier.fillMaxSize(0.8f).background(MaterialTheme.colorScheme.tertiary.copy(0.1f), RoundedCornerShape(8.dp))) { AppText("Line Chart", Modifier.align(Alignment.Center)) } }
@Composable private fun AreaChartMock() { Box(Modifier.fillMaxSize(0.8f).background(AcademicSuccess.copy(0.1f), RoundedCornerShape(8.dp))) { AppText("Area Chart", Modifier.align(Alignment.Center)) } }
