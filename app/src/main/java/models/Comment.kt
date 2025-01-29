package models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// ✅ Firestore에 저장되는 "댓글" 데이터 모델
data class Comment(
    var commentId: String = "",   // 🔥 댓글의 고유 ID (Firestore 문서 ID)
    var postId: String = "",      // 🔥 댓글이 속한 게시글 ID
    var author: String? = null,   // 🔥 댓글 작성자 이메일 (nullable 허용)
    var content: String? = null,  // 🔥 댓글 내용 (nullable 허용)
    @ServerTimestamp var timestamp: Date? = null // 🔥 Firestore에서 자동 생성되는 타임스탬프
) {
    // ✅ Firestore에서 객체 변환 시 필요! (기본 생성자 추가)
    constructor() : this("", "", null, null, null)
}
