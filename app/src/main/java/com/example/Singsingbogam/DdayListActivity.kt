package com.example.Singsingbogam

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.*
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.pm.PackageManager
import com.google.firebase.auth.FirebaseAuth

@Suppress("NAME_SHADOWING")
class DdayListActivity : AppCompatActivity() {

    private lateinit var dbManager: DBManager
    private lateinit var sqllitedb: SQLiteDatabase

    private lateinit var listView: ListView
    private lateinit var adapter: DdayListAdapter
    private val mAuth = FirebaseAuth.getInstance()

    // 알림 채널 ID
    private val CHANNEL_ID = "fridge_expiration_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dday_list)

        // (1) 안드로이드 13 알림 권한 요청
        requestNotificationPermissionIfNeeded()

        listView = findViewById(R.id.ddayListView)

        // (2) DBManager로 DB 열기
        dbManager = DBManager(this, "fridgeDB", null, 1)
        sqllitedb = dbManager.readableDatabase

        // (3) DB에서 데이터 로드 + D-Day 계산
        val itemList = loadDataFromDB()
        val sortedList = itemList.sortedBy { it.daysLeft }

        // (4) 리스트뷰 어댑터
        adapter = DdayListAdapter(this, sortedList)
        listView.adapter = adapter

        // (5) 알림 채널 생성
        createNotificationChannel()

        // (6) 유통기한 1일 남은 식재료 알림
        checkAndNotify(sortedList)
    }

    /**
     * 안드로이드 13(API 33) 이상에서 알림 권한 요청
     */
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 9999)
            }
        }
    }

    // (선택) 권한 요청 결과 확인
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 9999) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 알림 권한 승인됨
            } else {
                // 거부됨
            }
        }
    }

    /**
     * DB에서 fridgeTBL(fName, fDate) 읽어서 D-Day 계산
     * - 날짜를 자정(0시)으로 통일
     * - 단순히 (expCal - todayCal)/24h 로 'floor' (소수점 버림) 계산
     */
    private fun loadDataFromDB(): List<DdayItem> {
        val dataList = mutableListOf<DdayItem>()

        val cursor: Cursor = sqllitedb.rawQuery("SELECT * FROM fridgeTBL;", null)

        // 날짜 포맷 (yyyyMMdd)
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        // 오늘(자정)
        val todayCal = Calendar.getInstance().apply {
            time = Date()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        while (cursor.moveToNext()) {
            val nameCol = cursor.getColumnIndex("fName")
            val dateCol = cursor.getColumnIndex("fDate")
            if (nameCol == -1 || dateCol == -1) continue

            val name = cursor.getString(nameCol)
            val expDateInt = cursor.getInt(dateCol) // YYYYMMDD
            val expDateStr = expDateInt.toString()

            // 유통기한 날짜 Calendar(자정)
            val parsedDate = sdf.parse(expDateStr) ?: continue
            val expCal = Calendar.getInstance().apply {
                time = parsedDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // 일수 차이 (floor)
            val diffMillis = expCal.timeInMillis - todayCal.timeInMillis
            val daysLeft = (diffMillis / (24L * 60 * 60 * 1000)).toInt()
            // 2025-01-24(오늘) vs 2025-01-23 => daysLeft = -1 => D+1
            // 2025-01-24(오늘) vs 2025-01-24 => daysLeft = 0  => D-Day
            // 2025-01-24(오늘) vs 2025-01-25 => daysLeft = 1  => D-1

            // D-Day 문자열
            val ddayString = when {
                daysLeft > 0 -> "D-$daysLeft"
                daysLeft == 0 -> "D-Day"
                else -> "D+${-daysLeft}" // 과거
            }

            dataList.add(DdayItem(name, expDateInt, daysLeft, ddayString))
        }
        cursor.close()
        return dataList
    }

    /**
     * 알림 채널 생성 (오레오 이상)
     */
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
     * 유통기한 1일 남은 식재료가 있다면 알림 표시
     * ex) "{사과}의 유통기한이 하루 남았습니다!"
     */
    private fun checkAndNotify(items: List<DdayItem>) {
        val almostExpiredItems = items.filter { it.daysLeft == 1 }
        if (almostExpiredItems.isNotEmpty()) {
            val firstItemName = almostExpiredItems[0].name
            val contentText = "${firstItemName}의 유통기한이 하루 남았습니다!"

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("유통기한 임박 알림")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            // 알림 클릭 시 DdayListActivity로 이동
            val intent = Intent(this, DdayListActivity::class.java)
            val pendingIntent = TaskStackBuilder.create(this).run {
                addNextIntentWithParentStack(intent)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }
            builder.setContentIntent(pendingIntent)

            with(NotificationManagerCompat.from(this)) {
                // 안드로이드 13 이상에서 권한 거부 시 알림 X
                if (ActivityCompat.checkSelfPermission(
                        this@DdayListActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                notify(1001, builder.build())
            }
        }
    }

    /**
     * 옵션 메뉴
     */
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
                // 로그아웃 처리
                mAuth.signOut()

                // LoginActivity로 이동
                val intent = Intent(this, LoginActivity ::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish() // 현재 Activity 종료
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 액티비티 종료 시 DB 닫기
     */
    override fun onDestroy() {
        super.onDestroy()
        sqllitedb.close()
        dbManager.close()
    }
}

// D-day 표시용 데이터 클래스
data class DdayItem(
    val name: String,     // 재료명
    val dateInt: Int,     // 유통기한(YYYYMMDD)
    val daysLeft: Int,    // 남은 일수(정수)
    val ddayString: String
)