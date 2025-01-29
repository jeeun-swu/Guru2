package models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

class Post {
    var documentId: String? = null  // 🔥 게시글 고유 ID (Firestore Document ID)
    var title: String? = null  // 🔥 게시글 제목
    var contents: String? = null  // 🔥 게시글 내용
    var author: String? = null  // 🔥 작성자 이메일 앞부분 (닉네임처럼 활용)
    var fullAuthor: String? = null  // 🔥 작성자 이메일 전체
    var timestamp: String? = null  // 🔥 작성 날짜 (문자열로 저장)

    @ServerTimestamp
    var date: Date? = null  // 🔥 Firestore에서 자동 생성되는 서버 타임스탬프

    // 🔥 기본 생성자 (Firestore에서 데이터를 자동으로 매핑하려면 필수)
    constructor()

    // 🔥 데이터를 초기화하는 생성자
    constructor(
        documentId: String?,
        title: String?,
        contents: String?,
        author: String?,
        fullAuthor: String?,
        dateString: String?
    ) {
        this.documentId = documentId
        this.title = title
        this.contents = contents
        this.author = author
        this.fullAuthor = fullAuthor
        this.timestamp = dateString
    }

    // 🔥 toString() 오버라이드 (디버깅 및 로그 확인용)
    override fun toString(): String {
        return "Post{" +
                "documentId='" + documentId + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", author='" + author + '\'' +
                ", date=" + date +
                '}'
    }
}
