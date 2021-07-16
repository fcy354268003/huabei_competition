package com.example.huabei_competition.network

/**
0000		一切正常
1000       	用户密码错误
1001		token错误
1002		操作失败
1003		用户注册失败
2000       	用户操作异常
2001		表单参数错误

{
"code" : "code",
"message" : "message",
"data" : {
"token" : "token"
}
}
 */
class ApiResponse<T> {
    var code: String = "1002"
    var message: String = ""
    var data: T? = null

}