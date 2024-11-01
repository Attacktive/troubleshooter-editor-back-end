package com.github.attacktive.troubleshootereditor.sqlite

import java.io.File
import kotlin.test.Test
import com.github.attacktive.troubleshootereditor.sqlite.adapter.inbound.SqliteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SqliteServiceTest(@Autowired private val sqliteService: SqliteService) {
	@Test
	fun testBasic() {
		val url = SqliteServiceTest::class.java.classLoader.getResource("game.sav")
		val file = File(url!!.toURI())
		val saveData = sqliteService.readFile(file)
		println(saveData)
	}
}
