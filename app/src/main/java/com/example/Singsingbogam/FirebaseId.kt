package com.example.Singsingbogam

// ✅ Firestore에서 사용되는 컬렉션 및 필드명을 정의하는 객체
object FirebaseId {
    // 🔥 Firestore 컬렉션 이름
    var user: String = "user"    // 사용자 정보 저장 컬렉션
    var post: String = "post"    // 게시글 저장 컬렉션

    // 🔥 Firestore 필드 키 (게시글 관련)
    var documentId: String = "documentId"  // 문서 고유 ID
    var title: String = "title"            // 게시글 제목
    var contents: String = "contents"      // 게시글 내용
    var author: String = "author"          // 게시글 작성자 (이메일 앞부분)
    var fullAuthor: String = "fullAuthor"  // 게시글 작성자 전체 이메일
    var timestamp: String = "timestamp"    // 게시글 작성 시간

    // 🔥 Firestore 필드 키 (사용자 정보 관련)
    var Id: String = "Id"                  // 사용자 ID (이메일)
    var Password: String = "Password"      // 사용자 비밀번호 (암호화 필요)
}
