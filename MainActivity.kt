package com.example.tarikhwalhandasa

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF101B32)
private val Panel = Color(0xFF1B2944)
private val Gold = Color(0xFFFFC857)
private val Cream = Color(0xFFFFF7E6)

data class Question(val category: String, val question: String, val answers: List<String>, val correct: Int, val explanation: String)

private val questions = listOf(
    Question("تاريخ", "أي نهر ارتبط بقيام الحضارة المصرية القديمة؟", listOf("دجلة", "النيل", "الفرات", "الأردن"), 1, "وفّر نهر النيل الماء والتربة الخصبة وطرق النقل."),
    Question("تاريخ", "ما اسم الكتابة المصوّرة عند المصريين القدماء؟", listOf("المسمارية", "الهيروغليفية", "اللاتينية", "الآرامية"), 1, "الهيروغليفية نظام كتابة استخدمه المصريون القدماء."),
    Question("تاريخ", "في أي مدينة يوجد الهرم الأكبر؟", listOf("الأقصر", "أسوان", "الجيزة", "الإسكندرية"), 2, "يقع الهرم الأكبر في هضبة الجيزة."),
    Question("تاريخ", "ما المادة التي استخدمها المصريون القدماء للكتابة وصنعوا منها لفائف؟", listOf("البردي", "الحرير", "الكتان", "الخشب"), 0, "صُنعت لفائف الكتابة من نبات البردي."),
    Question("تاريخ", "أي حضارة قديمة اشتهرت بالكتابة المسمارية في بلاد الرافدين؟", listOf("السومرية", "الرومانية", "المايا", "الإغريقية"), 0, "تُعد الكتابة المسمارية من أبرز إنجازات السومريين."),
    Question("هندسة", "كم ضلعًا للمثلث؟", listOf("2", "3", "4", "5"), 1, "المثلث شكل هندسي له ثلاثة أضلاع."),
    Question("هندسة", "مجموع زوايا المثلث في الهندسة الإقليدية يساوي؟", listOf("90°", "180°", "270°", "360°"), 1, "مجموع الزوايا الداخلية لأي مثلث مستوٍ يساوي 180 درجة."),
    Question("هندسة", "ما الشكل الذي له أربعة أضلاع متساوية وأربع زوايا قائمة؟", listOf("مستطيل", "معيّن فقط", "مربع", "شبه منحرف"), 2, "المربع يجمع بين تساوي الأضلاع والزوايا القائمة."),
    Question("هندسة", "إذا كان طول ضلع مربع 5 سم، فما محيطه؟", listOf("10 سم", "15 سم", "20 سم", "25 سم"), 2, "محيط المربع = 4 × طول الضلع = 20 سم."),
    Question("هندسة", "كم درجة قياس الزاوية القائمة؟", listOf("45°", "90°", "120°", "180°"), 1, "الزاوية القائمة قياسها 90 درجة."),
    Question("هندسة", "ما اسم الخط الذي يقسم الدائرة إلى نصفين ويمر بمركزها؟", listOf("وتر", "نصف القطر", "القطر", "قوس"), 2, "القطر وتر يمر بمركز الدائرة."),
    Question("تاريخ", "أي ملك مصري قديم يرتبط اسمه غالبًا بالهرم الأكبر؟", listOf("خوفو", "توت عنخ آمون", "أخناتون", "رمسيس الثالث"), 0, "ينسب الهرم الأكبر بالجيزة إلى الملك خوفو."),
    Question("هندسة", "مساحة مستطيل طوله 6 سم وعرضه 3 سم تساوي؟", listOf("9 سم²", "18 سم²", "12 سم²", "36 سم²"), 1, "المساحة = الطول × العرض = 18 سم²."),
    Question("تاريخ", "ما اسم الحجر الذي ساعد العلماء على قراءة الهيروغليفية؟", listOf("حجر رشيد", "حجر الصوان", "حجر الكعبة", "حجر الفلاسفة"), 0, "حجر رشيد حمل نصوصًا بثلاثة أنظمة كتابة، وساعد في فك رموز الهيروغليفية.")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("progress", Context.MODE_PRIVATE)
        setContent { GameApp(prefs.getInt("points", 0), prefs.getInt("played", 0)) { p, n ->
            prefs.edit().putInt("points", p).putInt("played", n).apply()
        } }
    }
}

