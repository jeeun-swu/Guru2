package com.example.Singsingbogam

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.*
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.pm.PackageManager
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth

@Suppress("NAME_SHADOWING")
class DdayListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var myHelper: RegActivity.myDBHelper
    private lateinit var sqlDB: SQLiteDatabase
    private lateinit var adapter: DdayListAdapter
    private val mAuth = FirebaseAuth.getInstance()

    // "이미 알림을 보냈는지"를 저장하는 플래그 (앱 실행 중 재료 디데이 메뉴로 들어간 최초 1회만 알림)
    companion object {
        var alreadyNotifiedOnce = false
    }

    // 알림 채널 ID
    private val CHANNEL_ID = "fridge_expiration_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dday_list)

        listView = findViewById(R.id.ddayListView)

        // DB Helper 불러오기
        myHelper = RegActivity.myDBHelper(this)

        // DB에서 데이터 가져오기
        val itemList = loadDataFromDB()

        // D-day 계산 및 정렬 (남은 일수 오름차순)
        val sortedList = itemList.sortedWith(compareBy { it.daysLeft })

        // 어댑터 세팅
        adapter = DdayListAdapter(this, sortedList)
        listView.adapter = adapter

        // 알림 채널 생성 (오레오 이상)
        createNotificationChannel()

        // 유통기한이 1일 남은 식재료가 있다면 알림 띄우기 (단, 처음 1회만)
        checkAndNotify(sortedList)
    }

    //DB에서 fridgeTBL(fName, fDate) 읽어서 실제 일(day) 차이로 D-Day 계산
    private fun loadDataFromDB(): List<DdayItem> {
        val dataList = mutableListOf<DdayItem>()
        sqlDB = myHelper.readableDatabase

        // SELECT * FROM fridgeTBL
        val cursor = sqlDB.rawQuery("SELECT * FROM fridgeTBL;", null)

        // 날짜 포맷 (예: "20250306")
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        // 오늘 날짜를 자정(0시)으로 맞춘 Calendar
        val todayCal = Calendar.getInstance().apply {
            time = Date()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        while (cursor.moveToNext()) {
            // cursor에서 fName, fDate(YYYYMMDD 형태 int) 가져오기
            val name = cursor.getString(0)
            val dateInt = cursor.getInt(1)

            // fDate(예: 20250306)를 문자열로 바꿔 파싱
            val dateStr = dateInt.toString()
            val parsedDate = sdf.parse(dateStr) ?: continue

            // 유통기한 날짜를 자정으로 맞춘 Calendar
            val expCal = Calendar.getInstance().apply {
                time = parsedDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // (유통기한 - 오늘) / (24*60*60*1000) → 일(day) 단위
            val diffMillis = expCal.timeInMillis - todayCal.timeInMillis
            val daysLeft = (diffMillis / (24L * 60 * 60 * 1000)).toInt()

            // D-day 문자열 구성
            val ddayString = when {
                daysLeft > 0 -> "D-$daysLeft"
                daysLeft == 0 -> "D-Day"
                else -> "D+${-daysLeft}" // 이미 지남
            }

            dataList.add(DdayItem(name, dateInt, daysLeft, ddayString))
        }

        cursor.close()
        sqlDB.close()

        return dataList
    }

    // 알림 채널 생성
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Expiration Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "유통기한 임박 재료 알림 채널입니다."
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 유통기한이 1일 남은 식재료 알림 (처음 1회만)
     * - 여러 개면: "(첫 재료) 외 n개의 유통기한이 하루 남았습니다!"
     * - 하나면: "(재료명)의 유통기한이 하루 남았습니다!"
     */
    private fun checkAndNotify(items: List<DdayItem>) {
        if (alreadyNotifiedOnce) return

        val almostExpiredItems = items.filter { it.daysLeft == 1 }
        if (almostExpiredItems.isNotEmpty()) {
            val count = almostExpiredItems.size
            val firstName = almostExpiredItems[0].name

            val contentText = if (count == 1) {
                "$firstName 의 유통기한이 하루 남았습니다!"
            } else {
                "$firstName 외 ${count - 1}개의 유통기한이 하루 남았습니다!"
            }

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("유통기한 임박 알림")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            // 알림 클릭 시 DdayListActivity 열기
            val intent = Intent(this, DdayListActivity::class.java)
            val pendingIntent = TaskStackBuilder.create(this).run {
                addNextIntentWithParentStack(intent)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }
            builder.setContentIntent(pendingIntent)

            // 알림 권한 체크 & 발행
            with(NotificationManagerCompat.from(this)) {
                if (ActivityCompat.checkSelfPermission(
                        this@DdayListActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                notify(1001, builder.build())
            }
            // 이후 알림 안 띄우도록 플래그 세팅
            alreadyNotifiedOnce = true
        }
    }

    // 메뉴 생성
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_reg -> {
                startActivity(Intent(this, RegActivity::class.java))
                return true
            }
            R.id.menu_dday -> {
                startActivity(Intent(this, DdayListActivity::class.java))
                return true
            }
            R.id.menu_community -> {
                startActivity(Intent(this, PostViewActivity::class.java))
                return true
            }
            R.id.menu_logout -> {
                // 로그아웃
                mAuth.signOut()
                // LoginActivity로 이동
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

// D-day 표시용 데이터 클래스
data class DdayItem(
    val name: String,    // 재료명
    val dateInt: Int,    // YYYYMMDD
    val daysLeft: Int,   // 남은 일수
    val ddayString: String
)

