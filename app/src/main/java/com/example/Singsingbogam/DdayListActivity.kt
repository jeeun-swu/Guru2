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

@Suppress("NAME_SHADOWING")
class DdayListActivity : AppCompatActivity() {

    private lateinit var dbManager: DBManager
    private lateinit var sqllitedb: SQLiteDatabase

    private lateinit var listView: ListView
    private lateinit var adapter: DdayListAdapter

    private val CHANNEL_ID = "fridge_expiration_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dday_list)

        // 1) 안드로이드 13 알림 권한 요청
        requestNotificationPermissionIfNeeded()

        listView = findViewById(R.id.ddayListView)

        // DB 열기
        dbManager = DBManager(this, "fridgeDB", null, 1)
        sqllitedb = dbManager.readableDatabase

        // DB에서 데이터 로드
        val itemList = loadDataFromDB()
        val sortedList = itemList.sortedBy { it.daysLeft }

        adapter = DdayListAdapter(this, sortedList)
        listView.adapter = adapter

        // 알림 채널 생성
        createNotificationChannel()

        // 유통기한 1일 남은 재료 알림
        checkAndNotify(sortedList)
    }

    // -----------------------
    // 권한 요청 함수 추가
    // -----------------------
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 9999)
            }
        }
    }

    // 선택사항: 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 9999) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 알림 권한 승인됨
            } else {
                // 거부됨
            }
        }
    }

    private fun loadDataFromDB(): List<DdayItem> {
        val dataList = mutableListOf<DdayItem>()
        val cursor: Cursor = sqllitedb.rawQuery("SELECT * FROM fridgeTBL;", null)

        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
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
            val expDateInt = cursor.getInt(dateCol)
            val expDateStr = expDateInt.toString()

            val parsedDate = sdf.parse(expDateStr) ?: continue
            val expCal = Calendar.getInstance().apply {
                time = parsedDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val diffMillis = expCal.timeInMillis - todayCal.timeInMillis
            // +12h 반올림 방식(필요하다면)
            val daysLeft = ((diffMillis + 12L * 3600000) / 86400000).toInt()

            val ddayString = when {
                daysLeft > 0 -> "D-$daysLeft"
                daysLeft == 0 -> "D-Day"
                else -> "D+${-daysLeft}"
            }
            dataList.add(DdayItem(name, expDateInt, daysLeft, ddayString))
        }
        cursor.close()
        return dataList
    }

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

            val intent = Intent(this, DdayListActivity::class.java)
            val pendingIntent = TaskStackBuilder.create(this).run {
                addNextIntentWithParentStack(intent)
                // or FLAG_UPDATE_CURRENT 만 써도 됨,
                // Android 12 이상은 FLAG_IMMUTABLE 권장
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }
            builder.setContentIntent(pendingIntent)

            with(NotificationManagerCompat.from(this)) {
                // 만약 안드로이드 13이고 사용자 거부 → return
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
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        sqllitedb.close()
        dbManager.close()
    }
}

data class DdayItem(
    val name: String,
    val dateInt: Int,
    val daysLeft: Int,
    val ddayString: String
)



