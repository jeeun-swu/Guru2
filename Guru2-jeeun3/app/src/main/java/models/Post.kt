package models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

class Post {
    var documentId: String? = null
    @kotlin.jvm.JvmField
    var title: String? = null
    var contents: String? = null

    @ServerTimestamp
    var date: Date? = null

    constructor()

    constructor(documentId: String?, title: String?, contents: String?) {
        this.documentId = documentId
        this.title = title
        this.contents = contents
    }

    override fun toString(): String {
        return "Post{" +
                "documentId='" + documentId + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", date=" + date +
                '}'
    }
}
