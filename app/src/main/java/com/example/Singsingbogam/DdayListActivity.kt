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
import android.view.MenuItem
import androidx.core.app.ActivityCompat

@Suppress("NAME_SHADOWING")
class DdayListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var myHelper: RegActivity.myDBHelper
    private lateinit var sqlDB: SQLiteDatabase
    private lateinit var adapter: DdayListAdapter

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

        // D-day 계산 및 정렬
        // 디데이가 임박한 순으로 정렬: 남은 일수가 작은 순(0->1->2...)
        // '남은 일수 오름차순'으로만 정렬
        val sortedList = itemList.sortedWith(compareBy { it.daysLeft })

        // 어댑터 세팅
        adapter = DdayListAdapter(this, sortedList)
        listView.adapter = adapter

        // 알림 채널 생성 (오레오 이상)
        createNotificationChannel()

        // 유통기한이 1일 남은 식재료가 있다면 알림 띄우기
        checkAndNotify(sortedList)
    }

    //DB에서 fridgeTBL 데이터를 읽어와서 DdayItem 리스트로 만들어 준다.
    private fun loadDataFromDB(): List<DdayItem> {
        val dataList = mutableListOf<DdayItem>()
        sqlDB = myHelper.readableDatabase

        // fName(재료명), fDate(YYYYMMDD 형태 정수)
        val cursor = sqlDB.rawQuery("SELECT * FROM fridgeTBL", null)

        // 오늘 날짜를 YYYYMMDD 형태의 Int로 구하기
        val todayInt = getTodayInt()

        while (cursor.moveToNext()) {
            val name = cursor.getString(0)  // fName
            val dateInt = cursor.getInt(1)  // fDate

            // 남은 일수 = (유통기한 - 오늘날짜)
            val daysLeft = dateInt - todayInt

            // D-day 문자열 구성
            val ddayString = when {
                daysLeft > 0 -> "D-${daysLeft}"
                daysLeft == 0 -> "D-Day"
                else -> "D+${-daysLeft}"  // 이미 지남
            }

            dataList.add(DdayItem(name, dateInt, daysLeft, ddayString))
        }
        cursor.close()
        sqlDB.close()

        return dataList
    }

    //오늘 날짜를 YYYYMMDD 형태의 Int로 반환

    private fun getTodayInt(): Int {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val todayString = sdf.format(Date())
        return todayString.toInt()
    }

    //알림 채널 생성
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

    //유통기한이 1일 남은 식재료가 있다면 푸시 알림을 띄움

    private fun checkAndNotify(items: List<DdayItem>) {
        val almostExpiredItems = items.filter { it.daysLeft == 1 }

        if (almostExpiredItems.isNotEmpty()) {
            // 첫 번째 아이템 이름만
            val firstItemName = almostExpiredItems[0].name
            // 원하는 메시지 형식: "{재료 이름}의 유통기한이 하루 남았습니다!"
            val contentText = "${firstItemName}의 유통기한이 하루 남았습니다!"

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("유통기한 임박 알림")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            // 알림 클릭 시 앱 열기
            val intent = Intent(this, DdayListActivity::class.java)
            val pendingIntent = TaskStackBuilder.create(this).run {
                addNextIntentWithParentStack(intent)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            builder.setContentIntent(pendingIntent)

            // 권한 체크 & 알림 발행
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
        }
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
}

//D-day 표시용 데이터 클래스
data class DdayItem(
    val name: String,      // 재료명
    val dateInt: Int,      // 유통기한(YYYYMMDD 정수)
    val daysLeft: Int,     // 남은 일수
    val ddayString: String // "D-3" / "D-Day" / "D+2" 등
)