@Composable
fun GameApp(initialPoints: Int, initialPlayed: Int, save: (Int, Int) -> Unit) {
    var screen by remember { mutableStateOf("home") }
    var mode by remember { mutableStateOf("مختلط") }
    var points by remember { mutableIntStateOf(initialPoints) }
    var played by remember { mutableIntStateOf(initialPlayed) }
    var round by remember { mutableStateOf(emptyList<Question>()) }
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(-1) }
    var answered by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    fun start(category: String) {
        mode = category
        val pool = if (category == "مختلط") questions else questions.filter { it.category == category }
        round = pool.shuffled().take(5)
        index = 0
        selected = -1
        answered = false
        score = 0
        screen = "quiz"
    }

    MaterialTheme(colorScheme = darkColorScheme(primary = Gold, background = Navy, surface = Panel)) {
        Column(Modifier.fillMaxSize().background(Navy).verticalScroll(rememberScrollState()).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("التاريخ والهندسة", color = Gold, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            Text("اختبر معلوماتك... وتحدَّ ذكاءك", color = Cream, fontSize = 14.sp)
            Spacer(Modifier.height(20.dp))
            when (screen) {
                "home" -> {
                    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp)) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("أهلًا بك في التحدّي!", color = Cream, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text("جولات قصيرة في التاريخ والهندسة مع تفسير للإجابة الصحيحة.", color = Cream, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(18.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                Stat("النقاط", points.toString())
                                Stat("الجولات", played.toString())
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    HomeButton("🏺 تحدّي التاريخ", "تعرّف على الحضارات والألغاز التاريخية") { start("تاريخ") }
                    HomeButton("📐 تحدّي الهندسة", "أشكال وزوايا ومسائل سريعة") { start("هندسة") }
                    HomeButton("⚡ تحدّي مختلط", "اختبر نفسك في المجالين") { start("مختلط") }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { screen = "about" }, modifier = Modifier.fillMaxWidth()) { Text("عن اللعبة والإنجازات", color = Gold) }
                }
                "quiz" -> {
                    if (round.isNotEmpty()) {
                        val q = round[index]
                        Text("الجولة: ${index + 1} من ${round.size}", color = Gold, fontSize = 16.sp)
                        Spacer(Modifier.height(12.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp)) {
                            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                                Text(q.category, color = Gold, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(12.dp))
                                Text(q.question, color = Cream, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(18.dp))
                                q.answers.forEachIndexed { i, answer ->
                                    Button(onClick = { if (!answered) { selected = i; answered = true; if (i == q.correct) { score += 10; points += 10; save(points, played) } } },
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = if (answered && i == q.correct) Color(0xFF247A55) else if (answered && i == selected) Color(0xFF8D3540) else Color(0xFF30405C))) {
                                        Text(answer, color = Color.White, textAlign = TextAlign.Center)
                                    }
                                }
                                if (answered) {
                                    Spacer(Modifier.height(10.dp))
                                    Text(if (selected == q.correct) "إجابة صحيحة! +10 نقاط" else "الإجابة الصحيحة: ${q.answers[q.correct]}", color = Gold, fontWeight = FontWeight.Bold)
                                    Text(q.explanation, color = Cream)
                                    Spacer(Modifier.height(10.dp))
                                    Button(onClick = {
                                        if (index < round.lastIndex) { index++; selected = -1; answered = false }
                                        else { played++; save(points, played); screen = "result" }
                                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
                                        Text(if (index < round.lastIndex) "السؤال التالي" else "عرض النتيجة", color = Navy)
                                    }
                                }
                            }
                        }
                        Text("نقاط الجولة: $score", color = Gold, modifier = Modifier.padding(12.dp))
                    }
                }
                "result" -> {
                    Text("أحسنت! انتهت الجولة", color = Gold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(14.dp))
                    Text("حققت $score نقطة في هذه الجولة.", color = Cream, fontSize = 20.sp)
                    Spacer(Modifier.height(18.dp))
                    Button(onClick = { screen = "home" }, modifier = Modifier.fillMaxWidth()) { Text("العودة للرئيسية") }
                    Button(onClick = { start(mode) }, modifier = Modifier.fillMaxWidth()) { Text("جولة جديدة") }
                }
                else -> {
                    Text("عن اللعبة", color = Gold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Text("لعبة أسئلة عربية تجمع بين التاريخ والهندسة. اجمع النقاط مع كل إجابة صحيحة، واقرأ التفسير بعد كل سؤال.", color = Cream, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Text("الإنجازات", color = Gold, fontSize = 20.sp)
                    Text("إجمالي النقاط: $points\nعدد الجولات المكتملة: $played", color = Cream, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(onClick = { screen = "home" }) { Text("رجوع للرئيسية", color = Gold) }
                }
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Gold, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Cream)
    }
}

@Composable
private fun ColumnScope.HomeButton(title: String, subtitle: String, action: () -> Unit) {
    Button(onClick = action, modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), colors = ButtonDefaults.buttonColors(containerColor = Panel), shape = RoundedCornerShape(16.dp), contentPadding = PaddingValues(16.dp)) {
        Column(Modifier.fillMaxWidth()) {
            Text(title, color = Gold, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Cream, fontSize = 13.sp)
        }
    }
}

